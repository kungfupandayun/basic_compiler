package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import java.util.Iterator;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 *
 * @author gl07
 * @date 17/01/2020
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {
    private static final Logger LOG = Logger.getLogger(ListDeclMethod.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * check every method in the list passe 2
     * @param compiler
     * @param parentDef
     * @param def
     * @param env
     * @throws ContextualError When a contextual error 
     **/
    public void verifyListMethod(DecacCompiler compiler, ClassDefinition parentDef, ClassDefinition def, EnvironmentExp env) throws ContextualError {
        LOG.debug("verify listMethod: start");
        Iterator<AbstractDeclMethod> it = this.iterator();
        int i= 0;
        while(it.hasNext()){
          AbstractDeclMethod declMethod = it.next();
          declMethod.verifyMethod(compiler,parentDef,env,i);
          def.incNumberOfMethods();
          i++;
        }
        LOG.debug("verify listMethod: end");
    }

    /**
     * check every method in the list pass 3
     * @param compiler
     * @param env
     * @param currentClass
     * @throws ContextualError When a contextual error 
     **/
    public void verifyListMethodBody(DecacCompiler compiler, EnvironmentExp env , ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify listMethod: start");
        Iterator<AbstractDeclMethod> it = this.iterator();
        while(it.hasNext()){
          AbstractDeclMethod declMethod = it.next();
          // env est l'environment des champ est methode courant
          declMethod.verifyMethodBody(compiler,env,currentClass);
        }
        LOG.debug("verify listMethod: end");
    }
}
