package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Definition of a Deca type (builtin or class).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class TypeDefinition extends Definition {
	
	
	 private DAddr operand;
	
	/**
	 * Create a new type definition
	 * @param type
	 * @param location
	 */
    public TypeDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "type";
    }

    @Override
    public boolean isExpression() {
        return false;
    }
    
    public void setOperand(DAddr operand) {
        this.operand = operand;
    }
    /**
     * Get the adress of the class
     * @return
     */
    public DAddr getOperand() {
        return operand;
    }
   

}
