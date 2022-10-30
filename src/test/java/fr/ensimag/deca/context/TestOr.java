package fr.ensimag.deca.context;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.TestAnd.AndMock;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.And;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Or;

public class TestOr {
	  final Type BOOL = new BooleanType(null);


	    @Mock
	    AbstractExpr boolexpr1;
	    @Mock
	    AbstractExpr boolexpr2;
	

	    DecacCompiler compiler;
	    EnvironmentType envType = new EnvironmentType();

	    class OrMock extends Or{
	    	public OrMock(AbstractExpr leftOperand, AbstractExpr rightOperand) {
				super(leftOperand, rightOperand);
			}

			@Override
	    	public String getOperatorName() {
	    		return super.getOperatorName();
	    	}
	    }
	    @Mock
	    OrMock or =mock(OrMock.class);

	    @Before
	    public void setup() throws ContextualError {
	        MockitoAnnotations.initMocks(this);
	        compiler = new DecacCompiler(null, null);
	        compiler.setEnvironmentType(envType);
	        when(boolexpr1.verifyExpr(compiler, null, null)).thenReturn(BOOL);
	        when(boolexpr2.verifyExpr(compiler, null, null)).thenReturn(BOOL);

	    }

	    @Test
	    public void testBoolBool() throws ContextualError {
	        Or t = new Or(boolexpr1, boolexpr2);
	        // check the result
	        assertTrue(t.verifyExpr(compiler, null, null).isBoolean());
	        // check that the mocks have been called properly.
	        verify(boolexpr1).verifyExpr(compiler, null, null);
	        verify(boolexpr2).verifyExpr(compiler, null, null);
	    }


	    @Test
	    public void testOrName() throws ContextualError {
	      or= new OrMock(boolexpr1, boolexpr2);
	       assertEquals("||",or.getOperatorName());

	    }
}
