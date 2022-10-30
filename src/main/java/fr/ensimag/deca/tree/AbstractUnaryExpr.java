package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Abstract class of Unary expression.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

	/** 
	 * @return the operand of the operation
	 * 
	 */
    public AbstractExpr getOperand() {
        return operand;
    }
    
    
    private AbstractExpr operand;
    
    /**
     * @param operand
     * Store the operand of the operation
     */
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }


    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("("+ getOperatorName());
        getOperand().decompile(s);
        s.print(")");
        
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

}
