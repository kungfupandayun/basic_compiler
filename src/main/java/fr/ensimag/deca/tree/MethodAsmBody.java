package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.InlinePortion;
import fr.ensimag.ima.pseudocode.Label;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl07
 * @date 01/01/2020
 */
public class MethodAsmBody extends AbstractMethodBody {
    private static final Logger LOG = Logger.getLogger(MethodAsmBody.class);

    private StringLiteral declString;
    
    public MethodAsmBody(StringLiteral declString) {
        Validate.notNull(declString);
        this.declString = declString;
    }

    public StringLiteral getDeclString(){
      return this.declString;
    }

	@Override
	protected void verifyMethodBody(DecacCompiler compiler,  EnvironmentExp envParam,
			ClassDefinition currentClass, Type returnType) throws ContextualError {
		if(declString instanceof StringLiteral) {
			declString.verifyExpr(compiler, envParam, currentClass);
		} else {
			throw new ContextualError("[AsmMethodError] : the ASM method must only contains strings", this.getLocation());
		}
		
		
	}
	
    @Override
    protected void codeGenMethod(DecacCompiler compiler) {
        compiler.add(new InlinePortion(this.declString.getValue()));
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	s.print("asm( ");
        declString.decompile(s);    
        s.print(")");

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declString.iter(f);
        
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	declString.prettyPrint(s, prefix, false);
    }
    
    public int valSP(){
    	// MODIFIER§§§§§§
        return 0;
    }


}
