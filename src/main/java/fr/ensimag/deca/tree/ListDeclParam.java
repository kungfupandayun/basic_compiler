package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Iterator;
import org.apache.log4j.Logger;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations of parameters
 *
 * @author gl07
 * @date 17/01/2020
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {
	 
	private static final Logger LOG = Logger.getLogger(ListDeclParam.class);
    @Override
    public void decompile(IndentPrintStream s) {
    	boolean ifVide = true;
    	  for (AbstractDeclParam i : getList()) {
    		  if (!ifVide) {
                	s.print(",");
                }
              i.decompile(s);
              ifVide =false;
          }
    }
    
    /**
     * compose a signature with the parameter 
     * @param compiler contains the "env_types" attribute
     * @param sig signature
     * @throws ContextualError 
     */
    public void formerSignature(DecacCompiler compiler, Signature sig) throws ContextualError {
    	for (AbstractDeclParam i : getList()) {
            try {
				i.addInSignature(compiler, sig);
			} catch (ContextualError e) {
				throw new ContextualError(e.getMessage(), i.getLocation());
				
			}
        }
    	
    }
    //creer par nous meme
    protected void codeGenListParam(DecacCompiler compiler){
            int i= -3;
            for(AbstractDeclParam param : getList()){
            	param.codeGenDeclParam(i,compiler);
                i--;
            }
    }

    /**
     * Implements non-terminal "list_decl_Param" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv
     * @throws ContextualError 
     */
    void verifyListDeclParam(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
    	LOG.debug("verify listDeclParam: start");
        Iterator<AbstractDeclParam> it = this.iterator();
        while(it.hasNext()){
         AbstractDeclParam declParam = it.next();
          declParam.verifyDeclParam(compiler,localEnv);
        }
        LOG.debug("verify listDeclParam: end");
    }
        

    public int valSP() {
        return this.size();
    }

}
