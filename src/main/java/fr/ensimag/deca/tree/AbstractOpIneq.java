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
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.deca.codegen.GBmanager;
/**
 * Abstract class for comparison of approximation range 
 * >,<,>=,<=
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractOpIneq extends AbstractOpCmp {

	private static final Logger LOG = Logger.getLogger(AbstractOpIneq.class);

	/**
	 * Constructor of a Non- exact comparison
	 * @param leftOperand 
	 * @param rightOperand 
	 */
    public AbstractOpIneq(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


	 @Override
	    protected void codeGenExpr(DecacCompiler compiler, GBmanager GB){
	        boolean[] table= GB.getCopyTabRegister(); //enregistrer l'état init

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
				}else {//sinon
					compiler.addInstruction(new CMP(this.getRightOperand().getDval(),register));
					mnemop(compiler, register);
				}
			}else {//sinon
				if(GB.remplir()) {//si tous les registres sont utilisé
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
		 * Les fonctions de chaque opération unaire pour codeGenExpr
		 *
		 * @param compiler compiler où nous travaillons
		 * @param rightOperand right partie que nous voulons faire l'opération et enregistrer le resulatat
		 *
		 */
	public abstract void mnemop(DecacCompiler compiler, GPRegister rightOperand);

    @Override
    public  Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError{
    	LOG.debug("Verify OpIneq: start");
    	Type type1 = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		Type type2 = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
		Type type = this.typeArithOp(type1,type2);
		if (type.isFloat()) {
			ConvFloat conv;
			if (type1.isInt()) {
				conv = new ConvFloat(this.getLeftOperand());
				this.setLeftOperand(conv);
				this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
			}else if (type2.isInt()) {
				conv = new ConvFloat(this.getRightOperand());
				this.setRightOperand(conv);
				this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
			}
		}
		//type de parametre pour comparateur non équallité est soit Float ou Int

		Type boolType=new BooleanType(localEnv.getSymbolTab().create("boolean"));
		this.setType(boolType);
		return boolType;
		}
}
