package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.Program;
import fr.ensimag.deca.tree.AbstractMain;
import fr.ensimag.deca.tree.ListDeclClass;
import fr.ensimag.deca.tree.AbstractDeclClass;

/**
 * Manager of the Stack Pointer (SP).
 *
 * @author gl07
 * @date 14/01/2020
 */

public class SPmanager {

  /*On pointe vers la troisiéme case de la pile en comptant d'en bas
  en reservant les deux premiéres à NULL et à OBJECT EQUALS*/
	/** à incrementer pendant la création de la table de méthode */
  private int valSP = 3;

  /**
   * Getter of the SP's actual value.
   * 
   * @return valSP The SP's actual value.
   */
  public int getvalSP(){
    return this.valSP;
  }

  /**
   * Returns the value of SP that we need to save in the TSTO instruction.
   * @param program the program we want to get the SP value of.
   * @return maximum value the stac pointer can attends for a defined program.
   */
  public int valSP(Program program){
  	//Etape unique pour le langage sans object.
    AbstractMain main = program.getMain();
    this.valSP += main.valSP();
    //Etape à ajouter pour les autres langages.
    ListDeclClass classes = program.getClasses();
    int i=1;
    for(AbstractDeclClass classe : classes.getList()){
        i += classe.valSP();
    }
    this.valSP += i;
    return this.getvalSP();
  }

  /**
   * A method that returns the number of classes a program contains.
   * @param program the program we want to get the number of classes of.
   * @return the number of classes a program contains.
   */
  public int valSPclasses(Program program){
    ListDeclClass classes = program.getClasses();
    int i=2;
    for(AbstractDeclClass classe : classes.getList()){
      i += classe.valSP();
    }
    return i;
  }

  /**
   * A method that returns the number of global variables a program contains.
   * @param program the program we want to get the number of global variables it contains.
   * @return the number of global variables a program contains.
   */
  public int nbrVarGlobale(Program program){
    AbstractMain main = program.getMain();
    int varGlobale = main.varGlobale();
    return varGlobale;
  }
}
