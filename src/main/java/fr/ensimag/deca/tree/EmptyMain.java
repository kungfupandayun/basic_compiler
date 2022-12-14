package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Empty main Deca program
 *
 * @author gl07
 * @date 01/01/2020
 */
public class EmptyMain extends AbstractMain {
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
    	// C'est un terminal
    	//throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
    	// le main est vide rien à faire 
        //throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        // no main program => nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

	@Override
	public int valSP() {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public int varGlobale() {
        // TODO Auto-generated method stub
        return 0;
    }


}
