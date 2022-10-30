package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractMain;

import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.codegen.SPmanager;;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl07
 * @date 01/01/2020
 */
public class Program extends AbstractProgram {

	/** A log to debug*/
    private static final Logger LOG = Logger.getLogger(Program.class);
    /** A private list to save the classes declared*/
    private ListDeclClass classes;
    /** A private attribute to save the main program*/
    private AbstractMain main;

    /**Constructor of a program
     * @param a list of classes
     * @param a main program
     * Classes must be declared before the main program
     * Assign the list of class and main program to the private attributes
     */
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }

    /**
     * @return the list of class
     */
    public ListDeclClass getClasses() {
        return classes;
    }

    /**
     * @return the main program
     */
    public AbstractMain getMain() {
        return main;
    }



    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        // passe 1
        LOG.debug("verify program: start");
        //initialiser les envionments
        EnvironmentType envType = new EnvironmentType();
        compiler.setEnvironmentType(envType);
        // passe 1 les vérificationde classe ajoutes tous les types de classes
        this.getClasses().verifyListClass(compiler);
        this.getClasses().verifyListClassMembers(compiler);
        this.getClasses().verifyListClassBody(compiler);
        //throw new UnsupportedOperationException("not yet implemented");
        
        this.getMain().verifyMain(compiler);
        LOG.debug("verify program: end");

    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        SPmanager SP = new SPmanager();
        //init
        compiler.initLabel();
        compiler.addInstruction(new TSTO(SP.valSP(this) + SP.valSPclasses(this) + SP.nbrVarGlobale(this)));
        compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
        //ADDSP = tables des méthodes + variables globales.
        compiler.addInstruction(new ADDSP(SP.valSPclasses(this) + SP.nbrVarGlobale(this)));
        //Construction de la table des méthodes
        compiler.addComment("La table des Méthode de Object");
        //tab de methodes ne doit pas etre a la fin?
         if (compiler.getCompilerOptions().isRegister()) {
         	 compiler.initGBmanager(SP.getvalSP(),compiler.getCompilerOptions().getNbReg());
         } else {
         	compiler.initGBmanager(SP.getvalSP(),16);
         }
         RegisterOffset addr = new RegisterOffset(compiler.getGBmanager().getGB(), Register.GB);
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0,addr));
        compiler.getGBmanager().addGB();
        compiler.setAddrObjet(addr);
        //CODE.Objets.equals
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(compiler.getGBmanager().getGB(),Register.GB)));
        compiler.getGBmanager().addGB();

        //Initialiser les classes
        for(AbstractDeclClass c: classes.getList()){
            c.codeGenInitDeclClass(compiler);
        }
        //Main
        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        //les instructions des methodes
        compiler.addLabel(new Label("init.Object"));
        compiler.addInstruction(new RTS());
        compiler.addLabel(new Label("code.Object.equals"));
        //Object.equals

        for(AbstractDeclClass c: classes.getList()){
            c.codeGenFieldClass(compiler);
            c.codeGenMethodClass(compiler);   
        }
        compiler.resetLabel();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
