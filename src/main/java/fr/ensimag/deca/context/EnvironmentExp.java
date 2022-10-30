package fr.ensimag.deca.context;



import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 *
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 *
 * The dictionary at the head of this list thus corresponds to the "current"
 * block (eg class).
 *
 * Searching a definition (through method get) is done in the "current"
 * dictionary and in the parentEnvironment if it fails.
 *
 * Insertion (through method declare) is always done in the "current" dictionary.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class EnvironmentExp {

	  private HashMap<Symbol,ExpDefinition> symbolsDef;
	  private SymbolTable symbolTab;
	  private EnvironmentExp parentEnvironment;

	
	/**
	 * This constructor create the
	 * first environment in the program,
	 * it doesn't have a parent
	 */
    public EnvironmentExp() {
    	this.symbolsDef = new HashMap<Symbol,ExpDefinition>();
    	this.symbolTab = new SymbolTable();

    }
    /**
     * This Constructor create an environment that does have a parent
     * @param parentEnvironment The parent of this environment
     */
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
    	this.symbolsDef = new HashMap<Symbol,ExpDefinition>();
    	this.symbolTab = new SymbolTable();
        this.parentEnvironment = parentEnvironment;
    }
    
   /**
    * This Constructor create an environment by giving it's table of definition
    * @param symbolsDef
    * @param sym
    */
   public EnvironmentExp(HashMap<Symbol,ExpDefinition> symbolsDef, SymbolTable sym) {
	   this.symbolsDef = symbolsDef;
	   this.symbolTab= sym;
   }

   /**
    * This class is used when there is a double definition of a symbol
    * @author gl07
    *
    */
    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;

        public DoubleDefException(String message) {
        	super(message);
        }
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
	 * @param key the symbol
	 * @return the ExpDefiniiton of this symbol, if it doesn't exist, return null
     */
    public ExpDefinition get(Symbol key) {
    	if (this.symbolsDef.containsKey(key) ){
        	return this.symbolsDef.get(key);
        }

        return null;
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
    	if (!this.symbolsDef.containsKey(name)) {
    		
    		this.symbolsDef.put(name, def);
    	}else {
    		throw new DoubleDefException( "Erreur double défnition  : " + name.getName() + "  est déja définie");
    	}
    }
    
	/**
	 * Get the map in the environment
	 * @return the abstract syntax tree
	 **/
		public HashMap<Symbol, ExpDefinition> getSymbolsDef() {
			return symbolsDef;
		}

	/**
	 * set the map of symbol and ExpDefinition
	 * @param symbolsDef
	 **/
		public void setSymbolsDef(HashMap<Symbol, ExpDefinition> symbolsDef) {
			this.symbolsDef = symbolsDef;
		}

		/**
		 * get the table of symbol in the environment of expression
		 * @return the table
		 **/
		public SymbolTable getSymbolTab() {
			return symbolTab;
		}

		/**
	 * set the table of symbol
	 * @param symbolTab
	 **/
		public void setSymbolTab(SymbolTable symbolTab) {
			this.symbolTab = symbolTab;
		}

	/**
	 * Get the parent's environment
	 * @return
	 **/
		public EnvironmentExp getParentEnvironment() {
			return parentEnvironment;
		}

	/**
	 * set the super class's environment
	 * @param parentEnvironment
	 **/
		public void setParentEnvironment(EnvironmentExp parentEnvironment) {
			this.parentEnvironment = parentEnvironment;
		}

		/**
		 * This fonction is used to show the contents of the environments
		 */
	   public void affichage() {
		   Collection<Symbol> s = this.symbolsDef.keySet();
		   Iterator<Symbol> i = s.iterator();
		   while (i.hasNext()) {
			   Symbol e = i.next();
			   System.out.println(e);
		   }
	   }
	   
	   /**
	    * Change the definition of symbol in the environment
	    * @param s The symbol key
	    * @param def The new definition
	    */
		public void setDefinitionOfSymobl(Symbol s, ExpDefinition def) {
			// change la definition dans l'environment
			if (this.symbolsDef.containsKey(s)) {
				this.symbolsDef.remove(s);
				this.symbolsDef.put(s, def);
			}
			
		}
		/**
		 * Look for the EXPression definition in all environments starting from the local Environment
		 * @param key the symbol we are looking forit's definition
		 * @return null if there is not a definition , the definition in the other case 
		 */
		public ExpDefinition getParentsDef(Symbol key) {
				EnvironmentExp courant = this;
				while ((courant != null) && (courant.get(key) == null)) {
					courant = courant.getParentEnvironment();
				}
		    	if (courant != null) {
		    		return courant.get(key);
		    	}else {
		    		return null;
		    	}
		        
	    }
}
