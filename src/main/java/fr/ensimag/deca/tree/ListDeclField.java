package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import java.util.Iterator;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import org.apache.log4j.Logger;

/**
 *
 * @author gl07
 * @date 17/01/2020
 */
public class ListDeclField extends TreeList<AbstractDeclField> {
    private static final Logger LOG = Logger.getLogger(ListDeclField.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * check every field in the list
     * @param compiler
     * @param env
     * @param name
     * @throws ContextualError When a contextual error 
     **/    
    public void verifyListField(DecacCompiler compiler,EnvironmentExp env, Symbol name) throws ContextualError {
        LOG.debug("verify listField: start");
        Iterator<AbstractDeclField> it = this.iterator();
        int i = 0;
        while(it.hasNext()){
          AbstractDeclField declField = it.next();
          declField.verifyField(compiler,env,name,i);
          i++;
        }
        LOG.debug("verify listField: end");
    }
    
    /**
     * check every variable of the field in the list
     * @param compiler
     * @param env
     * @throws ContextualError When a contextual error 
     **/    
    public void verifyListFieldVal(DecacCompiler compiler,EnvironmentExp env, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verify listField: start");
        //throw new UnsupportedOperationException("not yet implemented");
        Iterator<AbstractDeclField> it = this.iterator();
        while(it.hasNext()){
          AbstractDeclField declField = it.next();
          declField.verifyFieldInit(compiler,env, currentClass);
        }
        LOG.debug("verify listField: end");
    }
    
    protected void codeGenListField(DecacCompiler compiler){

        boolean[] table=compiler.getGBmanager().getCopyTabRegister(); //on verifie les registre

        for(AbstractDeclField a : getList()){
            a.codeGenField(compiler);
        }
        compiler.getGBmanager().setGBmanager(table);
      }


}
