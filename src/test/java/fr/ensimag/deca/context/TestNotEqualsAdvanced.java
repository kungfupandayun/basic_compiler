package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.NotEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.EnvironmentExp;
/**
 * Test for the Equal node using mockito, using @Mock and @Before annotations.
 *
 * @author Ensimag
 * @date 01/01/2020
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNotEqualsAdvanced {

    final Type INT = new IntType(null);
    final Type FLOAT = new FloatType(null);
    final Type BOOL = new BooleanType(null);

    @Mock
    AbstractExpr intexpr1;
    @Mock
    AbstractExpr intexpr2;
    @Mock
    AbstractExpr floatexpr1;
    @Mock
    AbstractExpr floatexpr2;
    @Mock
    AbstractExpr booleanexpr1;
    @Mock
    AbstractExpr booleanexpr2;

    DecacCompiler compiler;
    EnvironmentType envType = new EnvironmentType();
    EnvironmentExp envExp = new EnvironmentExp(null);

    class NotEqualsMock extends NotEquals{
    	public NotEqualsMock(AbstractExpr leftOperand, AbstractExpr rightOperand) {
			super(leftOperand, rightOperand);
		}

		@Override
    	public String getOperatorName() {
    		return super.getOperatorName();
    	}
    }
    @Mock
    NotEqualsMock notequals =mock(NotEqualsMock.class);

    @Before
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(null, null);
        compiler.setEnvironmentType(envType);
        when(intexpr1.verifyExpr(compiler, envExp, null)).thenReturn(INT);
        when(intexpr2.verifyExpr(compiler, envExp, null)).thenReturn(INT);
        when(floatexpr1.verifyExpr(compiler, envExp, null)).thenReturn(FLOAT);
        when(floatexpr2.verifyExpr(compiler, envExp, null)).thenReturn(FLOAT);
        when(booleanexpr1.verifyExpr(compiler, envExp, null)).thenReturn(BOOL);
        when(booleanexpr2.verifyExpr(compiler, envExp, null)).thenReturn(BOOL);
    }

    @Test
    public void testIntInt() throws ContextualError {
        NotEquals t = new NotEquals(intexpr1, intexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, envExp, null).isBoolean());
        // check that the mocks have been called properly.
        verify(intexpr1).verifyExpr(compiler, envExp, null);
        verify(intexpr2).verifyExpr(compiler, envExp, null);
    }

    @Test
    public void testIntFloat() throws ContextualError {
        NotEquals t = new NotEquals(intexpr1, floatexpr1);
        // check the result
        assertTrue(t.verifyExpr(compiler, envExp, null).isBoolean());
        // ConvFloat should have been inserted on the right side
        assertTrue(t.getLeftOperand() instanceof ConvFloat);
        assertFalse(t.getRightOperand() instanceof ConvFloat);
        // check that the mocks have been called properly.
        verify(intexpr1,times(2)).verifyExpr(compiler, envExp, null);
        verify(floatexpr1).verifyExpr(compiler, envExp, null);
    }

    @Test
    public void testFloatInt() throws ContextualError {
    	NotEquals t = new NotEquals(floatexpr1, intexpr1);
        // check the result
        assertTrue(t.verifyExpr(compiler, envExp, null).isBoolean());
        // ConvFloat should have been inserted on the right side
        assertTrue(t.getRightOperand() instanceof ConvFloat);
        assertFalse(t.getLeftOperand() instanceof ConvFloat);
        // check that the mocks have been called properly.
        verify(intexpr1,times(2)).verifyExpr(compiler, envExp, null);
        verify(floatexpr1).verifyExpr(compiler, envExp, null);
    }

    @Test
    public void testBoolBool() throws ContextualError {
        NotEquals t = new NotEquals(booleanexpr1, booleanexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, envExp, null).isBoolean());
        // ConvFloat shouldn't have been inserted on the both sides
        assertFalse(t.getRightOperand() instanceof ConvFloat);
        assertFalse(t.getLeftOperand() instanceof ConvFloat);
        // check that the mocks have been called properly.
        verify(booleanexpr1).verifyExpr(compiler, envExp, null);
        verify(booleanexpr2).verifyExpr(compiler, envExp, null);
    }

    @Test
    public void testNotEqualsName() throws ContextualError {
      notequals  = new NotEqualsMock(floatexpr1, intexpr1);
       assertEquals("!=",notequals.getOperatorName());

    }
}
