package fr.ensimag.deca.context;
import static java.nio.file.StandardOpenOption.*;
import fr.ensimag.deca.CompilerOptions;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.syntax.AbstractDecaLexer;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 * Driver to test the contextual analysis (together with lexer/parser)
 *
 * @author Ensimag
 * @date 01/01/2020
 */
public class ManualTestContext {
    public static void main(String[] args) throws IOException {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        DecaLexer lex = AbstractDecaLexer.createLexerFromArgs(args);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);

        DecacCompiler compiler = new DecacCompiler(new CompilerOptions(), null);


        PrintStream stream=new PrintStream(System.out);
        IndentPrintStream idstream=new IndentPrintStream(stream);

        parser.setDecacCompiler(compiler);
        AbstractProgram prog = parser.parseProgramAndManageErrors(System.err);
        if (prog == null) {
            System.exit(1);
            System.out.print("hello");
            return; // Unreachable, but silents a warning.
        }
        try {
            prog.verifyProgram(compiler);
            prog.decompile(idstream);

        } catch (LocationException e) {
            e.display(System.err);
            System.exit(1);
        }
        prog.prettyPrint(System.out);
    }
}
