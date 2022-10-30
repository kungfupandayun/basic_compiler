package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;

import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ConvFloat extends AbstractUnaryExpr {
	  private static final Logger LOG = Logger.getLogger(ConvFloat.class);

	  /**
	   * Represent a float conversion mechanism
	   * @param operand 
	   */
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    /**
     * Verifier la conversion d'un float en un entier
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
       LOG.debug("ConvFolat : start");
       Type type2 = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
       // tester la comptabilité de caster type2 en float
       TypeDefinition defType = compiler.getEnvironmentType().get(compiler.getEnvironmentType().getSymbolTab().create("float"));
       if (type2.castCompatible(compiler.getEnvironmentType(), defType.getType())) {
    	   this.setType(new FloatType(compiler.getEnvironmentType().getSymbolTab().create("float")));
    	   LOG.debug("ConvFolat : End");
    	   return this.getType();
       } else {
    	   throw new ContextualError("Erreur de Conversion " + type2.getName().getName() + " ne peut pas être convertit en float", this.getLocation());
       }
     
    }


    @Override
    protected String getOperatorName() {
        return "";
    }

	@Override
	protected void codeGenExpr(DecacCompiler compiler, GBmanager GB) {
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
        getOperand().codeGenExpr(compiler, GB);
        GPRegister register = Register.getR(GB.getValGB());
	    compiler.addInstruction(new FLOAT(register, register));
	    GB.setGBmanager(table);
	}

	@Override
    protected void codeGenPrint(DecacCompiler compiler){
	  GBmanager GB = compiler.getGBmanager();
        boolean[] table = GB.getCopyTabRegister(); //on verifie les registre
        if(GB.remplir()){
        	GPRegister register = Register.getR(GB.getLength()-1);
			compiler.addInstruction(new PUSH(register));
			this.getOperand().codeGenExpr(compiler, GB);
			compiler.addInstruction(new LOAD(register, Register.R0));
			compiler.addInstruction(new POP(register));
			compiler.addInstruction(new WFLOAT());
        }
        else{
			this.getOperand().codeGenExpr(compiler, GB);
			compiler.addInstruction(new WFLOAT());
        }
        GB.setGBmanager(table);
    }


		@Override
		protected void codeGenPrintX(DecacCompiler compiler){
			GBmanager GB = compiler.getGBmanager();
			boolean[] table = GB.getCopyTabRegister(); //enregistrer l'état init
			if(GB.remplir()){
				GPRegister register = Register.getR(GB.getLength()-1);
				compiler.addInstruction(new PUSH(register));
				this.getOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new LOAD(register, Register.R0));
				compiler.addInstruction(new POP(register));
				compiler.addInstruction(new WFLOATX());
			}
			else{
				this.getOperand().codeGenExpr(compiler, GB);
				compiler.addInstruction(new WFLOATX());
			}
			GB.setGBmanager(table);
	}

	@Override
	public DVal getDval() {
		// TODO Auto-generated method stub
		return null;
	}

}
