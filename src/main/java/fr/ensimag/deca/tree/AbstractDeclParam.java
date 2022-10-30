package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;

/**
 * Parameter declaration
 *
 * @author gl07
 * @date 17/01/2020
 */
public abstract class AbstractDeclParam extends Tree {
	
	
	/**
     * add a parameter's type in the signature
     * @param compiler contains "env_types" attribute
     * @param sig a signature
     * @throws ContextualError
     */
	protected abstract void addInSignature(DecacCompiler compiler, Signature sig) throws ContextualError;

    /**
     * Implements non-terminal "decl_Param" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv
     * @throws ContextualError 
     */
    protected abstract void verifyDeclParam(DecacCompiler compiler,  EnvironmentExp localEnv) throws ContextualError;

  /**
   * Generate assembly code for the param.
   * @param compiler
   */
    protected abstract void codeGenDeclParam(int indice, DecacCompiler compiler);
}
