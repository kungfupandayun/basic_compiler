package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * A variable declaration is represented by
 * Type Name=Initialisation
 * @author gl07
 * @date 01/01/2020
 */
public class DeclVar extends AbstractDeclVar {
	private static final Logger LOG = Logger.getLogger(DeclVar.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;
    /**
     * Take in 3 parameters to represent a variable declaration
     * @param type
     * @param varName
     * @param initialization
     */
    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }


    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
    	LOG.debug("VerifyDeclVar: start");
    	this.type.verifyType(compiler);

    	if (this.type.getType() instanceof VoidType) {
    		throw new ContextualError("[DeclVarError]  : Variable type must be different from void", this.type.getLocation());
    	}
    	// We create the variables in a class
    	if (currentClass != null) {
    		EnvironmentExp envExpSup = currentClass.getMembers();
    		localEnv.setParentEnvironment(envExpSup);
        	this.initialization.verifyInitialization(compiler, this.type.getType(), localEnv,currentClass);
    	} else { 
    		this.initialization.verifyInitialization(compiler, this.type.getType(), localEnv,currentClass);
    	}

    	VariableDefinition varDef = new VariableDefinition(this.type.getType(), this.getLocation());
    	// à l'initialisation de la variable on passe on parametre un environment Exp qui est l'empilement de deux environment
    	
    	try {
    		localEnv.declare(varName.getName(), varDef);
    		this.varName.setDefinition(varDef);
    		this.varName.setType(this.type.getType());
    	} catch(Exception e){
    		throw new ContextualError("[DeclVarError]  : Double definition of " + varName.getName(), this.getLocation());
    	}
    	
    	LOG.debug("VerifyDeclVar: End");

    }

    @Override
    public void codeGenDeclVar(DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
        this.varName.codeGenInitVar(compiler, GB);
        if(this.initialization.getExpression()!=null) {
            this.initialization.codeGenDeclVar(compiler, GB);
            compiler.addInstruction(new STORE(Register.getR(GB.getValGB()), this.varName.getVariableDefinition().getOperand()));
            compiler.setAddr(this.varName.getVariableDefinition().getOperand());
			}
		GB.setGBmanager(table);

}
    
    @Override
    public void codeGenDeclVarMethod(DecacCompiler compiler){
    	GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
				this.varName.codeGenInitMethod(compiler);	
			if(this.initialization.getExpression()!=null) {
				this.initialization.codeGenDeclVar(compiler, compiler.getGBmanager());
				compiler.addInstruction(new STORE(Register.getR(GB.getValGB()), this.varName.getVariableDefinition().getOperand()));
				compiler.setAddr(this.varName.getVariableDefinition().getOperand());
			}		
		GB.setGBmanager(table);

}

    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.varName.decompile(s);
        this.initialization.decompile(s);
        s.print(";");
        s.println();
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public int valSP(){
        if (this.type.getType().isClass()){
            ClassDefinition currentClass = this.type.getClassDefinition();
            int compteur = currentClass.getNumberOfFields() + currentClass.getNumberOfMethods() -1;
            return compteur;
        }
        return 1;
    }

}
