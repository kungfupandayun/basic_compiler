package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * A class to represent a string in the programme
 *
 * @author gl07
 * @date 01/01/2020
 */
public class StringLiteral extends AbstractStringLiteral {

	/**A private attribute to save the value of the string*/
    private String value;

    /**
     * Constructor of the class
     * @param String value
     * Assign the parameter to the private attribute
     */
    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value.substring(1, value.length()-1);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        this.setType(new StringType(localEnv.getSymbolTab().create("String")));
    		return new StringType(localEnv.getSymbolTab().create("String"));
    }

    
    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
            s.print('"'+value+'"');
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
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		// TODO Auto-generated method stub

	}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
