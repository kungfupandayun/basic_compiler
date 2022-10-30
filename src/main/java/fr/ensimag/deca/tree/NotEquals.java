package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SNE;

/**
 * Exact comparison operator not equal !=
 * @author gl07
 * @date 01/01/2020
 */
public class NotEquals extends AbstractOpExactCmp {

	/**
	 * Take in two abstract expressions
	 * @param leftOperand 
	 * @param rightOperand
	 */
    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    //creer par nous
    @Override
    public void mnemop(DecacCompiler compiler, GPRegister rightOperand) {
      compiler.addInstruction(new SNE(rightOperand));
    }


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}


}
