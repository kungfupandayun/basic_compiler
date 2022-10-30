package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.SGT;

/**
 *Operator comparaison Greater
 * @author gl07
 * @date 01/01/2020
 */
public class Greater extends AbstractOpIneq {

	/**
	 * Take in two  expression to represent the comparison operation Greater
	 * @param leftOperand is of AbstractExpr class
	 * @param rightOperand is of AbstractExpr class
	 */
    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }

    //creer par nous
    @Override
    public void mnemop(DecacCompiler compiler, GPRegister rightOperand) {
      compiler.addInstruction(new SGT(rightOperand));
    }

  


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}


}
