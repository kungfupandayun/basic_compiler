package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BOV;

import java.io.PrintStream;

/**
 * Read a float value from the user's entry
 * @author gl07
 * @date 01/01/2020
 */
public class ReadFloat extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	Type type= new FloatType(localEnv.getSymbolTab().create("float"));
    	this.setType(type);
    	return type;
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Type type = this.verifyExpr(compiler, localEnv, currentClass);
        if (!type.isFloat()) {
        	throw new ContextualError( "[ReadFloatError] : a float is expected", this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readFloat()");
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
		compiler.addInstruction(new RFLOAT());
    if(!compiler.getCompilerOptions().isNoCheck()){
      Label error = new Label("io_error");
      compiler.addInstruction(new BOV(error));
    }
    compiler.addInstruction(new LOAD(Register.R1, Register.getR(GB.getValGB())));
    GB.setGBmanager(table);
	}

  @Override
	protected void codeGenInst(DecacCompiler compiler) {
		compiler.addInstruction(new RFLOAT());
	}

  @Override
  protected void codeGenPrint(DecacCompiler compiler) {
      this.codeGenInst(compiler);
      compiler.addInstruction(new WFLOAT());
  }
	@Override
	public DVal getDval() {
		return null;
	}

}
