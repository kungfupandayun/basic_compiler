package fr.ensimag.deca.tree;

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.DeclVar;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {
	 private static final Logger LOG = Logger.getLogger(ListDeclVar.class);
	 
    @Override
    public void decompile(IndentPrintStream s) {
    	  for (AbstractDeclVar i : getList()) {
              i.decompile(s);
              s.println();
          }
    }
    
    /**
     * Code generation for every variable in the list
     * @param compiler
     */
    protected void codeGenListVar(DecacCompiler compiler){
      for (AbstractDeclVar i : getList()) {
          i.codeGenDeclVar(compiler);
      }
    }
    protected void codeGenListVarMethod(DecacCompiler compiler){
        for (AbstractDeclVar i : getList()) {
            i.codeGenDeclVarMethod(compiler);
        }
      }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to
     *      the "env_exp_r" attribute
     * @param currentClass
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
    	LOG.debug("verify listDeclVar: start");
        Iterator<AbstractDeclVar> it = this.iterator();
        while(it.hasNext()){
         AbstractDeclVar declVar = it.next();
          declVar.verifyDeclVar(compiler,localEnv, currentClass);
        }
        LOG.debug("verify listDeclVar: end");
    }
    


    public int valSP(){
        int i = 0;
        for (AbstractDeclVar a : getList()){
            i = a.valSP();
        }
        return i;
    }


}
