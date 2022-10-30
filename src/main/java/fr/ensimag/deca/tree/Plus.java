package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;


/**
 * Arithmetic operation PLUS +
 * @author gl07
 * @date 01/01/2020
 */
public class Plus extends AbstractOpArith {
	
	/**
	 * Take in two expression to represent the arithmetic operation PLUS
	 * @param leftOperand 
	 * @param rightOperand 
	 */
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    //creer par nous
    @Override 
    public void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand) {
    	compiler.addInstruction(new ADD(leftOperand, rightOperand));
        if(!compiler.getCompilerOptions().isNoCheck() && this.getType().isFloat()){
          Label error = new Label("arithmetic_overflow");
          compiler.addInstruction(new BOV(error));
        }
    }

    @Override
    protected String getOperatorName() {
        return "+";
    }

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}
}
