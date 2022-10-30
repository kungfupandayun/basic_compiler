package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;

/**
 * 
 *Return the expression of a method
 * @author gl07
 * @date 15/01/2020
 */
public class Return extends AbstractInst{
	private AbstractExpr expr;

	/**
	 * Store the expression to be returned
	 * @param expr
	 */
	public Return(AbstractExpr expr) {
		this.expr = expr;
	}
	@Override
	protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass,
			Type returnType) throws ContextualError {

		if (returnType.isVoid()) {
			throw new ContextualError("[ReturnError] : This method returns nothing", this.getLocation());
		} else {
			AbstractExpr expr = this.getExpr().verifyRValue(compiler, localEnv, currentClass, returnType);
			this.expr.setType(expr.getType());
		}
	}

	@Override
	protected void codeGenInst(DecacCompiler compiler) {
		GBmanager GB = compiler.getGBmanager();
		boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
		this.expr.codeGenExpr(compiler,GB);
		compiler.addInstruction(new LOAD(Register.getR(GB.getValGB()),Register.R0));
		compiler.addInstruction(new BRA(compiler.getEnd()));
		GB.setGBmanager(table);
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("return ");
		expr.decompile(s);
		s.print(";");

	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		expr.prettyPrint(s, prefix, false);

	}

	@Override
	protected void iterChildren(TreeFunction f) {
		expr.iter(f);

	}
	public AbstractExpr getExpr() {
		return expr;
	}
	public void setExpr(AbstractExpr expr) {
		this.expr = expr;
	}

}
