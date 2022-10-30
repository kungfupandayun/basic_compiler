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

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class IfThenElse extends AbstractInst {

    private final AbstractExpr condition;
    private final ListInst thenBranch;
    private ListInst elseBranch;

    /**
     * Store a condition , a list of instruction for then branch
     * and a list of instruction for else branch
     * @param condition
     * @param thenBranch
     * @param elseBranch
     */
    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    
    /**
     * Décorer la condition et les deux branche
     */
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
    	this.condition.verifyCondition(compiler, localEnv, currentClass);
    	this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    	this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
    	// voir si le GB est remplie ou non
    	GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();

    	int i = compiler.getIndice();
        Label sinon = new Label("E_sinon."+i);
        Label endIf = new Label("E_end."+i);
        Label instruction = new Label("E_inst."+i);
        compiler.setLabel(i, sinon);
        compiler.setInstrcution(instruction);
        this.condition.codeGenExpr(compiler, compiler.getGBmanager());
        compiler.addInstruction(new CMP(0,Register.getR(compiler.getGBmanager().getValGB())));
        compiler.addInstruction(new BEQ(sinon));
        // Instructions
        compiler.addComment("Instructions");
        compiler.addLabel(instruction);
        this.thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(endIf));
        compiler.addLabel(sinon);
        this.elseBranch.codeGenListInst(compiler);
        compiler.addLabel(endIf);
    	GB.setGBmanager(table);
        }

    @Override
    public void decompile(IndentPrintStream s) {
    	 s.print("if (");
         this.condition.decompile(s);
         s.println(") {");
         s.indent();
         this.thenBranch.decompile(s);
         s.unindent();
         s.println("} else {");
         s.indent();
         this.elseBranch.decompile(s);
         s.unindent();
         s.println("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }

    /**
     * Change the else branch in case there is an else if condition
     * @param elseBranch
     */
    public void changeElse(ListInst elseBranch) {
    	this.elseBranch = elseBranch;
    }


}
