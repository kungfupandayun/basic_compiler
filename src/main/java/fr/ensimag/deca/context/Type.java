package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl07
 * @date 01/01/2020
 */

public abstract class Type {


    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;
    /**
     * a type is associated with a symbol
     * @param name The symbol name.
     */
    public Type(Symbol name) {
        this.name = name;
    }

    /**
     * Return the Symobl that is associated to the type.
     * @return
     */
    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }
    /**
     * Return true if the type is a class type
     * @return boolean
     */
    public boolean isClass() {
        return false;
    }
    /**
     * Return true if the type is a Int type.
     * @return
     */
    public boolean isInt() {
        return false;
    }
    /**
     * Return true if the Type is a float Type.
     * @return
     */
    public boolean isFloat() {
        return false;
    }
    /**
     * Return true if the Type is a boolean Type.
     * @return
     */
    public boolean isBoolean() {
        return false;
    }
    /**
     * Return true if the Type is a void Type.
     * @return
     */
    public boolean isVoid() {
        return false;
    }
    /**
     * Return true if the Type is a string Type.
     * @return
     */
    public boolean isString() {
        return false;
    }
    /**
     * Return true if the Type is null  Type.
     * @return
     */
    public boolean isNull() {
        return false;
    }
    /**
     * Return true if the Type is a class Type or null.
     * @return
     */
    public boolean isClassOrNull() {
        return false;
    }

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     */
    public ClassType asClassType(String errorMessage, Location l)
            throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    /**
     * Return true if this is a sub Type of t2
     * @param env The environment Type of the program
     * @param t2  A type
     * @return If this is a subType of t2 is the environment env.
     */
    public boolean subType(EnvironmentType env, Type t2) {
    	// si T2 est le meme que this

    	 if ((this.isClass()) && (t2.isClass())) { // si les deux types sont des classes
    	    if (this.equals(t2)) {
    	    		return true;
    	    }
    		if (t2.getName().equals(env.getSymbolTab().create("Object"))){
    			// toute class est un sous type de la class object
    			return true;
    		}
    		// On cherche la super class de this
    		
    		ClassDefinition classThis = (ClassDefinition) env.get(this.getName());
    		ClassDefinition classDef = (ClassDefinition)env.get(t2.getName());
    		ClassDefinition superDef = classThis.getSuperClass();
    		while (superDef != null) {
    			if (superDef.getType().equals(classDef.getType())) {
        			return true; // this est une sous class de t2
    			}
    			superDef = superDef.getSuperClass();
    		}
    	} else {
    		
    		if ((this.isNull()) && (t2.isClass())) {
    			return true; // null est un sous type de toute classe
    		}
    		if (this.sameType(t2)) {
        		return true;
        	} 
    	}
		return false;
    }
    /**
     * Test if an assign is possible between this and t2.
     * @param env The environment Type of the program
     * @param t2 A type
     * @return Return true if we can assign an object of type t2 to this.
     */
    public boolean assignCompatible(EnvironmentType env,Type t2) {
    	
    	if ((this.isFloat()) && (t2.isInt())) {
    		return true;
    	}
    	
    	return t2.subType(env, this);
    }

    /**
     * Test if a cast is possible between this and t2
     * @param env The environment Type of the program
     * @param t2 A type
     * @return Return true if the cast is possible
     */
    public boolean castCompatible(EnvironmentType env, Type t2) {
    	if (this.isVoid()) {
    		return false;
    	}
    	if (this.assignCompatible(env,t2) || t2.assignCompatible(env,this)) {
    		return true;
    	}
    	return false;
    }

    /**
     * Test if this type is instanceof type2
     * @param env The environment Type of the program
     * @param t2 A type
     * @return Return true if possible
     */
    public boolean typeInstanceOf(EnvironmentType env, Type t2) {
    	if (this.isClassOrNull()) {
    		if (t2.isClass()) {
    			return true;
    		}
    	}
    	return false;
    }

}
