package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import java.lang.System;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a field
 *
 * @author gl07
 * @date 17/01/2020
 */
public class DeclField extends AbstractDeclField {


	final private Visibility v;
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private AbstractInitialization initialization;

    public DeclField (Visibility v, AbstractIdentifier type, AbstractIdentifier name, AbstractInitialization initialization ) {
    	Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(initialization);
        this.v = v;
        this.type = type;
        this.name = name;
        this.initialization = initialization;
      
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
    	if(v == Visibility.PROTECTED) {
    		s.print("protected ");
    	}
    	s.print(" ");
        this.type.decompile(s);
        s.print(" ");
        this.name.decompile(s);
        this.initialization.decompile(s);
        s.print(";");
        s.println();
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */

    protected void verifyField(DecacCompiler compiler, EnvironmentExp env, Symbol name, int i) throws ContextualError {
        
    	// vérifier que le type est un type prédéfinie
        Type typeF = this.type.verifyType(compiler);
        if (typeF instanceof VoidType) {
    		throw new ContextualError("[DeclFieldError] : Field type must be different from void", this.type.getLocation());
    	}
        ClassDefinition identDef = (ClassDefinition) compiler.getEnvironmentType().get(name); // The class which contains this field
        // We check if the name is defined in the super classs
        ClassDefinition parentDef = identDef.getSuperClass();
        EnvironmentExp courant = identDef.getMembers();
        ExpDefinition nameDef = courant.getParentsDef(this.name.getName()); // recuperer une definition si il existe dans l'empilement
        // IF there is a field with the same name in the super class
        if (nameDef != null ) {
        	if (!(nameDef instanceof FieldDefinition)) {
        		throw new ContextualError("[DeclFieldError] : " + this.name.getName().toString()+" is not defined as field in the superior class"+ parentDef.getType().getName(), this.name.getLocation());
        	}	
        }
        FieldDefinition fDef = new FieldDefinition(typeF,this.getLocation(),this.v, identDef, i);
        try {
			env.declare(this.name.getName(),fDef);
		} catch (DoubleDefException e) {
			throw new ContextualError("[DeclFieldError]  : Double definition of " + this.name.getName(), this.getLocation());
		}    
        identDef.incNumberOfFields();
        this.name.setDefinition(fDef);
        this.name.setType(typeF);

    }

 
    protected void verifyFieldInit (DecacCompiler compiler,EnvironmentExp env , ClassDefinition currentClass) 
    		throws ContextualError{
    		Symbol typeName = this.type.getName();
    		Type typeF = compiler.getEnvironmentType().get(typeName).getType();
    		// env est l'environment de champ est methode
        	this.initialization.verifyInitialization(compiler, typeF, env ,currentClass);

    }

    @Override
    String prettyPrintNode() {
    	if(v == Visibility.PROTECTED) {
    		return "[visibility=PROTECTED] DeclField";
    	}
    	else {
    		return "[visibility=PUBLIC] DeclField";
    	}
        
    }
	
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        initialization.iter(f);
    }
    public AbstractIdentifier getident() {
    	return this.name;
    }

	@Override
	protected void codeGenField(DecacCompiler compiler) {
		GBmanager GB = compiler.getGBmanager();
		boolean[] table = GB.getCopyTabRegister();
        compiler.addComment("Initialisation d'un attribut");

        GPRegister register;
		if(!compiler.getCompilerOptions().isNoCheck()){
			 compiler.addInstruction(new TSTO(1));
			 compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
		}
		//si le registre attiend register MAX
        if(GB.remplir()) {
			register = Register.getR(GB.getLength()-1);
			compiler.addInstruction(new PUSH(register));
	        if(this.initialization.getExpression()!=null) {
	        	this.initialization.codeGenDeclVar(compiler, GB);
	        	register = Register.getR(GB.getValGB());
	        }else {
	        	register = Register.getR(GB.getValGB());
	        	compiler.addInstruction(new LOAD(0,register));
	        }
	        compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
        }else { //sinon
        	if(this.initialization.getExpression()!=null) {
	        	this.initialization.codeGenDeclVar(compiler, GB);
	        	register = Register.getR(GB.getValGB());
	        }else {
	        	register = Register.getR(GB.getValGB());
	        	compiler.addInstruction(new LOAD(0,register));
	        }
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),Register.R1));
        DAddr addr = new RegisterOffset(this.name.getFieldDefinition().getIndex()+1,Register.R1);
        compiler.addInstruction(new STORE(register,addr));
        this.name.getFieldDefinition().setOperand(addr);
        compiler.setAddr(addr);
        GB.setGBmanager(table);
	}

}
