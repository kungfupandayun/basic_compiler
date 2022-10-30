package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import java.util.Random;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl07
 * @date 01/01/2020
 */
public class FloatLiteral extends AbstractExpr {

	/**
	 *
	 * @return  the float value
	 */
    public float getValue() {
        return value;
    }

    private float value;

    /**
     * Store the float valu
     * @param value
     */
    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    /**
     * @return type Float
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
              this.setType(compiler.getEnvironmentType().get(compiler.getEnvironmentType().getSymbolTab().create("float")).getType());
            	return this.getType();
    }


    
    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateFloat(this.getValue()), Register.R1)); //on passe par R1
        compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateFloat(this.getValue()), Register.R1)); //on passe par R1
        compiler.addInstruction(new WFLOATX());
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(java.lang.Float.toHexString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
		compiler.addInstruction(new LOAD(this.getDval(),Register.getR(GB.getValGB())));
		GB.setGBmanager(table);

	}

	@Override
	public DVal getDval() {
		return new ImmediateFloat(this.value);
	}

}
