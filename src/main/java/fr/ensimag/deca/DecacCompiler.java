package fr.ensimag.deca;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractDeclClass;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

import fr.ensimag.deca.codegen.ErrorEtiquette;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class DecacCompiler implements  Callable<Boolean> {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
//    private EnvironmentExp environmentExp;
    private EnvironmentType environmentType;

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addErrorInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addErrorInstruction(ErrorEtiquette errorList){
        program.addErrorInstruction(errorList);
      }
    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();


    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = null;
        destFile=sourceFile.substring(0,sourceFile.lastIndexOf(".deca")).concat(".ass");
        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }

        if (this.getCompilerOptions().isParsed()) {
        	PrintStream p=new PrintStream(System.out);
        	IndentPrintStream id= new IndentPrintStream(p);
        	prog.decompile(id);
        	return false;
        }
        // verification contextuelle
        assert(prog.checkAllLocations());
        prog.verifyProgram(this);

        if(this.compilerOptions.getVerification()){
          // arrete decac apres l'étape de vérification
          return false;
        }

        assert(prog.checkAllDecorations());
        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");

        //la gestion des etiquettes des erreurs
        this.addLabel(new Label("arithmetic_overflow"));
        this.addErrorInstruction(new ErrorEtiquette("Arithmetic overflow"));
        this.addLabel(new Label("stack_overflow_error"));
        this.addErrorInstruction(new ErrorEtiquette("Stack overflow"));
        this.addLabel(new Label("heap_overflow"));
        this.addErrorInstruction(new ErrorEtiquette("Heap overflow"));
        this.addLabel(new Label("division_zero"));
        this.addErrorInstruction(new ErrorEtiquette("Diviseur ne doit pas egal 0"));
        this.addLabel(new Label("varaible_non_defini"));
        this.addErrorInstruction(new ErrorEtiquette("Variable n'est pas défini"));
        this.addLabel(new Label("dereferencement.null"));
        this.addErrorInstruction(new ErrorEtiquette("Error : dereferencing null pointer"));
        //les deux lignes ajouté par rapport rendu final
        this.addLabel(new Label("io_error"));
        this.addErrorInstruction(new ErrorEtiquette("Error : error de in/out"));

        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
          LOG.debug("Compiktation : lexer");
        	 lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }

//	public EnvironmentExp getEnvironmentExp() {
//		return environmentExp;
//	}
//
//	public void setEnvironmentExp(EnvironmentExp environmentExp) {
//		LOG.debug("Initialisation de l'environment Exp");
//		this.environmentExp = environmentExp;
//	}

	public EnvironmentType getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(EnvironmentType environmentType) {
		LOG.debug("Initialisation de l'environment Types ");
		this.environmentType = environmentType;
	}
/*La partie pour gestion de GB manager*/
	private GBmanager GB;
  /**
   * Initialisation d'un GB manager pour compiler
   *
   * @param SP nombre de register à reserver
   */
	public void initGBmanager(int SP, int RMAX) {
		this.GB = new GBmanager(SP,RMAX);
	}
  /**
   * Get le GB manager
   *
   * @return GBmanager
   */
	public GBmanager getGBmanager() {
		return this.GB;
	}

/*La partie pour gerer les label principalement pour IfThenElse et WHILE*/
	private ArrayList<Label> label;
	private boolean initlabel = false;
  /**
   * Veirifer si le list label est bien initialisé
   *
   * @return true or false
   */
	public boolean getinit() {
		return this.initlabel;
	}
  /**
   * get le (i-1)ème label de la list label
   *
   * @param i indice of the label
   *
   * @return label
   */
	public Label getLabel(int i) {
    //Parce que la list commence par indice 0 mais on veut l'indice de label commence par 1
		return this.label.get(i-1);
	}
  /**
   * Set le (i-1)ème label de la list
   *
   * @param i indice of the label
   * @param label label à ajouter dans la list
   */
	public void setLabel(int i, Label label) {
		if(this.label.size()<i) {//Veirifier si (i-1)ème label est deja ajouté
			this.label.add(label);
		}else {
			this.label.set(i-1, label);
		}
	}
  /**
   * Initialisation de la list label
   */
	public void initLabel() {
		this.label = new ArrayList<Label>();
		this.initlabel = true;
	}
  /**
   * Reset de la list label
   */
	public void resetLabel() {
		int i=0;
		while(i< this.label.size() && this.label.get(i)!=null) {
			this.label.set(i, null);
		}
	}
  /**
   * Recuperer l'indice de label que nous avons utiliser
   *
   * @return indice
   */
	public int getIndice() {
		int i = 1;
		while (i < this.label.size()+1 && this.label.get(i-1) != null) {
			i++ ;
		}
		return i;
	}
/*Partie pour cas vrai de ifthenelse*/
	private Label instrcution;
  /**
   * Set l'instruction de IfThenElse
   *
   * @param label
   */
	public void setInstrcution(Label label) {
		this.instrcution = label;
	}
  /**
   * Recuperer l'instruction de IfThenElse
   *
   * @return instruction
   */
	public Label getInstruction() {
		return this.instrcution;
	}
/*Partie pour cas false de while*/
	private Label end;
  /**
   * Set l'instruction de IfThenElse
   *
   * @param label
   */
	public void setEnd(Label label) {
		this.end = label;
	}
  /**
   * Recuperer l'instruction de IfThenElse
   *
   * @return instruction
   */
	public Label getEnd() {
		return this.end;
	}
/*Partie pour Enregistre de variables déclaré*/
	private ArrayList<DAddr> list_addr = new ArrayList<DAddr>();
  /**
   * Ajouter un var dans la list
   *
   * @param addr
   */
	public void setAddr(DAddr addr) {
		if(!list_addr.contains(addr)) {
			list_addr.add(addr);
		}
	}
  /**
   * Return si var est deja initialisé
   *
   * @param addr
   * @return true or false
   */
	public boolean getAddr(DAddr addr) {
		return list_addr.contains(addr);
	}
	
	int LB = 0;
	public void initLB() {
		this.LB = 1;
	}
	
	public int getLB() {
		return this.LB;
	}
	
	public void addLB() {
		this.LB++;
	}

	DAddr addrObject;
	public void setAddrObjet(DAddr addr) {
		this.addrObject = addr;
	}
	public DAddr getAddrObjet() {
		return this.addrObject;
	}
	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		return this.compile();
	}
	private int nombrefiled;
	public int getnbrfield() {
		return this.nombrefiled;
	}
	public void setnbrfield(int nbr) {
		this.nombrefiled = nbr;
	}
}
