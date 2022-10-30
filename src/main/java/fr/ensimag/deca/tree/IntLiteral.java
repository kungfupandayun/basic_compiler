package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;


import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl07
 * @date 01/01/2020
 */
public class IntLiteral extends AbstractExpr {
	
	/**
	 * @return private int value
	 */
    public int getValue() {
        return value;
    }

    
    private int value;

    /**
     * Store the INT value in private attribute value
     * @param value
     */
    public IntLiteral(int value) {
        this.value = value;
    }

    /**
     * Renvoie le type int
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	this.setType(compiler.getEnvironmentType().get(compiler.getEnvironmentType().getSymbolTab().create("int")).getType());
    	return this.getType();

    }

  
   
    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        compiler.addInstruction(new LOAD(this.getDval(),Register.R1)); // pour sortir une valeur: on est obligé de passer par R1
        compiler.addInstruction(new WINT());
    }

    //creer par nous
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
    	boolean[] table= compiler.getGBmanager().getCopyTabRegister();
        compiler.addInstruction(new LOAD(this.getDval(),Register.getR(GB.getValGB())));
        GB.setGBmanager(table);
    }

    @Override
    public DVal getDval(){
      return new ImmediateInteger(this.value);
    }

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	  s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }


}
