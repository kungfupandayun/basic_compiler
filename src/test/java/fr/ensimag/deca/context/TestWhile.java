package fr.ensimag.deca.context;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;

public class TestWhile {

    final Type BOOL = new BooleanType(null);

   
    @Mock
    AbstractExpr boolexpr1;

    DecacCompiler compiler;
    EnvironmentType envType = new EnvironmentType();

    @Test
    public void testType() throws ContextualError {
        DecacCompiler compiler = new DecacCompiler(null, null);
        AbstractExpr left = Mockito.mock(AbstractExpr.class);
        when(left.verifyExpr(compiler, null, null)).thenReturn(BOOL);
     

    }

}
