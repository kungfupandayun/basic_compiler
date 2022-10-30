package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;
/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class Assign extends AbstractBinaryExpr {

	private static final Logger LOG = Logger.getLogger(Assign.class);

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }
    /**
     * Assigne a variable to an expression
     * @param leftOperand
     * @param rightOperand
     */
    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    /**
     * context check of an assign, the resulted type of assign is the type of it's left operand
     */
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	LOG.debug("VerifyAssign: start");
		Type expectedType = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
		AbstractExpr expression = this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, expectedType);

		if (expectedType.isFloat()) {
			if (expression.getType().isInt()){
				ConvFloat conv = new ConvFloat(expression);
				this.setRightOperand(conv);
				this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);

			}
		}

		if (localEnv.get(this.getLeftOperand().getName()) != null) {
			if (expression instanceof Identifier) {
				Identifier ident = (Identifier) expression;
				localEnv.setDefinitionOfSymobl(this.getLeftOperand().getName(), ident.getExpDefinition());
			}
		}


		this.setType(expectedType); // type de assign c'est le type de lvalue
		LOG.debug("VerifyAssign: End");
		return expectedType;
    }

    @Override
    public void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType) throws ContextualError {
    	this.verifyExpr(compiler, localEnv, currentClass);
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    public void decompile(IndentPrintStream s) {

        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }

	@Override
	public DVal getDval() {
		return null;
	}


	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= GB.getCopyTabRegister();//enregistrer l'état init
		compiler.addComment("assign");
		GPRegister register;
		GPRegister registerleft = null;
		//si lest est un selection
		if(this.getLeftOperand().getDval()==null ) {
			this.getLeftOperand().codeGenInst(compiler);
			registerleft = Register.getR(GB.getValGB());
			DAddr addr = new RegisterOffset(0,Register.SP);
	        compiler.addInstruction(new STORE(registerleft, addr));
		}
		//si left est un filed
		if(getLeftOperand().getDefinition().isField()&&this.getLeftOperand().getDval()!=null){
			registerleft = Register.getR(GB.getValGB());
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),registerleft));
		}
		//si tous les registres sont utilisé
		if(GB.remplir()) {
			register = Register.getR(GB.getLength()-1);
			if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			if(this.getLeftOperand().getDval()==null) {
				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);			
				compiler.addInstruction(new LOAD(register, Register.R0));
				compiler.addInstruction(new POP(register));
			}
			else {
					this.getRightOperand().codeGenExpr(compiler, GB);
			}
		}
		else {
			if(this.getRightOperand().getDval()==null) {
				this.getRightOperand().codeGenInst(compiler);
			}else {
				this.getRightOperand().codeGenExpr(compiler, GB);
			}
			register = Register.getR(GB.getValGB());
		}
		//pour les cas différent
        if(getLeftOperand().getDefinition().isField()){
        	GPRegister stock;
        	if(registerleft!=null) {
        		stock = registerleft;
        	}else {
        		stock = register;
        	}
            compiler.addInstruction(new STORE(register,new RegisterOffset(this.getLeftOperand().getFieldDefinition().getIndex()+1,stock)));
        	compiler.setAddr(this.getLeftOperand().getFieldDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isClass()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getClassDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getClassDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isMethod()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getMethodDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getMethodDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isParam()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getParamDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getParamDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isExpression()){
            compiler.addInstruction(new STORE(register, this.getLeftOperand().getExpDefinition().getOperand()));
            compiler.setAddr(this.getLeftOperand().getExpDefinition().getOperand());
        }
        else{
        	System.out.println("var");
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getVariableDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getVariableDefinition().getOperand());
        }

		GB.setGBmanager(table);
	}

    @Override
    protected void codeGenInst(DecacCompiler compiler){
    	GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
		compiler.addComment("assign");
		GPRegister register;
		GPRegister registerleft = null;
		//si lest est un selection
		if(this.getLeftOperand().getDval()==null ) {
			this.getLeftOperand().codeGenInst(compiler);
			registerleft = Register.getR(GB.getValGB());
			DAddr addr = new RegisterOffset(0,Register.SP);
	        compiler.addInstruction(new STORE(registerleft, addr));
		}
		//si left est un filed
		if(getLeftOperand().getDefinition().isField()  && this.getLeftOperand().getDval()!=null){
			registerleft = Register.getR(GB.getValGB());
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),registerleft));
		}
		//si tous les registres sont utilisé
		if(GB.remplir()) {
			register = Register.getR(GB.getLength()-1);
			if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			if(this.getLeftOperand().getDval()==null) {
				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new LOAD(register, Register.R0));
				compiler.addInstruction(new POP(register));
			}
			else {
					this.getRightOperand().codeGenExpr(compiler, GB);
			}
		}
		else {
				this.getRightOperand().codeGenExpr(compiler, GB);
				register = Register.getR(GB.getValGB());
		}
		//pour les cas différent
        if(getLeftOperand().getDefinition().isField()){
        	GPRegister stock;
        	if(registerleft!=null) {
        		stock = registerleft;
        	}else {
        		stock = register;
        	}
            compiler.addInstruction(new STORE(register,new RegisterOffset(this.getLeftOperand().getFieldDefinition().getIndex()+1,stock)));
        	compiler.setAddr(this.getLeftOperand().getFieldDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isClass()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getClassDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getClassDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isMethod()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getMethodDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getMethodDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isParam()){
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getParamDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getParamDefinition().getOperand());
        }
        else if(getLeftOperand().getDefinition().isExpression()){
            compiler.addInstruction(new STORE(register, this.getLeftOperand().getExpDefinition().getOperand()));
            compiler.setAddr(this.getLeftOperand().getExpDefinition().getOperand());
        }
        else{
        	compiler.addInstruction(new STORE(register, this.getLeftOperand().getVariableDefinition().getOperand()));
        	compiler.setAddr(this.getLeftOperand().getVariableDefinition().getOperand());
        }

		GB.setGBmanager(table);
    }
}
