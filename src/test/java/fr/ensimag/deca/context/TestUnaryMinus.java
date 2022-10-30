package fr.ensimag.deca.context;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.UnaryMinus;

@RunWith(MockitoJUnitRunner.class)
public class TestUnaryMinus {
	
	    final Type INT = new IntType(null);
	    final Type FLOAT = new FloatType(null);
	   

	    @Mock
	    AbstractExpr intexpr1;
	    @Mock
	    AbstractExpr floatexpr1;


	    DecacCompiler compiler;
	    EnvironmentType envType = new EnvironmentType();
	    EnvironmentExp envExp = new EnvironmentExp(null);

	    class UnaryMinusMock extends UnaryMinus{
	    	public UnaryMinusMock(AbstractExpr operand) {
				super(operand);
			}

			@Override
	    	public String getOperatorName() {
	    		return super.getOperatorName();
	    	}
	    }
	    @Mock
	    UnaryMinusMock unaryminus =mock(UnaryMinusMock.class);

	    @Before
	    public void setup() throws ContextualError {
	        MockitoAnnotations.initMocks(this);
	        compiler = new DecacCompiler(null, null);
	        compiler.setEnvironmentType(envType);
	        when(intexpr1.verifyExpr(compiler, envExp, null)).thenReturn(INT);
	        when(floatexpr1.verifyExpr(compiler, envExp, null)).thenReturn(FLOAT);
	    }

	    @Test
	    public void testInt() throws ContextualError {
	        UnaryMinus t = new UnaryMinus(intexpr1);
	        // check the result
	        assertTrue(t.verifyExpr(compiler, envExp, null).isInt());
	        // check that the mocks have been called properly.
	        verify(intexpr1).verifyExpr(compiler, envExp, null);
	    }

	    @Test
	    public void testFloat() throws ContextualError {
	        UnaryMinus t = new UnaryMinus(floatexpr1);
	        // check the result
	        assertTrue(t.verifyExpr(compiler, envExp, null).isFloat());	 
	        // check that the mocks have been called properly.
	        verify(floatexpr1).verifyExpr(compiler, envExp, null);
	    }

	   
	    @Test
	    public void testUnaryMinusName() throws ContextualError {
	      unaryminus  = new UnaryMinusMock(intexpr1);
	       assertEquals("-",unaryminus.getOperatorName());

	    }

}
