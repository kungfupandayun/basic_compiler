package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Class declaration.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractDeclClass extends Tree {

    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Generate assembly code for the initialization of the class.
     * 
     * @param compiler
     */
    protected abstract void codeGenInitDeclClass(DecacCompiler compiler);

    /**
     * Generate assembly code for the initialization of the field.
     * 
     * @param compiler
     */
    protected abstract void codeGenFieldClass(DecacCompiler compiler);

    /**
     * Generate assembly code for the initialization of the method.
     * 
     * @param compiler
     */
    protected abstract void codeGenMethodClass(DecacCompiler compiler);

    /**
     * Calcul of the SP value.
     * 
     * @return valeur of SP
     */
    public abstract int valSP();

    /**
     * Get the identifier of the class.
     * 
     * @return identifier
     */
    public abstract AbstractIdentifier getident();

}
