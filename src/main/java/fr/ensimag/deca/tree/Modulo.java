package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.context.Type;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.Label;

import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * Perform operation modulo of two expression
 * Return the remainder of the division of two value
 * @author gl07
 * @date 01/01/2020
 */
public class Modulo extends AbstractOpArith {

	/** A log to debug*/
	private static final Logger LOG = Logger.getLogger(Modulo.class);

	/**
	 * Take in two expression to represent the arithmetic operation MULTIPLY
	 * @param leftOperand is an AbstractExpr
	 * @param rightOperand is an AbstractExpr
	 * The two expressions must be of type int
	 */
    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	LOG.debug("Verify Modulo: start");
    	Type lvalueType = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		Type rvalueType = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);

		//env (mod,int,int)=>int
		if(lvalueType.isInt()&&rvalueType.isInt()) {
			this.setType(lvalueType);
			return lvalueType;
		}
		else {
			throw new ContextualError("[ModuloOperationError] : Modulo operation can be performed only between int and int",this.getLocation());
		}

//		LOG.debug("Verify Modulo: end");
    }

	//creer par nous
	@Override
	public void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand) {
		compiler.addInstruction(new REM(leftOperand, rightOperand));
		if(!compiler.getCompilerOptions().isNoCheck()){
			// Check Divide.jqvq pour sqvoir pourquoi on q pqs qjouter lq condition que
			// le type soit un Flotant pour gérer le débordement arithmétique.
	      Label error = new Label("arithmetic_overflow");
	      compiler.addInstruction(new BOV(error));
   		 }
	}


    @Override
    protected String getOperatorName() {
        return "%";
    }


	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
