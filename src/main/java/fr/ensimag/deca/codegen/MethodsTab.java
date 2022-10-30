package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.AbstractDeclMethod;
import java.util.HashMap;
import java.util.Map;

/**
 * Tableau qui regroupe les Fields.
 *
 * @author gl07
 * @date 16/01/2020
 */

public class MethodsTab {

    private Map<Integer, AbstractDeclMethod> MethodsTab;
    private int nombre;

    /*General constructor of the fields table.*/
    public MethodsTab(){

    	MethodsTab=new HashMap<Integer, AbstractDeclMethod>();
    	nombre = 0;
    }

    /*A function that adds a new method to the hash map while associating it to a defined index.
     * @param indice index of the new method.
     * @param method  the new method to be added to the methods' table.
     */
	public void addMethod(int indice, AbstractDeclMethod method){
		//System.out.println(indice);
		MethodsTab.put(indice,method);
		method.getIdentifier().getMethodDefinition().setIndex(indice+2);
		nombre ++;
	}

    /*A function that returns a boolean to indicate if the methods' table contains
     * a specified key.
     * @param indice the index that we want to check if it exists on the methods' table.
     * @return a boolean that indicates true if the methods' table contains the given key.
    */
	public boolean hasKey(int i){
        return MethodsTab.containsKey(i);
    }

    /*A function that returns a method giving its index.
    * @param indice the index that we want to get the method of.
    * @return method that has the right index.
    */
    public AbstractDeclMethod getMethode(int i){
       return MethodsTab.get(i);
    }

    /*A function that returns the number of fields present in the methods' table.
     * @return the number of methods present on the methods' table.
     */
    public int getnombre() {
    	return this.nombre;
    }
}
