package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;


/**
 * @author gl07
 * @date 15/01/2020
 */
public class Selection extends AbstractLValue{
	private AbstractExpr expr;
	private AbstractIdentifier ident;

	public Selection(AbstractExpr expr, AbstractIdentifier ident) {
		Validate.notNull(ident);
		Validate.notNull(expr);
		this.expr = expr;
		this.ident = ident; // field_ident
	}
	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		
		Type typeS = this.expr.verifyExpr(compiler,localEnv,currentClass); //check ClassType class2 of this selection

		if (compiler.getEnvironmentType().get(typeS.getName())==null) {
			throw new ContextualError("[SelectionError] : The class "+typeS.getName()+" is not defined", this.expr.getLocation());
		}
		if (! (typeS.isClass())) {
			throw new ContextualError("[SelectionError] : " + typeS.getName()+" is not a class", this.expr.getLocation());
		}
		/* Check the filed or Method selected */
		ClassDefinition defS = (ClassDefinition) compiler.getEnvironmentType().get(typeS.getName()); // the definition of the selection
		EnvironmentExp envC = defS.getMembers(); //env_exp2
		Definition defF = ident.verifyFieldIdent(compiler, envC);
		
	    FieldDefinition defField =  (FieldDefinition) defF;
	    if (defField.getVisibility()==Visibility.PROTECTED) {
	    	// We call a field directly for one instruction. like print(A.x)
	    	if (currentClass == null) {
	    		throw new ContextualError("[FieldAccessError] : " + ident.getName()+" is protected in the class" + typeS.getName(), this.ident.getLocation());
	    	}
	    	ClassType thisClass = currentClass.getType();
	    	ClassDefinition classF = defField.getContainingClass();
	    	ClassType classField = classF.getType(); // The type of the class containing this field	    	
	    	ClassType class2 = defS.getType();
	    	if (!class2.subType(compiler.getEnvironmentType(), thisClass)|| !thisClass.subType(compiler.getEnvironmentType(), classField)) {
	    		throw new ContextualError( "[FieldAccessError] "+ident.getName()+" is protected", this.ident.getLocation());
	    	}
	    }
		
		this.setType(defF.getType());
		this.setDefinition(defF);
		return defF.getType();
	}

	@Override
	public void decompile(IndentPrintStream s) {
		expr.decompile(s);
		s.print(".");
		ident.decompile(s);

	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		expr.prettyPrint(s, prefix, false);
		ident.prettyPrint(s, prefix, false);

	}

	@Override
	protected void iterChildren(TreeFunction f) {
		expr.iter(f);
		ident.iter(f);

	}
	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
        GPRegister register;

		compiler.addComment("Selection");
		//si Registres sont tous utilisé
        if(GB.remplir()) {
			register = Register.getR(GB.getLength()-1);
			if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.expr.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
		}
		else {//sinon
			this.expr.codeGenExpr(compiler, GB);
			register = Register.getR(GB.getValGB());
		}
        compiler.addInstruction(new LOAD(new RegisterOffset(ident.getFieldDefinition().getIndex()+1,register),register));
        GB.setGBmanager(table);

	}
	@Override
    protected void codeGenInst(DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
        boolean[] table= GB.getCopyTabRegister(); 
        GPRegister register;
        compiler.addComment("Selection");
      //si Registres sont tous utilisé
        if(GB.remplir()) {
			register = Register.getR(GB.getLength()-1);
			if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.expr.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
		}
		else {//sinon
			this.expr.codeGenExpr(compiler, GB);	
			register = Register.getR(GB.getValGB());
		}
        compiler.addInstruction(new CMP(new NullOperand(),register));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
        GB.setGBmanager(table);;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
    	GBmanager GB = compiler.getGBmanager();
        this.codeGenExpr(compiler,GB);
        if(this.getType().isFloat()){
			compiler.addInstruction(new LOAD(Register.getR(GB.getValGB()),Register.R1));
            compiler.addInstruction(new WFLOAT());
        }
        else{
        	compiler.addInstruction(new LOAD(Register.getR(GB.getValGB()),Register.R1));
            compiler.addInstruction(new WINT());
        }
    }

		@Override
		protected void codeGenPrintX(DecacCompiler compiler){
			GBmanager GB = compiler.getGBmanager();
			this.codeGenExpr(compiler,GB);
			compiler.addInstruction(new LOAD(Register.getR(GB.getValGB()),Register.R1));
			compiler.addInstruction(new WFLOATX());
		}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ClassDefinition getClassDefinition() {
		return this.ident.getClassDefinition();
	}
	@Override
	public Definition getDefinition() {
		return this.ident.getDefinition();
	}
	@Override
	public FieldDefinition getFieldDefinition() {
		return this.ident.getFieldDefinition();
	}
	@Override
	public MethodDefinition getMethodDefinition() {
		return this.ident.getMethodDefinition();
	}
	@Override
	public Symbol getName() {
		return this.ident.getName();
	}
	@Override
	public ExpDefinition getExpDefinition() {
		return this.ident.getExpDefinition();
	}
	@Override
	public VariableDefinition getVariableDefinition() {
		return this.ident.getVariableDefinition();
	}
	@Override
	public void setDefinition(Definition definition) {
		this.ident.setDefinition(definition);
	}
	@Override
	public ParamDefinition getParamDefinition() {
		return this.getParamDefinition();
	}


}
