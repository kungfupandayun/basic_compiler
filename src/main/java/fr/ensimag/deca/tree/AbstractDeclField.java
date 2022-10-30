package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Class declaration of field.
 *
 * @author gl07
 * @date 17/01/2020
 */
public abstract class AbstractDeclField extends Tree {
	
	 /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members fields is OK
     * without looking at field initialization.
     * @param compiler
     * @param env the environment of expression
     * @param name the name of class
     * @param i the index of parameter
     */
    protected abstract void verifyField(DecacCompiler compiler, EnvironmentExp env, Symbol name, int i)
            throws ContextualError;

   
    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     * @param compiler
     * @param env
     * @param currentClass
     * @throws ContextualError
     */
    protected abstract void verifyFieldInit (DecacCompiler compiler,EnvironmentExp env , ClassDefinition currentClass)
    		throws ContextualError;

    /**
     * Generate assembly code for the field.
     * 
     * @param compiler
     */
    protected abstract void codeGenField(DecacCompiler compiler);

    /**
     * Get the identifier of the class.
     * 
     * @return ident
     */
    public abstract AbstractIdentifier getident();
}
