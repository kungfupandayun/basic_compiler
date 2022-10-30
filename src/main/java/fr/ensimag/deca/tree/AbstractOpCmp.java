package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Abstract class for comparison
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

	/**
	 * Constructor of a normal comparaison operation
	 * @param leftOperand 
	 * @param rightOperand 
	 */
    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


}
