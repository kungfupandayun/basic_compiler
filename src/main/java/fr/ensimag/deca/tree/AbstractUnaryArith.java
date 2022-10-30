package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractUnaryArith extends AbstractUnaryExpr {

    public AbstractExpr getOperand() {
        return super.getOperand();
    }
    
    private AbstractExpr operand;
    
    public AbstractUnaryArith(AbstractExpr operand) {
        super(operand);
    }
  
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 =  this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (type1.isInt() || type1.isFloat()) {
        	this.setType(type1);
        	return type1;
        } else {
        	throw new ContextualError("[UnaryOperationError] : "+ type1.getName().getName() + " is not an integer or float", this.getLocation());
        }
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName()+"(");
        this.getOperand().decompile(s);
        s.print(")");
        
    }
    
    
    


}
