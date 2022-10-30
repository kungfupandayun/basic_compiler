package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Main program  with list of instructions and list of variables
 * @author gl07
 * @date 01/01/2020
 */
public class Main extends AbstractMain {

	/**Log to debug*/
    private static final Logger LOG = Logger.getLogger(Main.class);

    /**A list of variables' declarations before the instructions*/
    private ListDeclVar declVariables;
    /**A list of instructions to be performed */
    private ListInst insts;

    /**
     * Representation of a main program
     * Start from declaration of variables and then the list of instructions
     * @param declVariables
     * @param insts
     */
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    /** @return the list of variables' declarations*/
    public ListDeclVar getDeclVariables(){
      return this.declVariables;
    }

    /** @return the list of instructions in the main program*/
    public ListInst getInsts(){
      return this.insts;
    }

    ///les param, void etc.
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // initilisation de l'environment EXP 
        EnvironmentExp envExp =  new EnvironmentExp();

        //appel de verify ListDeclVar
        this.getDeclVariables().verifyListDeclVariable(compiler,envExp,null);

        // void Type correspond au type de retour vide en cas de main
        Type voidType= new VoidType(envExp.getSymbolTab().create("void" ));

        //appel de verify verifyListInst
        this.getInsts().verifyListInst(compiler, envExp,null,voidType);
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {

        compiler.addComment("Variables declarations:");
        declVariables.codeGenListVar(compiler);
        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }


    /**A ajouter*/
    public int valSP(){
        return declVariables.valSP();
    }

    public int varGlobale(){
        return declVariables.size();
    }
}
