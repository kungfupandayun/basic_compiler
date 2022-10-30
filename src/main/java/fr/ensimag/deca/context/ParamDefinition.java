package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a method parameter.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ParamDefinition extends ExpDefinition {
	
	/**
	 * Create a parameter definition
	 * @param type The parameter type
	 * @param location The parameter location
	 */
    public ParamDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "parameter";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean isParam() {
        return true;
    }

}
