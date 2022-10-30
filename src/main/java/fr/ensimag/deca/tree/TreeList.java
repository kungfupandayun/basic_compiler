package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.Validate;

/**
 * An abstract class to all the Class List 
 * in the project 
 * @author gl07
 * @date 01/01/2020
 */
public abstract class TreeList<TreeType extends Tree> extends Tree {
    /*
     * We could allow external iterators by adding
     * "implements Iterable<AbstractInst>" but it's cleaner to provide our own
     * iterators, to make sure all callers iterate the same way (Main,
     * IfThenElse, While, ...). If external iteration is needed, use getList().
     */
	
	/**
	 * Declaration of a List from java.util.Collections
	 * of type TreeType which is the Abstract type of most of the 
	 * classes in the project
	 */
    private List<TreeType> list = new ArrayList<TreeType>();

    /**
     * Add a new member into the list 
     * The new member must have the same type as the list
     * @param treetype
     */
    public void add(TreeType i) {
        Validate.notNull(i);
        list.add(i);
    }

    /**
     * @return the list contained in the class, read-only. Use getModifiableList()
     *         if you need to change elements of the list.
     */
    public List<TreeType> getList() {
        return Collections.unmodifiableList(list);
    }

    /**
     * Set the element at position index in the list
     * Delete the element at position index if its already written
     * @param Number index
     * @param Element of the type as the list
     */
    public TreeType set(int index, TreeType element) {
        return list.set(index, element);
    }

    /**
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Iterate the list  
     * @return Iterator
     */
    public Iterator<TreeType> iterator() {
        return list.iterator();
    }
    
    /**
     * @return the size of the list
     */
    public int size() {
        return list.size();
    }

    /**
     * Do not check anything about the location.
     * 
     * It is possible to use setLocation() on a list, but it is also OK not to
     * set it.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    protected String prettyPrintNode() {
        return super.prettyPrintNode() +
                " [List with " + getList().size() + " elements]";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        int count = getList().size();
        for (TreeType i : getList()) {
            i.prettyPrint(s, prefix, count == 1, true);
            count--;
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        for (TreeType i : getList()) {
            i.iter(f);
        }
    }

}
