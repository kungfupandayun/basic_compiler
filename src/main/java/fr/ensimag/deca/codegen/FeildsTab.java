package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractDeclField;
import fr.ensimag.deca.tree.AbstractDeclMethod;
import fr.ensimag.deca.tools.SymbolTable;


import java.util.HashMap;
import java.util.Map;


/**
 * Tableau qui regroupe les Fields.
 *
 * @author gl07
 * @date 16/01/2020
 */
public class FeildsTab {

    private Map<Integer,AbstractDeclField> FeildsTab;
    int nombre=0;

    /*General constructor of the fields table.*/
    public FeildsTab(){
        FeildsTab = new HashMap<Integer, AbstractDeclField>();
    }

    /*A function that adds a new feild to the hash map while associating it to a defined index.
     * @param indice index of the new feild.
     * @param field  the new feild to be added to the fields table.
     */
    public void addField(int indice, AbstractDeclField field){
    	field.getident().getFieldDefinition().setIndex(indice);
    	FeildsTab.put(indice,field);
		nombre ++;
	}


    /*A function that returns a field giving its index.
    * @param indice the index that we want to get the field of.
    * @return field that has the right index.
    */
    public AbstractDeclField getField(int indice){
       return FeildsTab.get(indice);
    }


    /*A function that returns the number of fields present in the fields' table.
     * @return number of fields in our fields' table.
    */
    public int getnombre() {
    	return this.nombre;
    }
}