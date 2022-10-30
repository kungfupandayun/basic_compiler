package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class IntType extends Type {

    public IntType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isInt() {
        return true;
    }

    @Override
    /**
     * Return true si otherType est un entier
     */
    public boolean sameType(Type otherType) {
    	if (otherType.isInt()) {
    		return true;
    	}
    	return false;
    }


}
