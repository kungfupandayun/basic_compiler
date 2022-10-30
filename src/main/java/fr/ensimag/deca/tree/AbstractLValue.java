package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;

/**
 * Left-hand side value of an assignment.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractLValue extends AbstractExpr {

	/**
	 * @return Definition of the class of left expression
	 */
   public abstract ClassDefinition getClassDefinition();

   /**
	 * @return Definition of left expression
	 */
   public abstract Definition getDefinition();

   /**
	 * @return Definition of the field of left expression
	 */
   public abstract FieldDefinition getFieldDefinition();

   /**
	 * @return Definition of the method of left expression
	 */
   public abstract MethodDefinition getMethodDefinition();

   /**
	 * @return Symbol table of left expression
	 */
   public abstract SymbolTable.Symbol getName();

   /**
	 * @return Expression Definition of left expression
	 */
   public abstract ExpDefinition getExpDefinition();

   /**
    * 
    * @return Variable definition
    */
   public abstract VariableDefinition getVariableDefinition();

   /**
    * Set the defintion of the left expression
    * @param definition
    */
   public abstract void setDefinition(Definition definition);

    /**
     * Returns a parametere definition.
     * @return
     */
   public abstract ParamDefinition getParamDefinition();


}
