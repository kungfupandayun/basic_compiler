package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Syntax error for a Parse float
 * @author gl07
 *
 */
public class ParseFloatError extends DecaRecognitionException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */


	public ParseFloatError(DecaParser recognizer, ParserRuleContext ctx) {
        super(recognizer, ctx);
    }

    
	@Override
    public String getMessage() {
        return "[ParseFloatError] literal values cannot be infinite";
    }
}
