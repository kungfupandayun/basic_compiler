package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.ima.pseudocode.Label;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Operator;

import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;

import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * Arithmetic binary operations (+, -, /, ...)
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

	private static final Logger LOG = Logger.getLogger(AbstractOpArith.class);

	/**
	 * Constructor of the Arithmetic operation 
	 * Take in an left and a right expression
	 * @param leftOperand 
	 * @param rightOperand
	 */
    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
	  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
	          ClassDefinition currentClass) throws ContextualError {
	  	LOG.debug("VerifyOpArith: start");
	  	Type type1 = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		Type type2 = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
		Type type = this.typeArithOp(type1,type2);
		if (type.isFloat()) {
			ConvFloat conv;
			if (type1.isInt()) {
				conv = new ConvFloat(this.getLeftOperand());
				this.setLeftOperand(conv);
				this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
			}else if (type2.isInt()){
				conv = new ConvFloat(this.getRightOperand());
				this.setRightOperand(conv);
				this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
			}


		}
		LOG.debug("VerifyOpArith: end");
		this.setType(type);
		return type;

	  }

    @Override
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType) throws ContextualError {
    	Type typeAttendu = this.verifyExpr(compiler, localEnv, currentClass);
		this.setType(typeAttendu);

    }

	@Override
	public void codeGenExpr(DecacCompiler compiler, GBmanager GB){
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();//enregistrer l'etat init
		this.getLeftOperand().codeGenExpr(compiler, GB);
		GPRegister register = Register.getR(GB.getValGB());
		//si right est un valeur
		if(this.getRightOperand().getDval()!=null){
			//si right n'est pas un variable
			if(this.getRightOperand() instanceof Identifier && !compiler.getAddr((DAddr)this.getRightOperand().getDval()) && !((Identifier)this.getRightOperand()).getDefinition().isParam()) {
				//si right n'est pas un field
				if(!((Identifier)this.getRightOperand()).getDefinition().isField()) {
					compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
				}
				else {//sinon
					GPRegister register2 = Register.getR(GB.getValGB());
					compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register2)); // on charge l'objet
					mnemop(compiler, new RegisterOffset(((Identifier)this.getRightOperand()).getFieldDefinition().getIndex()+1,register2),register);
				}
			}else {//sinon
				mnemop(compiler, this.getRightOperand().getDval(),register);
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
				compiler.addInstruction(new LOAD(register, Register.R0));
				compiler.addInstruction(new POP(register));
				mnemop(compiler,Register.R0,register);
			}
			else {//sinon
				this.getRightOperand().codeGenExpr(compiler, GB);
				mnemop(compiler, Register.getR(GB.getValGB()),register);
			}
		}
		GB.setGBmanager(table);//reset l'etat init
	}

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
    	GBmanager GB = compiler.getGBmanager();
    	boolean[] table = GB.getCopyTabRegister();
        this.codeGenExpr(compiler, GB);
        compiler.addInstruction(new LOAD(Register.getR(GB.getValGB()), Register.R1));
        if(this.getType().isInt()){
            compiler.addInstruction(new WINT());
        }
        else if(this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        }
        GB.setGBmanager(table);
    }

		/**
     * Les fonctions de chaque opération binaire pour codeGenExpr et codeGenInst
     *
     * @param compiler compiler où nous travaillons
     * @param leftOperand left partie que nous voulons faire l'opération
     * @param rightOperand right partie que nous voulons faire l'opération et enregistrer le resulatat
     *
     */
    public abstract void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand);

}
