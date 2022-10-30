package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SGT;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.Label;


/**
 * Logical operation AND
 * @author gl07
 * @date 01/01/2020
 */
public class And extends AbstractOpBool {

	/***
	 * Represent logical operation AND
	 * @param leftOperand an expression
	 * @param rightOperand an expression
	 */
    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
	public void codeGenExpr(DecacCompiler compiler, GBmanager GB){
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();//enregistrer l'état init
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
					compiler.addInstruction(new MUL(new RegisterOffset(((Identifier)this.getRightOperand()).getFieldDefinition().getIndex()+1,register2), register));
				}
			}else {	//sinon		
				compiler.addInstruction(new MUL(this.getRightOperand().getDval(), register));
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
				compiler.addInstruction(new MUL(Register.R0, register));
			}
			else {//sinon
				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new POP(register));
				compiler.addInstruction(new MUL(Register.getR(GB.getValGB()), register));
			}
		}
		GB.setGBmanager(table); //reset tab a etat init
	}

	@Override
	public DVal getDval() {
		return null;
	}

}
