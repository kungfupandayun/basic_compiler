package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import org.apache.commons.lang.Validate;

/**
 * Type defined by a class.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ClassType extends Type {
    
    protected ClassDefinition definition;
    
    public ClassDefinition getDefinition() {
        return this.definition;
    }
            
    @Override
    public ClassType asClassType(String errorMessage, Location l) {
        return this;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    /**
     * Standard creation of a type class.
     */
    public ClassType(Symbol className, Location location, ClassDefinition superClass) {
        super(className);
        this.definition = new ClassDefinition(this, location, superClass);
    }

    /**
     * Creates a type representing a class className.
     * (To be used by subclasses only)
     */
    protected ClassType(Symbol className) {
        super(className);
    }
    

    @Override
    public boolean sameType(Type otherType) {
    	if(otherType.isClass()) {
    		if (this.getName().equals(otherType.getName())) {
    			return true;
    		}
    		return false;
        }
        else 
        	return false;
    }
    
    
    /**
     * Return true if potentialSuperClass is a superclass of this class.
     */
    public boolean isSubClassOf(EnvironmentType env, ClassType t2) {
    	
    	if (this.equals(t2)) {
    		return true;
    	} else if (this.isNull()) {
    		
			return true; // null est un sous type de toute classe
		} else {
			
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
    		
    	}
		return false;
    }

}
