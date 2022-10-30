package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl07
 * @date 01/01/2020
 */
public class Signature {
	/**
	 * The signature is created with 0 args 
	 */
    List<Type> args = new ArrayList<Type>();
    private static final Logger LOG = Logger.getLogger(Signature.class);
    
    /**
     * Add a new type to the signature
     * @param t
     */
    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }
    
    /**
     * get the list of arguments in the signature
     * @return list of type
     **/
    public List<Type> getArgs() {
    	return args;
    }
    
    /**
     * Compare two signatures
     * @param sig other signature
     * @return true if two signatures are the same
     **/
    @Override
    public boolean equals(Object object) {
    	if (object instanceof Signature) {
    		Signature sig = (Signature) object;
    		LOG.debug("verify Signature");
        	boolean equal = true;
        	if (this.size()!=sig.size()) {
        		return false;
        	}
        	int i = 0;
        	for (Type t: Collections.unmodifiableList(sig.getArgs())) {
        		if (this.paramNumber(i).sameType(t)) {
        			equal = true && equal;
        		} else {
        			return false;
        		}
        		i++;
        	}
        	return equal;
    	}
    	return false;
    	
    }
    
    /**
     * Show the Method signature
     */
    public void afficher() {
    	for (Type t: Collections.unmodifiableList(this.getArgs())) {
    		System.out.println(t.getName());

    	}
    }

}
