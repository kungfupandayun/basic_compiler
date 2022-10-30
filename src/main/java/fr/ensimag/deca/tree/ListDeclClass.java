package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import java.util.Iterator;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 * List of class Declarations
 * @author gl07
 * @date 01/01/2020
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    public void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        //throw new UnsupportedOperationException("not yet implemented");
        Iterator<AbstractDeclClass> it = this.iterator();
        while(it.hasNext()){
          AbstractDeclClass declClass = it.next();
          declClass.verifyClass(compiler);
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
    	LOG.debug("verify listClassMembers: start");
        Iterator<AbstractDeclClass> it = this.iterator();
        while(it.hasNext()){
          AbstractDeclClass declClass = it.next();
          declClass.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClassMembers: end");
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
    	LOG.debug("verify listClassBody: start");
        Iterator<AbstractDeclClass> it = this.iterator();
        while(it.hasNext()){
          AbstractDeclClass declClass = it.next();
          declClass.verifyClassBody(compiler);
        }
        LOG.debug("verify listClassBody: end");    }


}
