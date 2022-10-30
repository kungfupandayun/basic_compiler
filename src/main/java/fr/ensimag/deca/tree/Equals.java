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
import fr.ensimag.ima.pseudocode.instructions.SEQ;

/**
 * Exact comparison operator EQUALS =
 * @author gl07
 * @date 01/01/2020
 */
public class Equals extends AbstractOpExactCmp {

	/**
	 * @param leftOperand , AbstractExpr
	 * @param rightOperand , AbstractExpr
	 */
    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }




    @Override
    public void mnemop(DecacCompiler compiler, GPRegister rightOperand) {
      compiler.addInstruction(new SEQ(rightOperand));
    }


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
