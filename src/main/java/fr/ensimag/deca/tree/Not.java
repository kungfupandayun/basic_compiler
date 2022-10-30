package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

/**
 * Unary operator NOT
 * @author gl07
 * @date 01/01/2020
 */
public class Not extends AbstractUnaryExpr {

	/**
	 * Take in one expression
	 * @param operand 
	 */
    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type2 = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (type2.isBoolean()) {
          this.setType(type2);
        	return type2;
        } else {
        	throw new ContextualError("[UnaryNotError] : Not cannot be done on" + type2.getName().getName(), this.getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= GB.getCopyTabRegister();
	      GPRegister register;
	     if(GB.remplir()) {
	 			register = Register.getR(GB.getLength()-1);
	 			if(!compiler.getCompilerOptions().isNoCheck()){
	 				 compiler.addInstruction(new TSTO(1));
	 				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
	 			}
	 			this.getOperand().codeGenExpr(compiler,GB);
	 			compiler.addInstruction(new CMP(0,register));
	 			compiler.addInstruction(new SEQ(register));
	     } else{
	         getOperand().codeGenExpr(compiler,GB);
	         register = Register.getR(GB.getValGB());
	         compiler.addInstruction(new CMP(0,register));
	         compiler.addInstruction(new SEQ(register));
	     	}
	     GB.setGBmanager(table);
	}


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}
}
