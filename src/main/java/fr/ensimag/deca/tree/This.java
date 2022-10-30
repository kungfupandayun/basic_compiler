package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class This extends AbstractExpr{

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		if (currentClass == null ) {
			throw new ContextualError("[ThisError] : This must be applied inside a class", this.getLocation());
		}
		if (currentClass.getType()!=null) {
			this.setType(currentClass.getType());
			return currentClass.getType();
		}
		return null;
	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("this");
		
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		// c'est une feuille
		
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		//pas de children
		
	}

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
		compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),Register.getR(GB.getValGB())));
		GB.setGBmanager(table);
	}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
