package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * While(condition)
 * 		{instructions}
 * Loop in a list of instruction when the condition is true
 * @author gl07
 * @date 10/01/2020
 */

public class While extends AbstractInst {
	
	/**Condition is an expression which returns only boolean */
    private AbstractExpr condition;
    /**Body is a list of instruction to be executed if the condition is true*/
    private ListInst body;

    /**
     * Return the expression of the condition
     * @return AbstractExpr
     */
    public AbstractExpr getCondition() {
        return condition;
    }

    /**
     * Return the list of instructions
     * @return ListInst
     */
    public ListInst getBody() {
        return body;
    }

    /**
     * Constructor of class , assign condition and body to
     * private attributes 
     * @param AbstractExpr, ListInst 
     */
    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
    	GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
    	int i = compiler.getIndice();
        Label debut = new Label("E_debut."+i);
        Label cond = new Label("E_cond."+i);
        compiler.setLabel(i,debut);
        compiler.addInstruction(new BRA(cond));
        compiler.addLabel(debut);
        this.body.codeGenListInst(compiler);
        compiler.addLabel(cond);
        this.condition.codeGenExpr(compiler,compiler.getGBmanager());
        compiler.addInstruction(new CMP(1,Register.getR(compiler.getGBmanager().getValGB())));
        compiler.addInstruction(new BEQ(debut));
        GB.setGBmanager(table);
    }
    

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
    	this.condition.verifyCondition(compiler, localEnv, currentClass);
    	this.getBody().verifyListInst(compiler, localEnv, currentClass, returnType);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
