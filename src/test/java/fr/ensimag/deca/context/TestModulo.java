package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Modulo;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test for the Modulo node using mockito, using @Mock and @Before annotations.
 *
 * @author Ensimag
 * @date 01/01/2020
 */
@RunWith(MockitoJUnitRunner.class)
public class TestModulo{

    final Type INT = new IntType(null);
    final Type FLOAT = new FloatType(null);

    @Mock
    AbstractExpr intexpr1;
    @Mock
    AbstractExpr intexpr2;


    DecacCompiler compiler;
    EnvironmentType envType = new EnvironmentType();

    class ModuloMock extends Modulo{
    	public ModuloMock(AbstractExpr leftOperand, AbstractExpr rightOperand) {
			super(leftOperand, rightOperand);
		}

		@Override
    	public String getOperatorName() {
    		return super.getOperatorName();
    	}
    }
    @Mock
    ModuloMock modulo =mock(ModuloMock.class);

    @Before
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(null, null);
        compiler.setEnvironmentType(envType);
        when(intexpr1.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(intexpr2.verifyExpr(compiler, null, null)).thenReturn(INT);
    }

    @Test
    public void testIntInt() throws ContextualError {
        Modulo t = new Modulo(intexpr1, intexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isInt());
        // check that the mocks have been called properly.
        verify(intexpr1).verifyExpr(compiler, null, null);
        verify(intexpr2).verifyExpr(compiler, null, null);
    }


    @Test
    public void testModuloName() throws ContextualError {
      modulo  = new ModuloMock(intexpr1, intexpr2);
       assertEquals("%",modulo.getOperatorName());

    }
}
