/* A manual test for the initial sketch of code generation included in
 * students skeleton.
 *
 * It is not intended to still work when code generation has been updated.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import java.util.Scanner;

/**
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class Test {

    public static AbstractProgram toAbstractProg(String path) {
        ListInst linst = new ListInst();
        AbstractProgram source =
            new Program(
                new ListDeclClass(),
                new Main(new ListDeclVar(),linst));
        ListExpr lexp1 = new ListExpr();
        linst.add(new Println(false,lexp1));
        lexp1.add(new StringLiteral(path));
        return source;
    }

    public static String gencodeSource(AbstractProgram source) {
        DecacCompiler compiler = new DecacCompiler(null,null);
        source.codeGenProgram(compiler);
        return compiler.displayIMAProgram();
    }

    public static void test1(String path) {
        AbstractProgram source = toAbstractProg(path);
        System.out.println("---- From the following Abstract Syntax Tree ----");
        source.prettyPrint(System.out);
        System.out.println("---- We generate the following assembly code ----");
        String result = gencodeSource(source);
        System.out.println(result);
        // assert(result.equals(
        //         "; Main program\n" +
        //         "; Beginning of main function:\n" +
        //         "	WSTR \"Hello world!\"\n" +
        //         "	WNL\n" +
        //         "	HALT\n"));
    }



    public static void main(String args[]) {
        Scanner scanner = new Scanner(System. in);
        String inputString = scanner. nextLine();
        test1(inputString);
    }
}
