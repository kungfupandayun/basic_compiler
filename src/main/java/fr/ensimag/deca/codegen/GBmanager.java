package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.Program;
import fr.ensimag.deca.tree.AbstractMain;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.tools.DecacInternalError;


/**
 * Manager of the Global Pointer (GP).
 *
 * @author gl07
 * @date 14/01/2020
 */

public class GBmanager {

  private boolean tabRegistre[];
  private int valGB;
  private int length;
  private int RMAX;

  /**
   * the GB manager's constructor.
   *
   * @param nbr_registres how many registers do we actually have.
   * @param RMAX the maximum number of registers we are allowed to use.
   */
  public GBmanager(int nbr_registres, int RMAX){
	  	this.RMAX = RMAX;  
    	if (nbr_registres > this.RMAX) {
    		this.tabRegistre = new boolean[this.RMAX];
    		for(int i=0;i<this.RMAX;i++) {
    	   		this.tabRegistre[i] = false;
    	   		this.length = this.RMAX;
    		}
    	}else {
    		this.tabRegistre = new boolean[nbr_registres];
    		for(int i=0;i<nbr_registres;i++) {
    			this.tabRegistre[i] = false;
    			this.length = nbr_registres;
    		}
    	}
    this.valGB = 1;

  }
  /**
   * set le GBmanager
   *
   * @param tab[] un table de type boolean
   */
  public void setGBmanager(boolean tab[]){
    this.tabRegistre = tab;
  }
  /**
   * Renvoie l'indice de register
   *
   * @return l'indice plus petit de register qui est libre
   */
  public int getValGB(){
        int i=2;
        while(i < this.getLength()-1 && this.tabRegistre[i]==true){
          i++;
        }
        tabRegistre[i]=true;
        return i;
    }
    /**
     * Get le valeur GB que nous allons garder jusqu'a fin de program
     *
     * @return valeur de register que nous ne vont pas toucher apres
     */
  public int getGB(){
    return this.valGB;
  }
  /**
   * returns true if all registers are full
   *
   * @return true or false
   */
  public boolean remplir() {
	  return this.tabRegistre[this.length-1];
  }

  /**
   * return length of the table
   *
   * @return length of table
   */
  public int getLength() {
	  return this.length;
  }
  /**
   * incrementer la valeur de valGB
   *
   */
  public void addGB(){
      //this.tabRegistre[this.valGB]=true;
      this.valGB ++;
  }
  /**
   * Permet d'avoir un table de register qui ne point pas directment sur this.tabRegistre
   *
   * @return un table de boolean qui est le copie de tableregister
   */
  public boolean[] getCopyTabRegister() {
	  boolean[] tab = new boolean[this.length];
	  for(int i = 0; i <this.length; i++) {
		  tab[i] = this.tabRegistre[i];
	  }
	  return tab;
  }

    public GPRegister getFreeRegister(){
        int cmp = 2;
        boolean done = false;
        while (cmp < this.RMAX || (!done)){
            if (!this.tabRegistre[cmp]){
                done = true;
                this.tabRegistre[cmp] = true;
                return Register.getR(cmp);
            } else {
                cmp += 1;
            }
        }
        throw new DecacInternalError("There is not enough registers.");
    }
}
