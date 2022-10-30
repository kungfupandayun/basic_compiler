package fr.ensimag.deca.context;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.Not;

public class TestNot {
	 final Type BOOL = new BooleanType(null);


	    @Mock
	    AbstractExpr boolexpr1;


    DecacCompiler compiler;
    EnvironmentType envType = new EnvironmentType();
    EnvironmentExp envExp = new EnvironmentExp(null);

    class NotMock extends Not{
    	public NotMock(AbstractExpr operand) {
			super(operand);
		}

		@Override
    	public String getOperatorName() {
    		return super.getOperatorName();
    	}
    }
    @Mock
    NotMock not =mock(NotMock.class);


    @Before
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(null, null);
        compiler.setEnvironmentType(envType);
        when(boolexpr1.verifyExpr(compiler, null, null)).thenReturn(BOOL);

    }

    @Test
    public void testBoolBool() throws ContextualError {
        Not t = new Not(boolexpr1);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isBoolean());
        // check that the mocks have been called properly.
        verify(boolexpr1).verifyExpr(compiler, null, null);
    }


    @Test
    public void testNotName() throws ContextualError {
      not  = new NotMock(boolexpr1);
       assertEquals("!",not.getOperatorName());

    }
}
