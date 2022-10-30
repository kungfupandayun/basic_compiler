package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl07
 * @date 01/01/2020
 */

public abstract class AbstractExpr extends AbstractInst {

    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     * @return type decoration
     */
    public Type getType() {
        return type;
    }

    /**
     *  Set the type decoration associated to this expression
     * @param type
     */
    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     *
     * implements non-terminals "expr" and "lvalue"
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    
    /**
     * Verify the expression in right hand-side of (implicit) assignments
     *
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass,
            Type expectedType)
            throws ContextualError {
        Type type2 = this.verifyExpr(compiler, localEnv, currentClass);
    
        if (expectedType.assignCompatible(compiler.getEnvironmentType(), type2)) {

        	this.setType(type2);
        	return this;
        }else {
        	throw new ContextualError("[AssignCompatibleError] :" + expectedType.getName().getName() + " and " + type2.getName().getName() + " are not compatible for assignment",this.getLocation());
        }

    }


	
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

    	verifyExpr(compiler,localEnv,currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // verifier la condition et tester que l'expression renvoie un boolean
    	Type type = this.verifyExpr(compiler, localEnv, currentClass);
    	if (!type.isBoolean()) {
    		throw new ContextualError("[ConditionError] : "+ type.getName().getName() + " must be a boolean", this.getLocation());
    	}
    	this.setType(type);
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
    }

    /**
     * Generate code to printX the expression
     *
     * @param compiler
     */
    protected void codeGenPrintX(DecacCompiler compiler) {
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
    }

    /**
     * Permet de gerer l'instruction quand nous voulons faire l'instrcution en cas Vrai
     *
     * @param compiler compiler ou nous travaillons
     */
    protected void codeGenInstNot(DecacCompiler compiler) {

    }

    /**
     * Permet de gerer le code qui permet de faire la declaration de variables
     *
     * @param compiler compiler ou nous travaillons
     * @param GB GB manager qui permet de donner le numero de register
     */
    protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
    }

    /**
     * Return un Dval, si c'est int/float/boolean/identifier, il ne va pas return null.
     *
     @return Dval de variable
     */
    public DVal getDval() {
    	return null;
    }


    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    protected void decompileExp(IndentPrintStream s) {
        decompile(s);

    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
    





}
