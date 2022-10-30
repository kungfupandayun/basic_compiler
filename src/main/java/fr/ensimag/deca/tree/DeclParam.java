package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.codegen.GBmanager;

/**
 * 
 * @author gl07
 * @date 17/01/2020
 */
public class DeclParam extends AbstractDeclParam {
	private static final Logger LOG = Logger.getLogger(DeclParam.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier paramName;
    

    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName) {
        Validate.notNull(type);
        Validate.notNull(paramName);
        this.type = type;
        this.paramName = paramName;
        
    }

    protected void addInSignature(DecacCompiler compiler, Signature sig) throws ContextualError {
    	if (compiler.getEnvironmentType().get(this.type.getName()) == null) {
    		throw new ContextualError("[DeclParamError] : Parameter Type is not predefined ", this.type.getLocation());
    	}
    	Type typeParam = compiler.getEnvironmentType().get(this.type.getName()).getType();
    	if (typeParam instanceof VoidType) {
    		throw new ContextualError("[DeclParamError] : Parameter type must be different from void", this.type.getLocation());
    	}
    	sig.add(typeParam);
    	
    }
    
    
    @Override
    protected void verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
    	LOG.debug("VerifyDeclParam: start");
    	Type typeParam = compiler.getEnvironmentType().get(this.type.getName()).getType();
    	ParamDefinition defP = new ParamDefinition(typeParam, this.getLocation());
    	this.type.setDefinition(compiler.getEnvironmentType().get(this.type.getName()));
    	this.paramName.setDefinition(defP);
    	this.type.setDefinition(defP);
    	try {
			localEnv.declare(this.paramName.getName(), defP);
		} catch (DoubleDefException e) {
			// double definition
			throw new ContextualError("[DeclParamError] Double definition of " + this.paramName.getName(), this.getLocation());
		}
    	
    	LOG.debug("VerifyDeclParam: End");

    }
    private int indice;
    public int getIndice() {
    	return this.indice;
    }
    //creer par nou meme
    @Override
    public void codeGenDeclParam(int indice, DecacCompiler compiler){
		GBmanager GB = compiler.getGBmanager();
		boolean[] table= compiler.getGBmanager().getCopyTabRegister();
		paramName.getParamDefinition().setOperand(new RegisterOffset(indice, Register.LB));
		this.indice = indice;
		GB.setGBmanager(table);

}

    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.paramName.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        paramName.iter(f);
       
       
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, false);
    
    }
}
