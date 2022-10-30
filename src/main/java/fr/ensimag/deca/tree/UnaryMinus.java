package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;

import org.antlr.v4.runtime.atn.SemanticContext.Operator;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * Unary operator minus
 * typically for negative values
 * @author gl07
 * @date 01/01/2020
 */

public class UnaryMinus extends AbstractUnaryExpr {

	/**A log for debugging*/
    private static final Logger LOG = Logger.getLogger(UnaryMinus.class);
    /**Attribute for operator minus*/
    private Operator op;

    /**
     * Constructor of a negative value
     * @param AbstractExpr is usually minus
     */
    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }


    /**
     * Getter for the operator minus
     * @return '-'
     */
	public Operator getOp() {
		return op;
	}

	/**
	 *
	 * Set up the operator as minus
	 * @param op
	 */
	public void setOp(Operator op) {
		this.op = op;
	}


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	LOG.debug("verify UnaryMinus: start");
        Type type1 =  this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (type1.isInt() || type1.isFloat()) {
          LOG.debug("verify UnaryMinus: end");
          this.setType(type1);
        	return type1;
        } else {
        	throw new ContextualError("[UnaryMinusError] : A unary minus cannot be done on" + type1.getName().getName(), this.getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
        getOperand().codeGenExpr(compiler, GB);
        GPRegister register = Register.getR(GB.getValGB());
        compiler.addInstruction(new OPP(register,register));
        GB.setGBmanager(table);
	}

	  @Override
	    protected void codeGenPrint(DecacCompiler compiler){
		  GBmanager GB = compiler.getGBmanager();
	        boolean[] table = GB.getCopyTabRegister(); //on verifie les registre
	        if(GB.remplir()){
	        	GPRegister register = Register.getR(GB.getLength()-1);
				this.getOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new OPP(register, Register.R1));
	        }
	        else{
	        	GPRegister register = Register.getR(GB.getValGB());
				this.getOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new OPP(register, Register.R1));
	        }
	        if(this.getType().isInt()){
	            compiler.addInstruction(new WINT());
	        }
	        else if(this.getType().isFloat()){
	            compiler.addInstruction(new WFLOAT());
	        }
	        GB.setGBmanager(table);
	    }
			@Override
			protected void codeGenPrintX(DecacCompiler compiler){
				GBmanager GB = compiler.getGBmanager();
				boolean[] table = GB.getCopyTabRegister(); //on verifie les registre
				if(this.getType().isFloat()){
					if(GB.remplir()){
						GPRegister register = Register.getR(GB.getLength()-1);
						this.getOperand().codeGenExpr(compiler, GB);
						compiler.addInstruction(new OPP(register, Register.R1));
					}
					else{
						GPRegister register = Register.getR(GB.getValGB());
						this.getOperand().codeGenExpr(compiler, GB);
						compiler.addInstruction(new OPP(register, Register.R1));
					}
						compiler.addInstruction(new WFLOATX());
				}
				GB.setGBmanager(table);
			}

	@Override
	public DVal getDval() {
		return null;
	}

}
