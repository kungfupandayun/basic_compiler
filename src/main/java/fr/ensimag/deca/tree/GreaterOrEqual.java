package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.SGE;

/**
 * Operator comparaison GreaterOrEqual
 *
 * @author gl07
 * @date 01/01/2020
 */
public class GreaterOrEqual extends AbstractOpIneq {

	/**
	 * Take in two  expression to represent the comparison operation GreaterOrEqual
	 * @param leftOperand 
	 * @param rightOperand 
	 */
    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">=";
    }

    //creer par nous
    @Override
    public void mnemop(DecacCompiler compiler, GPRegister rightOperand) {
      compiler.addInstruction(new SGE(rightOperand));
    }

	@Override
	public DVal getDval() {
		return null;
	}


}
