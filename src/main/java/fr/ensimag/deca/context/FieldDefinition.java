package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Visibility;

/**
 * Definition of a field (data member of a class).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class FieldDefinition extends ExpDefinition {
	
	
	private int index;
	
	
	/**
	 * Get the field index
	 * @return field index
	 */
    public int getIndex() {
        return index;
    }

   
    /**
     * Change the index of the field in the current class
     * @param i
     */
    public void setIndex(int i) {
    	this.index = i;
    }
    
    @Override
    public boolean isField() {
        return true;
    }

    private final Visibility visibility;
    private final ClassDefinition containingClass;
    /**
     * Create a new field 
     * @param type The field type
     * @param location The location of the field in the deca program
     * @param visibility The field visibility
     * @param memberOf The class that the field belongs to it
     * @param index The index of the field in this class
     */
    public FieldDefinition(Type type, Location location, Visibility visibility,
            ClassDefinition memberOf, int index) {
        super(type, location);
        this.visibility = visibility;
        this.containingClass = memberOf;
        this.index = index;
    }
    
    @Override
    public FieldDefinition asFieldDefinition(String errorMessage, Location l)
            throws ContextualError {
        return this;
    }
    /**
     * Get the field visibility
     * @return Visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    public ClassDefinition getContainingClass() {
        return containingClass;
    }

    @Override
    public String getNature() {
        return "field";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

}
