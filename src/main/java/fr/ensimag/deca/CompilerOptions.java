 package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class CompilerOptions {


    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    private int debug = 0;
    private boolean parsed = false;
    private boolean verification = false;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean register = false;
    private boolean noCheck = false;
	  private int nbReg;
    private List<File> sourceFiles = new ArrayList<File>();

    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean isParsed() {
		    return parsed;
	  }

    public boolean getVerification(){
      return this.verification;
    }

  	public boolean isNoCheck() {
		return noCheck;
	}

	public void setNoCheck(boolean noCheck) {
		this.noCheck = noCheck;
	}

	public void setParsed(boolean parsed) {
  		this.parsed = parsed;
  	}


    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
    	int i = 0;
    	while (i<args.length) {
    		if (args[i].equals("-v")) {
    			// arreter decac aprés l'étape de vérification.
    			this.verification = true;
    			i++;
    		}
    		else if (args[i].equals("-p")) {
    			// parse arreter decac après l'étape de construction d l'arbre.
    			this.parsed = true;
    			i++;
    		}
        else if (args[i].equals("-n")) {
    			// lancer decac sans l'étape de verifications des erreurs.
    			this.noCheck = true;
    			i++;
    		}
    		else if (args[i].equals("-d")) {
    			this.debug += 1 ;
    			i++ ;
    		}
    		else if (args[i].equals("-P")) {
    			// pour plusieurs fichier source lancer la compilation
    			// des fichiers en parallel
    			this.parallel = true;
    			i++ ;
    		} else if (args[i].equals("-b")) {
    			// afficher une bannière indiquant le nom de l'équipe.
    			this.printBanner = true;
          i++;
    		} else if(args[i].equals("-r")) {
    			// -r X
    			this.register = true;
    			try {
    				this.nbReg = Integer.parseInt(args[i+1]);
    				if (this.nbReg < 4 || this.nbReg > 16) {
    					throw new  CLIException("le nombre de registre doit etre  entre 4 et 16");
    				}
    			//if (t)
    			i = i+2;
    			} catch (Exception e) {
    				throw new  CLIException("Il faut rentrer un nombre de registre -r X entre 4 et 16");
    			}
    		}
    		else if (args[i].contains("deca")) {
          //Traitée
    			File file =new File(args[i]);
    			if (sourceFiles.contains(file)){
    				i++;
    			} else {
    				sourceFiles.add(file);
    				i++ ;
    			}
    		}
            if(this.isParsed() && this.getVerification()){
                throw new CLIException("Les options -v et -p sont incompatibles");
            }
    	}



        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

    }

    public boolean isRegister() {
		return register;
	}

	public int getNbReg() {
		return nbReg;
	}

	public void setPrintBanner(boolean printBanner) {
		this.printBanner = printBanner;
	}

	protected void displayUsage() {
        System.out.println("La syntaxe d’utilisation de l’exécutable decac est :");
        System.out.println("decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println(" -b (banner)		:	affiche une bannière indiquant le nom de l’équipe");
        System.out.println(" -p (parse)		:	arrête decac après l’étape de construction de\n \t\t\t l’arbre, et affiche la décompilation de ce dernier ");
        System.out.println("\t\t\t (i.e. s’il n’y a qu’un fichier source à");
        System.out.println("\t\t\t compiler, la sortie doit être un programme \n \t\t\t deca syntaxiquement correct)");
        System.out.println(" -v (verification)	:	arrête decac après l’étape de vérifications");
        System.out.println("\t\t\t (ne produit aucune sortie en l’absence d’erreur)");
        System.out.println(" -n (no check) 		: supprime les tests de débordement à l’exécution ");
        System.out.println("\t\t\t\t  - débordement arithmétique \n\t\t\t\t  - débordement mémoire\n" +
        		"\t\t\t\t - déréférencement de null");
        System.out.println(" -r X (registers)	: limite les registres banalisés disponibles à");
        System.out.println("\t\t\t R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println(" -d (debug)		: active les traces de debug. Répéter ");
        System.out.println("\t\t\t l’option plusieurs fois pour avoir plus de traces");
        System.out.println(" -P (parallel)		: s’il y a plusieurs fichiers sources,");
        System.out.println("\t\t\t lance la compilation des fichiers en");
        System.out.println("\t\t\t parallèle (pour accélérer la compilation)");
    }
}
