package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.Label;

import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * Arithmetic operation MINUS -
 * @author gl07
 * @date 01/01/2020
 */
public class Minus extends AbstractOpArith {
	
	/**
	 * Take in two  expression to represent the arithmetic operation MINUS 
	 * @param leftOperand is an AbstractExpr
	 * @param rightOperand is an AbstractExpr
	 */
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    public void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand) {
    	compiler.addInstruction(new SUB(leftOperand, rightOperand));
        if(!compiler.getCompilerOptions().isNoCheck() && this.getType().isFloat()){
            Label error = new Label("arithmetic_overflow");
            compiler.addInstruction(new BOV(error));
        }
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
