package fr.ensimag.deca.context;

import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import java.util.*;


/**
 * Dictionary associating identifier's TypeDefinition to their names.
 *
 * @author gl07
 * @date 10/01/2020
 */
public class EnvironmentType {

    private SymbolTable symbolTab;
    private HashMap<Symbol, TypeDefinition> symbolsDef;

    /**
     * initialise the  type environment by adding the original defined types
     * 
     */

    public EnvironmentType() {
    	this.symbolsDef = new HashMap<Symbol, TypeDefinition>();
    	this.symbolTab = new SymbolTable();
    	Symbol object = this.symbolTab.create("Object");
    	Symbol intPredef = this.symbolTab.create("int");
    	Symbol floatPredef = this.symbolTab.create("float");
    	Symbol booleanPredef = this.symbolTab.create("boolean");
    	Symbol voidPredef = this.symbolTab.create("void");
    	ClassDefinition classDef = new ClassDefinition(new ClassType(object), Location.BUILTIN, null);
      this.symbolsDef.put(object, classDef);
      EnvironmentExp env = classDef.getMembers();
      Symbol sym =  env.getSymbolTab().create("equals");
      Signature sig = new Signature();
      sig.add(new ClassType(object));
	  Type bool = new BooleanType(this.symbolTab.create("boolean"));
      MethodDefinition defM = new MethodDefinition(bool,classDef.getLocation(),sig,0);
      classDef.incNumberOfMethods();
      env.getSymbolsDef().put(sym, defM);
      this.symbolsDef.put(intPredef, new TypeDefinition(new IntType(intPredef),  Location.BUILTIN));
      this.symbolsDef.put(floatPredef,new TypeDefinition(new FloatType(floatPredef),  Location.BUILTIN));
      this.symbolsDef.put(booleanPredef, new TypeDefinition(new BooleanType(booleanPredef),  Location.BUILTIN));
      this.symbolsDef.put(voidPredef,new TypeDefinition(new VoidType(voidPredef),  Location.BUILTIN));

    }

    /**
   * add a new TypeDefinition in the list
   * @param sym symbol
   * @param typeDef TypeDefinition
   **/
    public void addTypeDefinition(Symbol sym,TypeDefinition typeDef){
    	if (!this.symbolsDef.containsKey(sym)) {
    		this.symbolsDef.put(sym, typeDef);
    	}
    	// symbol already exists in the environment, throw an exception
    }


    /**
   * get the typeDefiniiton corresponding to the symbol
   * @param key
   * @return typeDefinition
   **/
    public TypeDefinition get(Symbol key) {
    	Symbol sym = this.symbolTab.create(key.getName());
        if (this.symbolsDef.containsKey(sym)){
        	return this.symbolsDef.get(sym);
        }

        return null;
    }


      /**
     * get the table of symbols
     * @return the table of symbols
     **/
  	public SymbolTable getSymbolTab() {
  		return symbolTab;
  	}


   /**
   * set the table of symbols
   * @param symbolTab
   **/
  	public void setSymbolTab(SymbolTable symbolTab) {
  		this.symbolTab = symbolTab;
  	}

   /**
   * get the map of symbols and its typeDefiniiton
   * @return map of symbols and typeDefinition
   **/
  	public HashMap<Symbol, TypeDefinition> getSymbolsDef() {
  		return symbolsDef;
  	}


    /**
   * set the table of symbols and typeDefiniiton
   * @param symbolsDef
   **/
  	public void setSymbolsDef(HashMap<Symbol, TypeDefinition> symbolsDef) {
  		this.symbolsDef = symbolsDef;
  	}

    /**
     * This fonction shows the contents of the environment 
     */
    public void afficher() {
	   Collection<Symbol> s = this.symbolsDef.keySet();
	   Iterator<Symbol> i = s.iterator();
	   while (i.hasNext()) {
		   Symbol e = i.next();
		   System.out.println(e);
	   }
   }

}
