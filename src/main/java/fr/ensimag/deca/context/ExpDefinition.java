package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Definition associated to identifier in expressions.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class ExpDefinition extends Definition {

    public void setOperand(DAddr operand) {
        this.operand = operand;
    }
    /**
     * Get the adress of the expression
     * @return
     */
    public DAddr getOperand() {
        return operand;
    }
    private DAddr operand;
    
    /**
     * Create expression definition
     * @param type The type of the symbol.
     * @param location The location of the symbol in the deca file.
     */
    public ExpDefinition(Type type, Location location) {
        super(type, location);
    }

}
