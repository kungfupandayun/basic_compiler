package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.SNE;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca Identifier
 *
 * @author gl07
 * @date 01/01/2020
 */
public class Identifier extends AbstractIdentifier {
	private static final Logger LOG = Logger.getLogger(Identifier.class);
    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ParamDefinition getParamDefinition() {
        try {
            return (ParamDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a param identifier, you can't call getParamDefinition on it");
        }
    }
    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	
    	Definition idenDef = null;
    	if (localEnv.getParentsDef(this.getName()) != null) {
    		idenDef = localEnv.getParentsDef(this.getName());		
    	}
    	
    	if (compiler.getEnvironmentType().get(this.getName())!=null) {
    		idenDef = compiler.getEnvironmentType().get(this.getName());
    	}
		if (idenDef == null){
			throw new ContextualError( "[SymbolError]  :  " + this.getName() + "  is not defined " ,this.getLocation());
		}
    	
    	this.setDefinition(idenDef);
    	this.setType(idenDef.getType());
    	return idenDef.getType();

    }
    

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
    	Symbol sym = this.getName();
    	if (compiler.getEnvironmentType().get(sym) == null) {
    		throw new ContextualError("[TypeError] :  The type  " + sym.getName() + " is not predefined",this.getLocation());
    	} else {
    		if (this.getDefinition() == null) {
    			TypeDefinition defT = compiler.getEnvironmentType().get(sym);
        		this.setDefinition(defT);
        		this.setType(defT.getType());
    		}
    		return this.getType();
    	}
    }
	
	
	protected Definition verifyFieldIdent(DecacCompiler compiler, EnvironmentExp env)
			throws ContextualError {
	     	 LOG.debug("FieldCall : start");
		     Symbol name = this.getName();
		     Definition defF = env.getParentsDef(this.getName());
		     if (defF == null) {
		    	 throw new ContextualError("[FieldCallError] : The " + name +  " is not declared neither in the current class nor in its super Classes",this.getLocation());
		     }
		     if (!(defF.isField())) {
		    	 throw new ContextualError("[FieldCallError] : "+ name +" is not a field ", this.getLocation());
		     }
		     // defM est une methodeDef
		     this.setDefinition(defF);
		     LOG.debug("FieldCall : End");
		     return defF;
		
		
	}
	
	protected Definition verifyMethodIdent(DecacCompiler compiler, EnvironmentExp env)
			throws ContextualError {
		     Symbol name = this.getName();
		     EnvironmentExp courant = env;
		     Definition defM = courant.getParentsDef(this.getName());
		     // vérifier maintenat qu'il s'agit bien d'une methode
		     if (defM == null) {
		    	 throw new ContextualError("[MethodCallError]  :" + name + " is not declared neither in the current class nor in its super Classes",this.getLocation());
		     }
		     if (!(defM instanceof MethodDefinition)) {
		    	 throw new ContextualError("[MethodCallError]  : " + name +" is not a method", this.getLocation());
		     }
		     // defM est une methodeDef
		     this.setDefinition(defM);
		     return defM;
		
		
	}
	
    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
    	if(getDefinition().isField()){
			GPRegister register = Register.getR(compiler.getGBmanager().getValGB());
            compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register)); // on charge l'objet
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex()+1,register),Register.R1)); //on charge l'élément
        }
        else if(getDefinition().isClass()){
        	compiler.addComment("class");
            compiler.addInstruction(new LOAD(this.getClassDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isMethod()){
        	compiler.addComment("method");
        	compiler.addInstruction(new LOAD(this.getMethodDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isParam()){
        	compiler.addComment("param");
        	compiler.addInstruction(new LOAD(this.getParamDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isExpression()){
        	compiler.addComment("exp");
        	if(compiler.getAddr(this.getExpDefinition().getOperand())) { 			
        		compiler.addInstruction(new LOAD(this.getExpDefinition().getOperand(),Register.R1));
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }
        else{
        	if(compiler.getAddr(this.getVariableDefinition().getOperand())) { 			
    			compiler.addInstruction(new LOAD(this.getVariableDefinition().getOperand(),Register.R1)); //on charge l'élément
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }
        if(definition.getType().isInt()) {
            compiler.addInstruction(new WINT());
        }else {
            compiler.addInstruction(new WFLOAT());
        }
    }
    @Override
    protected void codeGenPrintX(DecacCompiler compiler){
    	if(getDefinition().isField()){
			GPRegister register = Register.getR(compiler.getGBmanager().getValGB());
            compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register)); // on charge l'objet
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex()+1,register),Register.R1)); //on charge l'élément
        }
        else if(getDefinition().isClass()){
        	compiler.addComment("class");
            compiler.addInstruction(new LOAD(this.getClassDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isMethod()){
        	compiler.addInstruction(new LOAD(this.getMethodDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isParam()){
        	compiler.addComment("param");
        	compiler.addInstruction(new LOAD(this.getParamDefinition().getOperand(),Register.R1));
        }
        else if(getDefinition().isExpression()){
        	compiler.addComment("exp");
        	if(compiler.getAddr(this.getExpDefinition().getOperand())) { 			
    			compiler.addInstruction(new LOAD(this.getExpDefinition().getOperand(),Register.R1)); //on charge l'élément
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }
        else{
        	if(compiler.getAddr(this.getVariableDefinition().getOperand())) { 			
    			compiler.addInstruction(new LOAD(this.getVariableDefinition().getOperand(),Register.R1)); //on charge l'élément
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }
        if(definition.getType().isFloat()) {
            compiler.addInstruction(new WFLOATX());
        }
    }

     @Override
    public void codeGenInitVar(DecacCompiler compiler, GBmanager GB) {
    	RegisterOffset addr = new RegisterOffset(GB.getGB(), Register.GB);
    	this.getVariableDefinition().setOperand(addr);
    	GB.addGB();
    }
     
     @Override
     public void codeGenInitMethod(DecacCompiler compiler) {
     	RegisterOffset addr = new RegisterOffset(compiler.getLB(), Register.LB);
     	this.getVariableDefinition().setOperand(addr);
     	compiler.addLB();
     }
     
 	@Override
 	public void codeGenInitClass(DecacCompiler compiler) {
         RegisterOffset addr = new RegisterOffset(compiler.getGBmanager().getGB(), Register.GB);
		 this.getClassDefinition().setOperand(addr);
         compiler.getGBmanager().addGB();	
 	}

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();

		if(getDefinition().isField()){
			GPRegister register = Register.getR(GB.getValGB());
            compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),register)); // on charge l'objet
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex()+1,register),register)); //on charge l'élément
        }
        else if(getDefinition().isClass()){
        	compiler.addComment("class");
            compiler.addInstruction(new LOAD(this.getClassDefinition().getOperand(),Register.getR(GB.getValGB())));
        }
        else if(getDefinition().isMethod()){
        	compiler.addInstruction(new LOAD(this.getMethodDefinition().getOperand(),Register.getR(GB.getValGB())));
        }
        else if(getDefinition().isParam()){
        	compiler.addComment("param");
        	compiler.addInstruction(new LOAD(this.getParamDefinition().getOperand(),Register.getR(GB.getValGB())));
        }
        else if(getDefinition().isExpression()){
        	compiler.addComment("exp");
        	if(compiler.getAddr(this.getExpDefinition().getOperand())) { 			
    			compiler.addInstruction(new LOAD(this.getExpDefinition().getOperand(),Register.getR(GB.getValGB()))); //on charge l'élément
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }
        else{
        	compiler.addComment("var");
        	if(compiler.getAddr(this.getVariableDefinition().getOperand())) { 			
    			compiler.addInstruction(new LOAD(this.getVariableDefinition().getOperand(),Register.getR(GB.getValGB()))); //on charge l'élément
    		}
    		else {
    			compiler.addInstruction(new BRA(new Label("varaible_non_defini")));
    		}
        }	
        GB.setGBmanager(table);
	}

	@Override
	public DVal getDval() {
		if(this.getDefinition().isField()){
            return new RegisterOffset(getFieldDefinition().getIndex()+1,Register.R0);
        }
        else if(getDefinition().isClass()){
        	return this.getClassDefinition().getOperand();
        }
        else if(getDefinition().isMethod()){
        	return this.getMethodDefinition().getOperand();
        }
        else if(getDefinition().isParam()){
        	return this.getParamDefinition().getOperand();
        }
        else if(getDefinition().isExpression()){
        	return this.getExpDefinition().getOperand();
        }
        else{			
        	return this.getVariableDefinition().getOperand();
        }
	 }






}
