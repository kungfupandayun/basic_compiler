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

import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractLValue;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Assign;

public class TestAssign {
    final Type INT = new IntType(null);
    final Type FLOAT = new FloatType(null);

    @Mock
    AbstractLValue intexpr1;
    @Mock
    AbstractLValue floatexpr1;

   
    DecacCompiler compiler;
    EnvironmentType envType = new EnvironmentType();

    class AssignMock extends Assign{
    	
    	public AssignMock(AbstractLValue leftOperand, AbstractExpr rightOperand) {
			super(leftOperand, rightOperand);
		}

		@Override
    	public String getOperatorName() {
    		return super.getOperatorName();
    	}
    }
    @Mock
    AssignMock assign =mock(AssignMock.class);

    @Before
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(null, null);
        compiler.setEnvironmentType(envType);
        when(intexpr1.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(floatexpr1.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
    }

//
//    @Test
//    public void testFloatInt() throws ContextualError {
//        Assign hello = new Assign(floatexpr1, intexpr1);
//        // check the result
//        assertTrue(hello.verifyExpr(compiler, null, null)==AbstractLValue);
//        // ConvFloat should have been inserted on the right side
//       assertTrue(hello.getRightOperand() instanceof ConvFloat);
//        // check that the mocks have been called properly.
//        verify(floatexpr1).verifyExpr(compiler, null, null);
//        verify(intexpr1,times(2)).verifyExpr(compiler, null, null);
//        
//    }
    
 
    @Test
    public void testAssignName() throws ContextualError {
      assign  = new AssignMock(floatexpr1, intexpr1);
       assertEquals("=",assign.getOperatorName());

    }
}
