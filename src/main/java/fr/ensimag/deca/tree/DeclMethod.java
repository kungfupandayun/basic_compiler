package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;


import fr.ensimag.deca.tools.DecacInternalError;


import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * Declaration of a method
 *
 * @author gl07
 * @date 17/01/2020
 */
public class DeclMethod extends AbstractDeclMethod {


    final private AbstractIdentifier type;
    final private AbstractIdentifier ident;
    final private ListDeclParam param;
    final private AbstractMethodBody method;

    public DeclMethod (AbstractIdentifier type, AbstractIdentifier ident, ListDeclParam param, AbstractMethodBody method ) {
    	Validate.notNull(type);
        Validate.notNull(ident);

        this.type = type;
        this.ident = ident;
        this.param = param; // can be null
        this.method = method;
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	this.type.decompile(s);
        s.print(" ");
        this.ident.decompile(s);
        s.print(" (");
        if (param!=null) {
        	this.param.decompile(s);
        }
        s.print(")");

        if (method!=null) {
        	method.decompile(s);
        }

    }


      /**
       * Pass 2 of [SyntaxeContextuelle].
       */
    protected void verifyMethod(DecacCompiler compiler,ClassDefinition def, EnvironmentExp env, int i) throws ContextualError {
    	
    	if (compiler.getEnvironmentType().get(type.getName()) == null) {
    		throw new ContextualError("[DeclMethodError] : The return Type of "+ this.ident.getName() +  " is not predefined", this.ident.getLocation());
    	}
    	Type typeM = compiler.getEnvironmentType().get(type.getName()).getType(); // the type of the return value in this method
    	Symbol name = this.ident.getName(); // the name of this method
    	Signature sig = new Signature(); // The signature of the method in the current class
    	// former la signature
    	if (this.param != null) {
    		this.param.formerSignature(compiler, sig);
    	}
    	MethodDefinition mDef = new MethodDefinition(typeM,this.getLocation(),sig,i);

    	ExpDefinition parentDef = env.getParentsDef(this.ident.getName());
    	//If there is a method called by the same name in the super class
     	if (parentDef != null) {
     		if (!parentDef.isMethod()) {
        		throw new ContextualError("[DeclMethodError] :" + this.ident.getName()+" is not a method in the class "+def.getType().getName(), this.ident.getLocation());
        	}
     		MethodDefinition superDef = (MethodDefinition) parentDef;
     		Signature superSig = superDef.getSignature();
        	// check if the signaturs are the same
        	if (!sig.equals(superSig)) {
        		throw new ContextualError("[DeclMethodError] : The signature of the method "+ name.getName().toString()+" is different from that defined in the super class" , this.ident.getLocation());
        	}
     		Type superType = superDef.getType();
     		if (!typeM.subType(compiler.getEnvironmentType(),superType)) {
        		throw new ContextualError("[DeclMethodError] : The return type of  " + name.getName().toString() + " is not a subtype of the higher class method definition("+ superType.getName() + ")", this.ident.getLocation());
     		}
     	}

	    try {
			env.declare(name,mDef);
		} catch (DoubleDefException e) {
			throw new ContextualError("[DeclMethodError] Double definition of " + name , this.getLocation());
		}
	    this.ident.setDefinition(mDef);
	    this.ident.setType(typeM);
	    this.type.setType(typeM);
	    this.type.setDefinition(compiler.getEnvironmentType().get(type.getName()));


    }


    /**
     * Pass 3 of [SyntaxeContextuelle].
     */
    protected void verifyMethodBody(DecacCompiler compiler,EnvironmentExp env , ClassDefinition def) throws ContextualError {

    	// le type de retour de la méthode
    	Type returnType = compiler.getEnvironmentType().get(this.type.getName()).getType();
    	EnvironmentExp envParam = new EnvironmentExp();
    	this.param.verifyListDeclParam(compiler, envParam);
    	// env Param contient les parametre
    	this.method.verifyMethodBody(compiler,envParam, def,returnType);

    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	type.prettyPrint(s, prefix, false);
    	ident.prettyPrint(s, prefix, false);
        if (param!=null) {
        	param.prettyPrint(s, prefix, false);
        }
        if (method!=null) {
        	method.prettyPrint(s, prefix, false);
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	type.iter(f);
    	ident.iter(f);
        param.iter(f);
        method.iter(f);    }

	@Override
	public AbstractIdentifier getIdentifier() {
		return this.ident;
	}

	@Override
    protected void codeGenMethod(DecacCompiler compiler) {
		boolean[] table=compiler.getGBmanager().getCopyTabRegister(); //on verifie les registres
        compiler.add(new Line(this.ident.getMethodDefinition().getLabel()));
        compiler.initLB();
        //enregistrer le nombre de attrubut de class qq part
        int attribut = compiler.getnbrfield();
        compiler.addInstruction(new TSTO(attribut));
        compiler.add(new Line(new BOV(new Label("stack_overflow_error"))));
        //si on a declarer trop de attribut
        if((attribut+2)>16) {
            throw new DecacInternalError("[Register Error]: There is not enough registers.");
        }
        for(int i=2;i<attribut+2;i++){
            compiler.addInstruction(new PUSH(Register.getR(i)));
        }
        Label fin = new Label("fin."+getIdentifier().getMethodDefinition().getLabel().toString());
        compiler.setEnd(fin);
        param.codeGenListParam(compiler);
        method.codeGenMethod(compiler);
        if(!type.getType().isVoid()){
            compiler.addInstruction(new WSTR("[ReturnError] : the method "+ident.getName().toString() +" is expecting a return but has none."));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
        compiler.addLabel(fin);
        for(int i=attribut+1;i>=2;i--){
            compiler.addInstruction(new POP(Register.getR(i)));
        }
        compiler.getGBmanager().setGBmanager(table);; //reset le table init
        compiler.addInstruction(new RTS());

    }
}
