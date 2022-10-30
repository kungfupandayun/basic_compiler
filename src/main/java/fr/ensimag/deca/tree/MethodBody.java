 package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl07
 * @date 17/01/2020
 */
public class MethodBody extends AbstractMethodBody {
    private static final Logger LOG = Logger.getLogger(MethodBody.class);

    private ListDeclVar declVariables;
    private ListInst insts;
    public MethodBody(ListDeclVar declVariables, ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListDeclVar getDeclVariables(){
      return this.declVariables;
    }
    
    
    public ListInst getInsts(){
      return this.insts;
    }

    @Override
    protected void verifyMethodBody (DecacCompiler compiler, EnvironmentExp envParam, ClassDefinition currentClass, Type returnType)
    		throws ContextualError{
    	LOG.debug("verifyMethodeBody : start");
    	//appel de verify ListDeclVar
        this.getDeclVariables().verifyListDeclVariable(compiler,envParam,currentClass);
        //appel de verify verifyListInst
        envParam.setParentEnvironment(currentClass.getMembers()); // empiler l'environment des param sur l'environment de la classe
        this.getInsts().verifyListInst(compiler, envParam,currentClass,returnType);
        LOG.debug("verifyMethodeBody : End");
    }

    @Override
    protected void codeGenMethod(DecacCompiler compiler) {
        compiler.addComment("Variables declarations:");
        declVariables.codeGenListVarMethod(compiler);
        compiler.addComment("Beginning of methode instructions:");
        insts.codeGenListInst(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	s.println(" { ");
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

    public int valSP(){
        return declVariables.valSP();
    }
}
