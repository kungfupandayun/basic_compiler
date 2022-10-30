package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;


/**
 * Cast to represent conversion of class or type int/float
 *
 * @author gl07
 * @date 01/01/2020
 */
public  class Cast extends AbstractExpr{
	private static final Logger LOG = Logger.getLogger(Cast.class);
	private AbstractIdentifier  ident;
	private AbstractExpr expr;

	/**
	 * @param ident
	 * @param expr
	 * ident to represent the type and expr to represent the expression to be converted
	 */
	public Cast(AbstractIdentifier ident, AbstractExpr expr) {
		Validate.notNull(ident);
		Validate.notNull(expr);
		this.ident = ident;
		this.expr = expr;
	}

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		LOG.debug("VerifyCast: start");
		Type type = this.ident.verifyType(compiler);
		Type type2 = this.expr.verifyExpr(compiler, localEnv, currentClass);

		if (!type2.castCompatible(compiler.getEnvironmentType(), type)) {
			throw new ContextualError("[CastError] : Impossible to cast " + type2.getName().getName() + " in  " + type.getName().getName() , this.getLocation());
		}
		this.setType(ident.getType());
		LOG.debug("VerifyCast: End");
		return ident.getType();

	}

	@Override
	public void decompile(IndentPrintStream s) {
		s.print(" (");
		ident.decompile(s);
		s.print(") (");
		expr.decompile(s);
		s.print(")");


	}

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		ident.prettyPrint(s,prefix,true);
		expr.prettyPrint(s,prefix,true);
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		ident.iter(f);
		expr.iter(f);
	}

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table = GB.getCopyTabRegister();//enregistrer l'état init
		GPRegister register;
		if(!this.ident.getType().sameType(this.expr.getType())) {
            if (this.ident.getType().isFloat()) {
                this.expr.codeGenExpr(compiler, GB);
                register= Register.getR(GB.getValGB());
                compiler.addInstruction(new FLOAT(register, register));
            } else if (this.ident.getType().isClass()) {//cas class
            	this.expr.codeGenExpr(compiler, GB);
            	register= Register.getR(GB.getValGB());
            	int i = compiler.getIndice();
                Label cast_begin = new Label("Cast_begin." + i); 
                Label cast_failed = new Label("Cast_failed." + i);
                Label cast_fini = new Label("Cast_fini." + i);
                compiler.setLabel(i, cast_begin);
                if (this.ident.getDval() == null) {
                    throw new DecacInternalError("element vide");
                } else {
                	compiler.addComment("Cast");
                    boolean[] backup = GB.getCopyTabRegister();
                    GPRegister register2;
    	            if(GB.remplir()){
    	            	register2 = Register.getR(GB.getLength()-1);
    	            	if(!compiler.getCompilerOptions().isNoCheck()){
    						 compiler.addInstruction(new TSTO(1));
    						 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
    					}
    					compiler.addInstruction(new PUSH(register2));
    					this.expr.codeGenExpr(compiler, GB);
    					compiler.addInstruction(new LOAD(register2, Register.R0));
    					compiler.addInstruction(new POP(register2));

                    } else {
                    	this.expr.codeGenExpr(compiler, GB);
                    	register2 = Register.getR(GB.getValGB());
                    }

                    Label instance = new Label("Instanceof." + i);
                    //test instance of
                    compiler.addInstruction(new LOAD(new RegisterOffset(0, register2), register2));
                    compiler.addInstruction(new LEA(this.ident.getClassDefinition().getOperand(), register));
                    compiler.addLabel(instance);
                    compiler.addInstruction(new CMP(register2, register));
                    compiler.addInstruction(new BEQ(cast_begin)); 
                    compiler.addInstruction(new LOAD(new RegisterOffset(0, register2), register2)); 
                    compiler.addInstruction(new CMP(new NullOperand(), register2)); 
                    compiler.addInstruction(new BNE(instance));
                    compiler.addInstruction(new CMP(new NullOperand(), register2));
                    compiler.addInstruction(new BRA(cast_failed));

                    // Cast
                    compiler.addLabel(cast_begin);
                    this.expr.codeGenExpr(compiler, GB);
                    GPRegister register3 = Register.getR(GB.getValGB());
                    compiler.addInstruction(new LEA(this.ident.getClassDefinition().getOperand(), register2)); 
                    compiler.addInstruction(new STORE(register2, new RegisterOffset(0, register)));
                    compiler.addInstruction(new BRA(cast_fini));
                    compiler.addLabel(cast_failed);
                    compiler.addInstruction(new WSTR("Erreur: bad cast from " + expr.getType().toString() + " to " + this.ident.getName().toString()));
                    compiler.addInstruction(new WNL());
                    compiler.addInstruction(new ERROR());
                    compiler.addLabel(cast_fini);
                    compiler.addInstruction(new LOAD(register3, register));

                    GB.setGBmanager(backup);
                }


                } else if (this.getType().isInt()) {
                this.expr.codeGenExpr(compiler, GB);
                register= Register.getR(GB.getValGB());
                compiler.addInstruction(new INT(register, register));
            }
            else {
                compiler.addInstruction(new WSTR("Erreur: bad cast from " + expr.getType().toString() + " to " + this.getType().getName().toString()));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());

            }
        }
		GB.setGBmanager(table);

	}

	@Override
    protected void codeGenInst(DecacCompiler compiler) {
		GBmanager GB = compiler.getGBmanager();
		boolean[] table = GB.getCopyTabRegister();//enregistrer l'état init
		GPRegister register;
		if(!this.ident.getType().sameType(this.expr.getType())) {
            if (this.ident.getType().isFloat()) {
                this.expr.codeGenExpr(compiler, GB);
                register= Register.getR(GB.getValGB());
                compiler.addInstruction(new FLOAT(register, register));
            } else if (this.ident.getType().isClass()) {//cas class
            	this.expr.codeGenExpr(compiler, GB);
            	register= Register.getR(GB.getValGB());
            	int i = compiler.getIndice();
                Label cast_begin = new Label("Cast_begin." + i); 
                Label cast_failed = new Label("Cast_failed." + i);
                Label cast_fini = new Label("Cast_fini." + i);
                compiler.setLabel(i, cast_begin);
                if (this.ident.getDval() == null) {
                    throw new DecacInternalError("element vide");
                } else {
                	compiler.addComment("Cast");
                    boolean[] backup = GB.getCopyTabRegister();
                    GPRegister register2;
    	            if(GB.remplir()){
    	            	register2 = Register.getR(GB.getLength()-1);
    	            	if(!compiler.getCompilerOptions().isNoCheck()){
    						 compiler.addInstruction(new TSTO(1));
    						 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
    					}
    					compiler.addInstruction(new PUSH(register2));
    					this.expr.codeGenExpr(compiler, GB);
    					compiler.addInstruction(new LOAD(register2, Register.R0));
    					compiler.addInstruction(new POP(register2));

                    } else {
                    	this.expr.codeGenExpr(compiler, GB);
                    	register2 = Register.getR(GB.getValGB());
                    }

                    Label instance = new Label("Instanceof." + i);
                    //test instance of
                    compiler.addInstruction(new LOAD(new RegisterOffset(0, register2), register2));
                    compiler.addInstruction(new LEA(this.ident.getClassDefinition().getOperand(), register));
                    compiler.addLabel(instance);
                    compiler.addInstruction(new CMP(register2, register));
                    compiler.addInstruction(new BEQ(cast_begin)); 
                    compiler.addInstruction(new LOAD(new RegisterOffset(0, register2), register2)); 
                    compiler.addInstruction(new CMP(new NullOperand(), register2)); 
                    compiler.addInstruction(new BNE(instance));
                    compiler.addInstruction(new CMP(new NullOperand(), register2));
                    compiler.addInstruction(new BRA(cast_failed));

                    // Cast
                    compiler.addLabel(cast_begin);
                    this.expr.codeGenExpr(compiler, GB);
                    GPRegister register3 = Register.getR(GB.getValGB());
                    compiler.addInstruction(new LEA(this.ident.getClassDefinition().getOperand(), register2)); 
                    compiler.addInstruction(new STORE(register2, new RegisterOffset(0, register)));
                    compiler.addInstruction(new BRA(cast_fini));
                    compiler.addLabel(cast_failed);
                    compiler.addInstruction(new WSTR("Erreur: bad cast from " + expr.getType().toString() + " to " + this.ident.getName().toString()));
                    compiler.addInstruction(new WNL());
                    compiler.addInstruction(new ERROR());
                    compiler.addLabel(cast_fini);
                    compiler.addInstruction(new LOAD(register3, register));

                    GB.setGBmanager(backup);
                }


                } else if (this.getType().isInt()) {
                this.expr.codeGenExpr(compiler, GB);
                register= Register.getR(GB.getValGB());
                compiler.addInstruction(new INT(register, register));
            }
            else {
                compiler.addInstruction(new WSTR("Erreur: bad cast from " + expr.getType().toString() + " to " + this.getType().getName().toString()));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());

            }
        }
		GB.setGBmanager(table);
	}

	@Override
  protected void codeGenPrint(DecacCompiler compiler){
	  GBmanager GB = compiler.getGBmanager();
      boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
      if(GB.remplir()) {
				GPRegister register = Register.getR(GB.getLength()-1);
				//compiler.addInstruction(new PUSH(register));
				this.expr.codeGenExpr(compiler,GB);
				if(this.getType().isFloat()) {
		            compiler.addInstruction(new FLOAT(register, Register.R1));
		            compiler.addInstruction(new WFLOAT());
		        }else {
		            compiler.addInstruction(new INT(register, Register.R1));
		            compiler.addInstruction(new WINT());
		        }
				//compiler.addInstruction(new LOAD(register, Register.R0));
				//compiler.addInstruction(new POP(register));
      }
      else{
      	GPRegister register = Register.getR(GB.getValGB());
				this.expr.codeGenExpr(compiler, GB);
				if(this.getType().isFloat()) {
            compiler.addInstruction(new FLOAT(register, Register.R1));
            compiler.addInstruction(new WFLOAT());
        }else {
            compiler.addInstruction(new INT(register, Register.R1));
            compiler.addInstruction(new WINT());
        }
      }
      GB.setGBmanager(table);
  }
		@Override
		protected void codeGenPrintX(DecacCompiler compiler){
			GBmanager GB = compiler.getGBmanager();
			boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
			if(this.getType().isFloat()){
				if(GB.remplir()) {
					GPRegister register = Register.getR(GB.getLength()-1);
					//compiler.addInstruction(new PUSH(register));
					this.expr.codeGenExpr(compiler,GB);
					compiler.addInstruction(new FLOAT(register, Register.R1));
					compiler.addInstruction(new WFLOATX());
					//compiler.addInstruction(new LOAD(register, Register.R0));
					//compiler.addInstruction(new POP(register));
				}
				else{
					GPRegister register = Register.getR(GB.getValGB());
					this.expr.codeGenExpr(compiler, GB);
					compiler.addInstruction(new FLOAT(register, Register.R1));
					compiler.addInstruction(new WFLOATX());
				}
			}
			GB.setGBmanager(table);
		}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
