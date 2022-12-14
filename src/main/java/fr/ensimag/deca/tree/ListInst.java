package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;


import java.util.Iterator;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.log4j.Logger;

/**
 * A list of instruction inherited from the TreeList
 * @author gl07
 * @date 01/01/2020
 */
public class ListInst extends TreeList<AbstractInst> {
	private static final Logger LOG = Logger.getLogger(ListInst.class);

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

    	LOG.debug("verify ListInst: start");

        Iterator<AbstractInst> it = this.iterator();

        while(it.hasNext()){
          // cette partie sera changer et plus enrichi pour la passe 3
          AbstractInst  inst = it.next();
          if (inst instanceof Return){
        	  if(it.hasNext()) {
        		  throw new ContextualError("[UnrechableCode] : Return must be the last instruction", inst.getLocation());
        	  }
          }
          
          inst.verifyInst(compiler, localEnv,currentClass,returnType);
          

        }
        
        LOG.debug("verify ListInst: end");
    }

    /**
     * For each instruction in the list, generate the code using compiler
     * @param compiler
     */
    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }
    
}
