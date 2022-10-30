package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WNL;

/**
 * Print expressions in a new line
 * @author gl07
 * @date 01/01/2020
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
      //faut voir quand est ce aue on fait le codegenPrintX later
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }

    @Override
    String getSuffix() {
        return "ln";
    }

    @Override
    public void decompilePrint(IndentPrintStream s, boolean x) {
        if(x) {
        	s.print("printlnx(");
        } else {
        	s.print("println(");
        }
        this.getArguments().decompile(s);
        s.print(");");
    }
}
