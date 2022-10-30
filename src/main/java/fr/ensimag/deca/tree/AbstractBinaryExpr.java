package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GBmanager;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.Operator;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Binary expressions.
 *
 * @author gl07
 * @date 01/01/2020
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {
	private static final Logger LOG = Logger.getLogger(AbstractBinaryExpr.class);
	
	/** 
	 * @return the expression at the left
	 */
    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    /**
     * @return  the expression at the right
     * */
    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    /**
     * Set the expression at the left
     * @param leftOperand
     */
    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    /**
     * Set the expression at the right
     * @param rightOperand
     */
    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    /**
     * Store the left and right expressions
     * @param leftOperand
     * @param rightOperand
     */
    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    /**
     * Get the Name/symbol of the operator in.
     */
    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }



    /**Perform operation while the types belong to the same domain
     * An INT expression could not perform operation with a BOOLEAN type
     */
    public boolean domArithOp(Type type1, Type type2) {
    	boolean b1 = type1.isInt() || type1.isFloat();
		boolean b2 = type2.isInt() || type2.isFloat();
		return (b1 && b2);
    }
    
    /**
     * Filter the type of the two operations in a binary operation so that the operation  is valid 
     * @throws ContextualError if operation of two different expressions with non-valid type 
     */
    public  Type typeBinaryOperation(Operator op, Type type1, Type type2) throws ContextualError {
    	if (op.isPlus()||op.isMinus()||op.isMult()||op.isDivide()) {
    		return this.typeArithOp(type1,type2);
    	} else if (op.isMod()&& type1.isInt() && type2.isInt()) {
    		return type1;
    	} else if ((op.isEq()||op.isNeq()||op.isLt()||op.isGt()||op.isLeq()||op.isGeq())&& domArithOp(type1,type2)) {
    		return new BooleanType(new SymbolTable().create("boolean"));
    	} else if ((op.isNeq()|| op.isEq())&&(type1.isClassOrNull())&&(type2.isClassOrNull())) {
    		return new BooleanType(new SymbolTable().create("boolean"));
    	} else if (op.isAnd()|| op.isOr()||op.isEq()||op.isNeq()) {
    		return new BooleanType(new SymbolTable().create("boolean"));
    	}
    	return null;
    }
    
    
    /**
     * An arithmetic operation could be performed between int and float type operation
     * return true if the arithmetic operation could be perform legally
     * @throws ContextualError if operation of two different expressions with non-valid type 
     */
	public Type typeArithOp(Type type1, Type type2) throws ContextualError {
		if ((type1.isFloat() && type2.isFloat()) || (type1.isFloat() && type2.isInt()) || (type2.isFloat() && type1.isInt())) {
			return new FloatType(new SymbolTable().create("float"));
		} else if (type1.isInt() && type2.isInt()) {
			return new IntType(new SymbolTable().create("int"));
		} else {
			throw new ContextualError("[ArithmeticOperationError]  : An arithmetic operation cannot be performed between " + type1.getName().getName() + " and " + type2.getName().getName(), this.getLocation());
		}
	}
}
