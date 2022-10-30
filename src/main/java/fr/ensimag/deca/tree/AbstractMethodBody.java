package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Declaration of a method (asm method and normal method)
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractMethodBody extends Tree {
	
    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     * @param compiler
     * @param envParam
     * @param currentClass
     * @throws ContextualError
     */
    protected abstract void verifyMethodBody (DecacCompiler compiler, EnvironmentExp envParam, ClassDefinition currentClass, Type returnType) 
    		throws ContextualError;
    
    
    
    public abstract int valSP();
    
    protected abstract void codeGenMethod(DecacCompiler compiler);
}
