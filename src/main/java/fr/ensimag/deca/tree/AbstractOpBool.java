package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * Abstract class for logic Operation OR and AND
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

	private static final Logger LOG = Logger.getLogger(AbstractOpBool.class);

	/**
	 * Constructor of a logic operation
	 * take in a left expression and a right expression to perform operation
	 * @param leftOperand
	 * @param rightOperand 
	 */
    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public  Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError{

    	LOG.debug("Verify OpBool: start");

    	// get le type d'operand gauche et droite
    	Type lvalueType = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		Type rvalueType = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);

    	if ((lvalueType.isBoolean())&&(rvalueType.isBoolean()))
    	{
    		this.setType(lvalueType);
    		return lvalueType;
    	}
    	else {
			throw new ContextualError("[BooleanOperationError] : Boolean operation cannot be performed between " + lvalueType.getName() + " and " + rvalueType.getName() ,this.getLocation());
		}
    }

}
