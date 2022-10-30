package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.*;

/**
 * Arithmetic operation TIMES *
 * @author gl07
 * @date 01/01/2020
 */
public class Multiply extends AbstractOpArith {
	
	/**
	 * Take in two expression to represent the arithmetic operation MULTIPLY
	 * @param leftOperand
	 * @param rightOperand
	 */
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    //creer par nous
    @Override 
    public void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand) {
    	compiler.addInstruction(new MUL(leftOperand, rightOperand));
        if(!compiler.getCompilerOptions().isNoCheck() && this.getType().isFloat()){
          Label error = new Label("arithmetic_overflow");
          compiler.addInstruction(new BOV(error));
        }
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
