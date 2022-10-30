package fr.ensimag.deca.context;

import fr.ensimag.deca.codegen.FeildsTab;
import fr.ensimag.deca.codegen.MethodsTab;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.commons.lang.Validate;

/**
 * Definition of a class.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ClassDefinition extends TypeDefinition {

	private FeildsTab feildsTab = new FeildsTab();
	
	public FeildsTab getTabFiled() {
		return this.feildsTab;
	}
	private MethodsTab methodTab = new MethodsTab();
	
	public MethodsTab getTabMethod() {
		return this.methodTab;
	}

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }
    
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    private EnvironmentExp members;
    private final ClassDefinition superClass; 

    /**
     * Return the EnvironmentExp of the class
     * @return
     */
    public EnvironmentExp getMembers() {
        return members;
    }
    
    public void setMembers(EnvironmentExp members) {
    	this.members = members;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }
    
    
}
