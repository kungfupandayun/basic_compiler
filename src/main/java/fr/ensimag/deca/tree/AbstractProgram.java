package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Entry point for contextual verifications and code generation from outside the package.
 * 
 * @author gl07
 * @date 01/01/2020
 *
 */
public abstract class AbstractProgram extends Tree {
	/**
	 * Verify the type environment and the expression environment of the program
	 * @param compiler
	 * @throws ContextualError
	 */
    public abstract void verifyProgram(DecacCompiler compiler) throws ContextualError;
    /**
     * Generate assembly code
     * @param compiler
     */
    public abstract void codeGenProgram(DecacCompiler compiler) ;

}
