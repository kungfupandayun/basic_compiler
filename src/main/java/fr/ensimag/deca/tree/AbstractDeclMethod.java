package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Class declaration.
 *
 * @author gl07
 * @date 17/01/2020
 */
public abstract class AbstractDeclMethod extends Tree {

	/**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members 
     * methods is OK, without looking at method body .
     * @param compiler
     * @param def the class definition of super class
     * @param env
     * @param i index of method
     * @throws ContextualError
     */
    protected abstract void verifyMethod(DecacCompiler compiler,ClassDefinition def, EnvironmentExp env, int i)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     * @param compiler
     * @param env
     * @param currentClass
     * @throws ContextualError
     */
    protected abstract void verifyMethodBody (DecacCompiler compiler,EnvironmentExp env , ClassDefinition currentClass) 
    		throws ContextualError;

    /**
     * Get the identifier of the class.
     * 
     * @return ident
     */
    public abstract AbstractIdentifier getIdentifier();

    /**
     * Generate assembly code for the method.
     * 
     * @param compiler
     */
    protected abstract void codeGenMethod(DecacCompiler compiler);
}
