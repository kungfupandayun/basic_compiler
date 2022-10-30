package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.TSTO;



/**
 * instanceOF
 *
 * @author gl07
 * @date 19/01/2020
 */
public  class InstanceOf extends AbstractExpr{
	private AbstractIdentifier  type;
	private AbstractExpr expr;

	/**
	 * @param ident
	 * @param expr
	 *
	 */
	public InstanceOf( AbstractExpr expr, AbstractIdentifier ident) {
		this.expr = expr;
		this.type = ident;
	}

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {

		Type type1 = this.expr.verifyExpr(compiler, localEnv, currentClass);
		Type type2 = this.type.verifyType(compiler);

		if (!type1.typeInstanceOf(compiler.getEnvironmentType(), type2)) {
			throw new ContextualError("[InstanceOfError] : The InstanceOf operation cannot be performed between  " + type1.getName().getName() + "  and " + type2.getName().getName() , this.getLocation());
		}
		BooleanType typeReturn =  new BooleanType(new SymbolTable().create("boolean"));
		this.setType(typeReturn);
		return typeReturn;

	}

		@Override
		public void decompile(IndentPrintStream s) {
			// TODO Auto-generated method stub
			s.print(" (");
			expr.decompile(s);
			s.print(" instanceOf ");
			type.decompile(s);
			s.print(")");


		}

		@Override
		protected void prettyPrintChildren(PrintStream s, String prefix) {
			type.prettyPrint(s,prefix,true);
			expr.prettyPrint(s,prefix,true);
		}

		@Override
		protected void iterChildren(TreeFunction f) {
			type.iter(f);
			expr.iter(f);
		}

		@Override
		protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
	        if(expr.getDval()==null){
	            throw new DecacInternalError("element vide");
	        } else{
	        	
	        	boolean[] table = GB.getCopyTabRegister();
	            GPRegister register = Register.getR(GB.getValGB());
	            GPRegister register2;
	            if(GB.remplir()){
	            	if(!compiler.getCompilerOptions().isNoCheck()){
						 compiler.addInstruction(new TSTO(1));
						 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
					}
					compiler.addInstruction(new PUSH(register));
					register2 = Register.getR(GB.getLength()-1);
	            }
	            else{
	                register2 = Register.getR(GB.getValGB());
	            }

	            int i = compiler.getIndice();
	            compiler.addInstruction(new LOAD(expr.getDval(),register));
	            compiler.addInstruction(new LOAD(new RegisterOffset(0,register),register2));
	            compiler.addInstruction(new LEA(type.getClassDefinition().getOperand(),register2));
	            Label instance = new Label("Instanceof_Begin."+i);
	            compiler.addLabel(instance);
	            compiler.setLabel(i, instance);
	            compiler.addInstruction(new CMP(register,register2));
	            compiler.addInstruction(new BEQ(new Label("Instanceof_ok."+i)));
	          //les 3 lignes ajouté par rapport rendu final
	            compiler.addInstruction(new LOAD(new RegisterOffset(0, register), register)); 
                compiler.addInstruction(new CMP(new NullOperand(), register)); 
                compiler.addInstruction(new BNE(instance));
	            compiler.addInstruction(new LOAD(new ImmediateInteger(0),register));
	            compiler.addInstruction(new BRA(new Label("Instanceof_fini."+i)));
	            compiler.addLabel(new Label("Instanceof_ok."+i));
	            compiler.addInstruction(new LOAD(new ImmediateInteger(1),register));
	            compiler.addLabel(new Label("Instanceof_fini."+i));
	            if(GB.remplir()){
	            	compiler.addInstruction(new POP(register2));
	            }
	            GB.setGBmanager(table);

	        } 
		}

		@Override
	  protected void codeGenPrint(DecacCompiler compiler){
			//nothing to do
	  }
			@Override
		protected void codeGenPrintX(DecacCompiler compiler){
				//nothing to do
		}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
