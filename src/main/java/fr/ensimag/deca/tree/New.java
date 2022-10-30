package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

public class New extends AbstractExpr{
	private AbstractIdentifier ident ;

	public New(AbstractIdentifier ident) {
		Validate.notNull(ident);
		this.ident = ident;
	}
	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		if (compiler.getEnvironmentType().get(ident.getName())==null) {
			throw new ContextualError( "[NewError] : "+ ident.getName()+ " is not defined" , this.getLocation());
		}
		// un new n'est appele que sur un type de type Class
		Definition typeDef = compiler.getEnvironmentType().get(ident.getName());
		if (!(typeDef instanceof ClassDefinition)) {
			throw new ContextualError("[NewError] :  " + ident.getName()+ "is not a  class" , this.getLocation());
		}
		Type t = compiler.getEnvironmentType().get(ident.getName()).getType();
		this.ident.setDefinition(compiler.getEnvironmentType().get(ident.getName()));
		this.setType(t);
		return t;
	}
	
	@Override
    public void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table = GB.getCopyTabRegister();
		GPRegister register = Register.getR(GB.getValGB());
        compiler.addInstruction(new NEW(ident.getClassDefinition().getNumberOfFields()+1,register));
        compiler.addInstruction(new BOV(new Label("heap_overflow")));
        compiler.addInstruction(new LEA(ident.getClassDefinition().getOperand(),Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(0,register)));
        compiler.addInstruction(new PUSH(register));
        compiler.addInstruction(new BSR(new Label("init."+ident.getName().toString())));
        compiler.addInstruction(new POP(register));

        GB.setGBmanager(table);
    }

	@Override
	public void decompile(IndentPrintStream s) {
		s.print("new ");
		ident.decompile(s);
		s.print("()");
		
	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		 ident.prettyPrint(s, prefix, false);
		
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		ident.iter(f);
		
	}

}
