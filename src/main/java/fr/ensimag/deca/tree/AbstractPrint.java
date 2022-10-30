package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import java.util.Iterator;
import org.apache.log4j.Logger;
import java.util.Random;
import org.apache.commons.lang.Validate;

/**
 * Abstract class for print statement (print, println, ...).
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractPrint extends AbstractInst {
	private static final Logger LOG = Logger.getLogger(AbstractPrint.class);

    private boolean printHex;
    private ListExpr arguments = new ListExpr();

    abstract String getSuffix();

    /**
     * Print out a list of arguments 
     * @param printHex 
     * @param arguments 
     */
    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    /**Return the list of the arguments to be printed
     *@return arguments
     **/
    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            		 throws ContextualError {
    	LOG.debug("verify Print: start");
    	if( !arguments.isEmpty()) {
    			
    	LOG.debug("list non null Print: start");
        Iterator<AbstractExpr> it = this.arguments.iterator();
        while(it.hasNext()){
            AbstractExpr exp = it.next();
            Type type = exp.verifyExpr(compiler, localEnv, currentClass);
            // si l'xpression est un identifier il faut qu'il soit pas
            // une methode
            if (exp instanceof Identifier) {
            	Identifier ident = (Identifier) exp;
            	Definition def = ident.getDefinition();
            	if (def instanceof MethodDefinition) {
            		throw new ContextualError("[PrintError] : A method cannot be printed ",exp.getLocation());
            	}
            }
            
            if(!(type.isFloat()||type.isInt()||type.isString()))
            	throw new ContextualError("[PrintError]  : " + type.getName() + " cannot be printed",exp.getLocation());
        	}
        	
    	}
        LOG.debug("verify Print: end");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr expr : getArguments().getList()) {
			if(this.getPrintHex() && expr.getType().isFloat())
					expr.codeGenPrintX(compiler);
			else
					expr.codeGenPrint(compiler);
		}
    }

    /** Return if the value should be printed in hexdecimal
     * @return printHex
     **/
    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	decompilePrint(s,this.getPrintHex());
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

    /** Method to be refined in the child classes.
     * @param s IndentPrintStream
     * @param x boolean
     **/
    public abstract void decompilePrint(IndentPrintStream s, boolean x);
}
