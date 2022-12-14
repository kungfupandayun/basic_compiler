package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

/**
 *Abstract class for comparison of exact value
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

	private static final Logger LOG = Logger.getLogger(AbstractOpExactCmp.class);
	/**
	 * Construct of an exact comparison operation
	 * @param leftOperand
	 * @param rightOperand
	 */
    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public  Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError{

    	LOG.debug("Verify OpEq: start");

    	// get le type d'operand gauche et droite
    	Type type1 = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		Type type2 = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);

		Type boolType=new BooleanType(localEnv.getSymbolTab().create("boolean"));

		//les types d'op??randes pour comparer sont soit Float ou Int , soit Classe ou null, soit deux Bools
		if((type1.isInt()||type1.isFloat())&&
				(type2.isInt()||type2.isFloat())) {
			// il aut convertir les entier en float
			Type type = this.typeArithOp(type1, type2);
			if (type.isFloat()) {
				ConvFloat conv;
				if(type1.isInt()) {
				 conv = new ConvFloat(this.getLeftOperand());
				 this.setLeftOperand(conv);
				 this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
				} else if (type2.isInt()){
				 conv = new ConvFloat(this.getRightOperand());
				 this.setRightOperand(conv);
				 this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
				}
			}
			this.setType(boolType);
			return boolType;
		}
    	else if ((type1.isClassOrNull())&&(type2.isClassOrNull())) {
    		this.setType(boolType);
    		if(type1.isNull()) {
    			this.getLeftOperand().setType(type1);
    		}
    		if(type2.isNull()) {
    			this.getRightOperand().setType(type2);
    		}
    		return boolType;
    	}
    	else if ((type1.isBoolean())&&(type2.isBoolean())) {
    		this.setType(boolType);
    		return boolType;
    	}
		else {
			throw new ContextualError("[ComparisonOperationError]  : Comparison operation cannot be performed between " + type1.getName() + " and " + type2.getName() ,this.getLocation());
		}

    }



    @Override
    protected void codeGenExpr(DecacCompiler compiler,GBmanager GB){
        boolean[] table= GB.getCopyTabRegister(); //enregistrer l'??tat init

		this.getLeftOperand().codeGenExpr(compiler, GB);
		GPRegister register = Register.getR(GB.getValGB());
		//si right est un valeur
		if(this.getRightOperand().getDval()!=null) {
			//si right n'est pas un variable
			if(this.getRightOperand() instanceof Identifier && !compiler.getAddr((DAddr)this.getRightOperand().getDval()) && !((Identifier)this.getRightOperand()).getDefinition().isParam()) {
				//si right n'est pas un field
				if(!((Identifier)this.getRightOperand()).getDefinition().isField()) {
					compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
				}
				else {//sinon
					GPRegister register2 = Register.getR(GB.getValGB());
					compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register2)); // on charge l'objet
					compiler.addInstruction(new CMP(new RegisterOffset(((Identifier)this.getRightOperand()).getFieldDefinition().getIndex()+1,register2),register));
					mnemop(compiler, register);
				}
			}else {				//sinon
				compiler.addInstruction(new CMP(this.getRightOperand().getDval(),register));
				mnemop(compiler, register);
			}
		}else {//sinon
			if(GB.remplir()) {//si tous les registres sont utilis??
				register = Register.getR(GB.getLength()-1);
				if(!compiler.getCompilerOptions().isNoCheck()){
					 compiler.addInstruction(new TSTO(1));
					 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
				}
				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new POP(register));
			}else {//sinon
				this.getRightOperand().codeGenExpr(compiler, GB);
			}
			compiler.addInstruction(new CMP(Register.getR(GB.getValGB()),register));
			mnemop(compiler, register);
		}

        GB.setGBmanager(table);//reset tab a etat init
    }
		/**
		 * Les fonctions de chaque op??ration unaire pour codeGenExpr
		 *
		 * @param compiler compiler o?? nous travaillons
		 * @param rightOperand right partie que nous voulons faire l'op??ration et enregistrer le resulatat
		 *
		 */
      public abstract void mnemop(DecacCompiler compiler, GPRegister rightOperand);

}
