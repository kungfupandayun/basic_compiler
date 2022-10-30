package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class DeclClass extends AbstractDeclClass {

	private static final Logger LOG = Logger.getLogger(DeclClass.class);

    final private AbstractIdentifier ident; // nom de la class
    final private AbstractIdentifier parent;
    final private ListDeclField field;
    final private ListDeclMethod method;
    
    public AbstractIdentifier getident() {
    	return this.ident;
    }

    public DeclClass (AbstractIdentifier ident, AbstractIdentifier parent, ListDeclField field, ListDeclMethod method ) {
    	Validate.notNull(ident);
        Validate.notNull(field);
        Validate.notNull(method);
        this.ident = ident;
        if (parent != null) {
        	this.parent = parent;
        }else {
        	this.parent = new Identifier(new SymbolTable().create("Object"));
        	this.parent.setLocation(Location.BUILTIN);
        }
        this.field = field;
        this.method = method;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        ident.decompile(s);
        if (!parent.getName().getName().equals("Object")) {
        	s.print(" extends ");
        	parent.decompile(s);
        }
        s.println(" { ");
        s.indent();
        field.decompile(s);
        method.decompile(s);
        s.unindent();
        s.println("}");

    }

   /**
   * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
   * without looking at its content.
   */
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("Verify Class: start");
        if (compiler.getEnvironmentType().get(this.ident.getName())!=null){
        	throw new ContextualError("[ClassDeclarationError] : This class " + this.ident.getName()+ " is already defined", this.ident.getLocation());
        } else {

        	Symbol symParent;
        	// We get the name of superclass
    		symParent = this.parent.getName();
        	if (compiler.getEnvironmentType().get(symParent)==null|| !(compiler.getEnvironmentType().get(symParent) instanceof ClassDefinition )) {
            	throw new ContextualError("[ClassDeclarationError] : the upper class "+ this.parent.getName () +" is not predefined", this.parent.getLocation());
        	}
        		// aprés avoir bien vérifier que le parent existe
        		// on associe le parent à sa définition
	        	this.parent.setDefinition(compiler.getEnvironmentType().get(symParent));
	    		this.parent.setType(this.parent.getDefinition().getType());
        		// When the superclass is in the environmentType, we take the definition of the parentclass at first
        		ClassDefinition defParent = (ClassDefinition)compiler.getEnvironmentType().get(symParent);
        		// We define the type and definition of the new class, it will be added to the environmentType
        		ClassType typ = new ClassType(this.ident.getName(),this.ident.getLocation(),defParent);
        		ClassDefinition def = new ClassDefinition(typ,this.ident.getLocation(),defParent);
        		compiler.getEnvironmentType().addTypeDefinition(this.ident.getName(),def);
        		this.ident.setType(typ);
        		this.ident.setDefinition(def);
        }

        LOG.debug("Verify Class: end");

    }

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
    		// super est associé à une définition de classe pendant la passe 1
			// check that the name and the super ont bien une définition
	 		LOG.debug("Verify ClassMembers: start");
			// si la passe 1 est bien faite alors
    		Validate.notNull(compiler.getEnvironmentType().get(this.ident.getName()));
    		Validate.notNull(compiler.getEnvironmentType().get(this.parent.getName()));
    		// We take the environmentExp to update
    		ClassDefinition identDef = (ClassDefinition) compiler.getEnvironmentType().get(this.ident.getName());
    		ClassDefinition parentDef = identDef.getSuperClass();
    		// We get the environmentExp of superClass
    		EnvironmentExp parentEnvExp =  parentDef.getMembers();
    		// lorsque la classe à été créer pendant la premier passe il a un environment exp bien définie
    		EnvironmentExp envCourant = identDef.getMembers();
    		// on va empiler son environment courant sur l'environment de son pere
    		envCourant.setParentEnvironment(parentEnvExp);
    		// on va initialiser le nombre de fields et des methode par le nombre de son pere
    		identDef.setNumberOfFields(parentDef.getNumberOfFields());
    	  	identDef.setNumberOfMethods(parentDef.getNumberOfMethods());
    	  	// apres on rajoute les nouveau fields
    		this.field.verifyListField(compiler,envCourant,this.ident.getName());
    		this.method.verifyListMethod(compiler,parentDef,identDef,envCourant);
    		// à la fin on a l'union disjointe des environment dans l'environment envCourant
    		// Check the new environment doesn't contain the values in the parent's environment
    		// emplier l'environment de la class superieur sur l'environment courant
    	  	identDef.setMembers(envCourant); // set l'environment courant
    	  	this.ident.setDefinition(identDef);
    	  	LOG.debug("Verify ClassMembers: end");
    }


    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
    	LOG.debug("Verify ClassBody: start");
    	// We take the environmentExp to update
    	// si la passe 1 et 2 sont biens faites
    	Validate.notNull(compiler.getEnvironmentType().get(this.ident.getName()));
		Validate.notNull(compiler.getEnvironmentType().get(this.parent.getName()));

		ClassDefinition currentClass = (ClassDefinition) compiler.getEnvironmentType().get(this.ident.getName());
    	EnvironmentExp env = currentClass.getMembers(); // environment de champ est methode
    	this.field.verifyListFieldVal(compiler, env, currentClass);
    	this.method.verifyListMethodBody(compiler, env, currentClass);
    	LOG.debug("Verify ClassBody: end");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	ident.prettyPrint(s, prefix, false);
    	if(parent!=null) {
            parent.prettyPrint(s, prefix, false);

    	}
        field.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	ident.iter(f);
    	if(parent!=null) {
    		parent.iter(f);

    	}
        field.iter(f);
        method.iter(f);
    }

    @Override
    protected void codeGenInitDeclClass(DecacCompiler compiler){
    	compiler.addComment("Code de la table des méthodes de " + this.ident.getName().getName());
    	//Recuperer l'addr de parent
    	if(parent.getName().getName().equals("Object") && parent.getClassDefinition().getOperand()==null){
    		parent.getClassDefinition().setOperand(compiler.getAddrObjet());
    	}
        compiler.addInstruction(new LEA(parent.getClassDefinition().getOperand(),Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(compiler.getGBmanager().getGB(),Register.GB)));
        ident.codeGenInitClass(compiler);
        int nombreMethod = 0;
        // on a ajouté les methode de parent
        if(!parent.getName().getName().equals("Object")){
        	for(int i=0;i<parent.getClassDefinition().getTabMethod().getnombre();i++) {
        		ident.getClassDefinition().getTabMethod().addMethod(nombreMethod, parent.getClassDefinition().getTabMethod().getMethode(nombreMethod));
        		nombreMethod++;
        	}
        }
        for(AbstractDeclMethod m : method.getList()){
        	Label methodName = new Label("code."+ident.getName().toString()+"."+m.getIdentifier().getName());
        	boolean extend = false;
        	for(int i=0;i<parent.getClassDefinition().getTabMethod().getnombre();i++) {
        		if(m.getIdentifier().getName()==parent.getClassDefinition().getTabMethod().getMethode(i).getIdentifier().getName()) {
        			this.ident.getClassDefinition().getTabMethod().addMethod(i, m);
        			extend = true;
        		}
        	}
        	if(extend == false) {
        		ident.getClassDefinition().getTabMethod().addMethod(nombreMethod, m);
                nombreMethod ++;
        	}  
        	m.getIdentifier().getMethodDefinition().setLabel(methodName);
        }
        //ajoute code.Objet.equals
        this.ident.getClassDefinition().setNumberOfMethods(nombreMethod);
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(compiler.getGBmanager().getGB(),Register.GB)));
        compiler.getGBmanager().addGB();
         Integer i=0;
         //init le table de methode
         while(this.ident.getClassDefinition().getTabMethod().hasKey(i)){
             compiler.addInstruction(new LOAD(new LabelOperand(
                     this.ident.getClassDefinition().getTabMethod().getMethode(i).getIdentifier().getMethodDefinition().getLabel()),
                     Register.R0));
             compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(compiler.getGBmanager().getGB(),Register.GB)));
             compiler.getGBmanager().addGB();
             i++;
         }
    }

    @Override
    protected void codeGenFieldClass(DecacCompiler compiler){
        compiler.addLabel(new Label("init."+ident.getName().toString())); //pour s'en rappeler
        int nombreField = 0;
        // on a ajouté les field de parent
        if(!parent.getName().getName().equals("Object")){
        	for(int i=0;i<this.parent.getClassDefinition().getTabFiled().getnombre();i++) {
        		boolean contain = false;
        		if(!field.isEmpty()) {
        			for(int j =0;j<this.field.size();j++) {
        				if(this.field.getList().get(j).getident().getName().equals(this.parent.getClassDefinition().getTabFiled().getField(i).getident().getName())) {
        					contain = true;
                		}
        			}  
        			if(contain == false) {
        				this.ident.getClassDefinition().getTabFiled().addField(nombreField, this.parent.getClassDefinition().getTabFiled().getField(i));
            			nombreField++;
        			}
        		}else {
        			this.ident.getClassDefinition().getTabFiled().addField(nombreField, this.parent.getClassDefinition().getTabFiled().getField(i));
        			nombreField++;
        		}
        	}
        }
     // on a ajouté les field de ident
        ListDeclField fieldcopy = new ListDeclField();
        if(!field.isEmpty()){  
        	for(int i =0;i<this.field.getList().size();i++) {
        		this.ident.getClassDefinition().getTabFiled().addField(nombreField, this.field.getList().get(i));
        		nombreField++;
        	}
        }
        for(int i=0;i<nombreField;i++) {
    		fieldcopy.add(this.ident.getClassDefinition().getTabFiled().getField(i));
    	}
        //init filed parent
        compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB),Register.R1));
	      if(!parent.getName().toString().equals("Object")){
	      compiler.addInstruction(new TSTO(3));
	      compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
	      compiler.addInstruction(new PUSH(Register.R1)); // on sauvegarde R1 pour la superclass
	      compiler.addInstruction(new BSR(new Label("init."+parent.getName().toString())));
	
	      compiler.addInstruction(new SUBSP(1));
	      }
	      //init field ident
        if(!fieldcopy.isEmpty()) {
        	compiler.setnbrfield(nombreField);
        	fieldcopy.codeGenListField(compiler);
        	this.ident.getClassDefinition().setNumberOfFields(nombreField);
        }
        compiler.addInstruction(new RTS());
    }

    @Override
    protected void codeGenMethodClass(DecacCompiler compiler){
        for(AbstractDeclMethod m : method.getList()){
            m.codeGenMethod(compiler);
        }

    }


    public int valSP(){
        int i=2;
        if (!parent.getName().toString().equals("Object")){
            i += parent.getClassDefinition().getNumberOfMethods();
        }
        
        i += method.size();
        return i;
    }

}
