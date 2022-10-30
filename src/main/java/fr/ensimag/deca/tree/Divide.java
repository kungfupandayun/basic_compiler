package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

/**
 * Arithmetic operation Divide / 
 * @author gl07
 * @date 01/01/2020
 */
public class Divide extends AbstractOpArith {
	
	/**
	 * Take in two expression to represent the arithmetic operation DIVIDE
	 * @param leftOperand 
	 * @param rightOperand 
	 */
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public void mnemop(DecacCompiler compiler, DVal leftOperand, GPRegister rightOperand) {
    	 if(this.getType().isFloat()) {
             // Instruction DIV pour les flottants
             compiler.addInstruction(new DIV(leftOperand, rightOperand));
         }else{
             // Instruction QUO pour les entiers
             compiler.addInstruction(new QUO(leftOperand, rightOperand));
         }
         if(!compiler.getCompilerOptions().isNoCheck() ) {
             //Ca gére la division par zéro mais pas le débordement quand il s'agit d'entier
             // parce que si on ajoute && this.getType().isFloat() dans la condition il rentre
             // meme pas dans le if quand il ne s'agit pas de 0.0
             compiler.addInstruction(new BOV(new Label("arithmetic_overflow")));

         }
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
