package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ListExpr extends TreeList<AbstractExpr> {
		private static final Logger LOG = Logger.getLogger(ListExpr.class);

	public void verifyListRValueStar(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass,
			Signature signature) throws ContextualError {
		LOG.debug("Verify ListExpr : start");
		int i=0;
		for (AbstractExpr c : getList()) {
			
			Type typeAttendu = signature.paramNumber(i);
            AbstractExpr typeExpr = c.verifyRValue(compiler, localEnv, currentClass, typeAttendu);
			if (typeAttendu.isFloat()) {
				if (typeExpr.getType().isInt()) {
					ConvFloat conv = new ConvFloat(typeExpr);
					this.set(i, conv);
					conv.verifyExpr(compiler, localEnv, currentClass);
					}
			}
			
            i++;
            c.setType(typeAttendu);
            
        }
		LOG.debug("Verify ListExpr : End");
		
	}
	

    @Override
    public void decompile(IndentPrintStream s) {
    	boolean ifVide = true;
        for (AbstractExpr i : getList()) {
        	  if (!ifVide) {
              	s.print(",");
              }
            i.decompileExp(s);
          
            ifVide =false;
        }
    }
}
