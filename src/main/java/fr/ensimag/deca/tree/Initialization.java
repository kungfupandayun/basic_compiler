package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * @author gl07
 * @date 01/01/2020
 */
public class Initialization extends AbstractInitialization {
	private static final Logger LOG = Logger.getLogger(Initialization.class);

	/**
	 * @return  the expression to be initialised
	 */
    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    /**
     * Set the expression to be assign to a variable
     * @param expression
     */
    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    /**
     * Store the expression to be assign to a variable
     * @param expression
     */
    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
    	LOG.debug("Verify Initialisation: start");
    	Type type = this.getExpression().verifyExpr(compiler, localEnv, currentClass);
    	// verifier que c'est pas une methode
    	 if (this.getExpression() instanceof Identifier) {
         	Identifier ident = (Identifier) this.getExpression();
         	Definition def = ident.getDefinition();
         	if (def.isMethod()) {
         		throw new ContextualError("[InitializationError] : " + ident.getName().getName()  + " is a method and not a variable",this.getLocation());
         	}
         }
    	if (t.assignCompatible(compiler.getEnvironmentType(), type)) {
    		
    		 if (t.isFloat() && type.isInt()){
        		ConvFloat conv = new ConvFloat(this.expression);
        		this.setExpression(conv);
        		this.getExpression().verifyExpr(compiler, localEnv, currentClass);
        	} else if (t.isClass() && type.isClass()){ // t1 est un sous type de t2
        		this.getExpression().setType(type);
        	} else if (t.sameType(type)) {
        		this.getExpression().setType(t);
        	} 
    	} else {
    		throw new ContextualError("[InitializationError] : " + type.getName().getName() + " is not a subtype of  " + t.getName().getName(), this.getLocation());
    	}
    	LOG.debug("Verify Initialisation: End");
    }

    //creer par nous meme
    @Override
    public void codeGenDeclVar(DecacCompiler compiler, GBmanager GB){
    	LOG.debug("CodeGen Initialisation");
    	compiler.addComment("initialisation ");
    	this.expression.codeGenExpr(compiler, GB);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("=");
        this.getExpression().decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }


}
