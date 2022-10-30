package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }



        if (options.getPrintBanner()) {
            System.out.println("Le groupe gl07");
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            System.err.println("[Failed operation] : no source file. ");
            options.displayUsage();
            System.exit(0);
        }

        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
        	// obtenir le nombre de thread disponible dans la machine
        	int nbProcs = Runtime.getRuntime().availableProcessors();
        	ExecutorService executor = Executors.newFixedThreadPool(nbProcs);
        	List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
        	for (File source : options.getSourceFiles()) {
        		Callable<Boolean> task = new DecacCompiler(options, source);
        		Future<Boolean> future = executor.submit(task);
        		list.add(future);
            }
        	// attendre que toutes les taches soientt terminées.
        	for (Future<Boolean> future: list) {
        		try {
    					if (future.get()) {
    						error = true;
    					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
              	}
        	 //shut down the executor service now
            executor.shutdown();

        } else {

            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
