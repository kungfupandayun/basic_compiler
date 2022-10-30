package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of an identifier.
 * 
 * @author gl07
 * @date 01/01/2020
 */
public abstract class Definition {
	
	private Location location;
    private Type type;
    @Override
    public String toString() {
        String res;
        res = getNature();
        if (location == Location.BUILTIN) {
            res += " (builtin)";
        } else {
            res += " defined at " + location;
        }
        res += ", type=" + type;
        return res;
    }

    public abstract String getNature();
    /**
     * A definition is the combination of a type and it is location
     * given by the parser
     * @param type The symbol Type.
     * @param location The location of the symbol in the parser.
     */
    public Definition(Type type, Location location) {
        super();
        this.location = location;
        this.type = type;
    }
    /**
     * Get Type definition
     * @return Type definition
     */
    public Type getType() {
        return type;
    }
    /**
     * Get the location of the definition in the deca file
     * @return The location in the deca file
     */
    public Location getLocation() {
        return location;
    }
    /**
     * Set the location of the symbol , used while parsing the file
     * in DecaParser.
     * @param location The location given by the lexer.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * True if the definition is a field definition
     * @return boolean
     */
    public boolean isField() {
        return false;
    }
    /**
     * Return true if the definition is a method definition
     * @return
     */
    public boolean isMethod() {
        return false;
    }
    /**
     * Return true if the definition is a class definition.
     * @return
     */
    public boolean isClass() {
        return false;
    }
    /**
     * Return true if the definition is a parameter definition.
     * @return
     */
    public boolean isParam() {
        return false;
    }

    /**
     * Return the same object, as type MethodDefinition, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     */
    public MethodDefinition asMethodDefinition(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }
    
    /**
     * Return the same object, as type FieldDefinition, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     */
    public FieldDefinition asFieldDefinition(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    public abstract boolean isExpression();

}
