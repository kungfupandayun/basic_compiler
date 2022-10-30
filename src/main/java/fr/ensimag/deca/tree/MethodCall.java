package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Methode Call
 * @author gl07
 * @date 20/01/2020
 */
public class MethodCall extends AbstractExpr{
	private static final Logger LOG = Logger.getLogger(MethodCall.class);
	private AbstractExpr expr;
	private AbstractIdentifier ident;
	private ListExpr listExpr;
	
	public MethodCall(AbstractExpr expr, AbstractIdentifier ident, ListExpr listExpr) {
		Validate.notNull(ident);
		Validate.notNull(listExpr);
		this.expr = expr; // can be null 
		this.ident = ident;  // methode_ident
		this.listExpr = listExpr; // rvalue_star // can be Null
	}

	@Override
	public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
			throws ContextualError {
		Type typeS;
		LOG.debug("Verify Methode Call : start");
		if ((this.expr == null) && (currentClass == null)) {
			throw new ContextualError("[MethdoCallError] : the class  is not defined", this.ident.getLocation());
		}
		else if (this.expr == null && (currentClass != null)) {
			typeS = currentClass.getType();
		}
		else {
			typeS = this.expr.verifyExpr(compiler,localEnv,currentClass); //check ClassType of this selection
		}
		if (compiler.getEnvironmentType().get(typeS.getName())==null) {
			throw new ContextualError("[MethdoCallError] : the class "+typeS.getName()+" is not defined", this.expr.getLocation());
		}
		if (! (typeS.isClass())) {
			throw new ContextualError("[MethdoCallError] : " + typeS.getName()+"is not a class ", this.expr.getLocation());
		}
		ClassDefinition defC = (ClassDefinition) compiler.getEnvironmentType().get(typeS.getName());
		EnvironmentExp envC = defC.getMembers(); //env_exp2 de class2
		Definition defF = ident.verifyMethodIdent(compiler, envC);
		MethodDefinition defM = (MethodDefinition) defF;
		if (defM.getSignature().size() != listExpr.size()) {
			throw new ContextualError("[MethodCallError] : The method called has a  different signature than that of its call", this.getLocation());
		} else {
			this.listExpr.verifyListRValueStar(compiler, localEnv, currentClass, defM.getSignature()); // vérifier la signature
		}
		this.setType(defM.getType());
		LOG.debug("Verify Methode Call : End");
		return defM.getType();
		
	}

	@Override
	public void decompile(IndentPrintStream s) {
		if (expr != null) {
			expr.decompile(s);
			s.print(".");
		}
		ident.decompile(s);
		s.print("(");
		listExpr.decompile(s);
		s.print(")");
	}
	
	@Override
    public void codeGenExpr(DecacCompiler compiler,GBmanager GB){
        boolean[] table=GB.getCopyTabRegister(); //Enregistrer l'etat init
        compiler.addInstruction(new TSTO(3+listExpr.size()));
        compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
        compiler.addInstruction(new ADDSP(1+listExpr.size()));
        expr.codeGenExpr(compiler,GB);
        GPRegister register;
        if(GB.remplir()){//si tous les registrer sont utilisé
        	register = Register.getR(GB.getLength()-1);
        	if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.expr.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
			
        } else {
        	if(this.expr!=null) {
        		this.expr.codeGenExpr(compiler, GB);
        		register = Register.getR(GB.getValGB());
        	}else {
        		register = Register.getR(GB.getValGB());
        		compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),register));   		
        	}	
        	
        }
        compiler.addInstruction(new STORE(register,new RegisterOffset(0,Register.SP)));
        if(!listExpr.isEmpty()){
            for(int j=0;j<listExpr.size();j++){
            	listExpr.getList().get(j).codeGenExpr(compiler,GB);
                compiler.addInstruction(new STORE(Register.getR(GB.getValGB()),new RegisterOffset(-j-1,Register.SP)));
            }
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0,Register.SP),register));
        compiler.addInstruction(new CMP(new NullOperand(),register));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));     
        compiler.addInstruction(new LOAD(new RegisterOffset(0,register),register));
        compiler.addInstruction(new BSR(new RegisterOffset(ident.getMethodDefinition().getIndex(),register)));
        compiler.addInstruction(new LOAD(Register.R0,register));
        compiler.addInstruction(new SUBSP(1+listExpr.size()));
        GB.setGBmanager(table);
        }
	
	@Override
    protected void codeGenInst(DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
        boolean[] table=GB.getCopyTabRegister(); //Enregistrer l'etat init
        compiler.addInstruction(new TSTO(3+listExpr.size()));
        compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
        compiler.addInstruction(new ADDSP(1+listExpr.size()));
        GPRegister register;
        if(GB.remplir()){//si tous les registrer sont utilisé
        	register = Register.getR(GB.getLength()-1);
        	if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.expr.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
			
        } else {
        	if(this.expr!=null) {
        		this.expr.codeGenExpr(compiler, GB);
        		register = Register.getR(GB.getValGB());
        	}else {
        		register = Register.getR(GB.getValGB());
        		compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),register));   		
        	}	
        	
        }
        DAddr addr = new RegisterOffset(0,Register.SP);
        compiler.addInstruction(new STORE(register, addr));
        if(!listExpr.isEmpty()){
            for(int j=0;j<listExpr.size();j++){
            	listExpr.getList().get(j).codeGenExpr(compiler,GB);
                compiler.addInstruction(new STORE(Register.getR(GB.getValGB()),new RegisterOffset(-j-1,Register.SP)));
            }
        }
        compiler.addInstruction(new LOAD(addr,register));
        compiler.addInstruction(new CMP(new NullOperand(),register));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
        compiler.addInstruction(new LOAD(new RegisterOffset(0,register),register));
        compiler.addInstruction(new BSR(new RegisterOffset(ident.getMethodDefinition().getIndex(),register)));
        compiler.addInstruction(new SUBSP(1+listExpr.size()));
        GB.setGBmanager(table);

    }
	@Override
    protected void codeGenPrint(DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
        boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
        GPRegister register;
        if(GB.remplir()){
        	register = Register.getR(GB.getLength()-1);
        	if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
			
        } else {                   	
        	this.codeGenExpr(compiler, GB);
        	register = Register.getR(GB.getValGB());
        }
        
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));
        if(this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        }
        else{
            compiler.addInstruction(new WINT());
        }

        GB.setGBmanager(table);;


    }
    @Override
    protected void codeGenPrintX(DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
        boolean[] table= GB.getCopyTabRegister(); //on verifie les registre
        GPRegister register;
        if(GB.remplir()){
        	register = Register.getR(GB.getLength()-1);
        	if(!compiler.getCompilerOptions().isNoCheck()){
				 compiler.addInstruction(new TSTO(1));
				 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
			}
			compiler.addInstruction(new PUSH(register));
			this.codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
			
        } else {                   	
        	this.codeGenExpr(compiler, GB);
        	register = Register.getR(GB.getValGB());
        }
        compiler.addInstruction(new LOAD(register, Register.R1));
        compiler.addInstruction(new WFLOATX());

        GB.setGBmanager(table);;

    }

	@Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
		if (expr != null) {
			expr.prettyPrint(s, prefix, false);
		}
		ident.prettyPrint(s, prefix, false);
		listExpr.prettyPrint(s, prefix, false);
		
	}

	@Override
	protected void iterChildren(TreeFunction f) {
		if (expr != null) {
			expr.iter(f);
		}
		ident.iter(f);
		listExpr.iter(f);
		
	}


}
