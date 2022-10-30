package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Syntax error for a Parse int
 * @author gl07
 *
 */
public class ParseIntError extends DecaRecognitionException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ParseIntError(DecaParser recognizer, ParserRuleContext ctx) {
        super(recognizer, ctx);
    }

    
	@Override
    public String getMessage() {
        return "[ParseINTError]  literal values cannot be infinite";
    }

}
