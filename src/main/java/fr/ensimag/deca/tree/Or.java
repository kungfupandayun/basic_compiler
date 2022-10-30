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
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SGT;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

/**
 * Logical operation OR
 * return true only if one of the two expressions is true
 * @author gl07
 * @date 01/01/2020
 */
public class Or extends AbstractOpBool {

	/**
	 * Take in two expressions
	 * @param leftOperand
	 * @param rightOperand 
	 */
    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


    //creer par nous
    @Override
	public void codeGenExpr(DecacCompiler compiler, GBmanager GB){
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();	
		this.getLeftOperand().codeGenExpr(compiler, GB);
		GPRegister register = Register.getR(GB.getValGB());
		//si right est un valeur
		if(this.getRightOperand().getDval()!=null){
			//si right n'est pas un variable
			if(this.getRightOperand() instanceof Identifier && !compiler.getAddr((DAddr)this.getRightOperand().getDval()) && !((Identifier)this.getRightOperand()).getDefinition().isParam()) {
				//si right n'est pas un field
				if(!((Identifier)this.getRightOperand()).getDefinition().isField()) {
					compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
				}//sinon
				else {
					GPRegister register2 = Register.getR(GB.getValGB());
					compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register2)); // on charge l'objet
					compiler.addInstruction(new ADD(new RegisterOffset(((Identifier)this.getRightOperand()).getFieldDefinition().getIndex()+1,register2), register));
			        compiler.addInstruction(new CMP(0,register));
			        compiler.addInstruction(new SGT(register));

				}
			}else {		//sinon	
			compiler.addInstruction(new ADD(this.getRightOperand().getDval(), register));
	        compiler.addInstruction(new CMP(0,register));
	        compiler.addInstruction(new SGT(register));
			}
		}else {//sinon
			if(GB.remplir()) {//si tous les registres sont utilisé
				register = Register.getR(GB.getLength()-1);
				compiler.addInstruction(new TSTO(1));
                compiler.addInstruction(new BOV(new Label("stack_overflow")));
				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);	
				compiler.addInstruction(new LOAD(register, Register.R0));
				compiler.addInstruction(new POP(register));
				compiler.addInstruction(new ADD(Register.R0, register));
		        compiler.addInstruction(new CMP(0,register));
		        compiler.addInstruction(new SGT(Register.R0));
			}
			else {

				compiler.addInstruction(new PUSH(register));
				this.getRightOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new POP(register));
				GPRegister register1 = Register.getR(GB.getValGB());
				compiler.addInstruction(new ADD(register1, register));
		        compiler.addInstruction(new CMP(0,register));
		        compiler.addInstruction(new SGT(register));
			}
		}
		GB.setGBmanager(table);
	}
    
    
	@Override
	public DVal getDval() {
		return null;
	}

}
