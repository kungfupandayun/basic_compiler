package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Main block of a Deca program.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractMain extends Tree {

    protected abstract void codeGenMain(DecacCompiler compiler);


    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3 
     * @param compiler
     * @throws ContextualError
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;

    /**
     * Function that returns the SP value of the main program.
     * @return int the SP value of the main.
     */
    public abstract int valSP();

    /**
     * A function that returns the number of variable the main funtion of a program contains.
     * @return int the number of global variables that the main of a program contains.
     */
    public abstract int varGlobale();

    }
