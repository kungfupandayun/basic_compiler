package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.IntLiteral;

/**
 * Operand that contains a value.
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public abstract class DVal extends Operand {
	private int n ;
	private Identifier symbo;

	public int integer(IntLiteral n) {
		return n.getValue();
	}
	
	public DAddr identifier(Identifier symbo) {
		return symbo.getExpDefinition().getOperand();
	}
	

}
