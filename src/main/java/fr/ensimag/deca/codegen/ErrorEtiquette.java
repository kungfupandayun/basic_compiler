package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.util.LinkedList;
import fr.ensimag.ima.pseudocode.Instruction;

/**
 * Etiquettes pour enregistrer et gerer les erreurs
 *
 * @author gl07
 * @date 16/01/2020
 */

public class ErrorEtiquette {
  private LinkedList<Instruction> list = new LinkedList<Instruction>();

  public ErrorEtiquette(String errorMessage){
    this.list.add(new WSTR(errorMessage));
    this.list.add(new WNL());
    this.list.add(new ERROR());
  }

  /**
   * Recuperer la liste des instructions.
   * @return list de l'instructions
   */
  public LinkedList<Instruction> getListError(){
    return this.list;
  }
}
