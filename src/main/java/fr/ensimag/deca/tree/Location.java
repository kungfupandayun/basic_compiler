package fr.ensimag.deca.tree;

import java.io.Serializable;

/**
 * Location in a file (File, line, positionInLine).
 *
 * @author gl07
 * @date 01/01/2020
 */
public class Location implements Serializable {
    /*
     * Location implements Serializable because it appears as a field
     * of LocationException, which is serializable.
     */
    private static final long serialVersionUID = -2906437663480660298L;

    public static final String NO_SOURCE_NAME = "<no source file>";
    public static final Location BUILTIN = new Location(-1, -1, NO_SOURCE_NAME);

    
    /**
     * Display the (line, positionInLine) as a String. The file is not
     * displayed.
     */
    @Override
    public String toString() {
        if (this == BUILTIN) {
            return "[builtin]";
        } else {
            return "[" + line + ", " + positionInLine + "]";
        }
    }
    /**
     * @return line of the location
     */
    public int getLine() {
        return line;
    }

    /**
     * @return column of the location
     */
    public int getPositionInLine() {
        return positionInLine;
    }

    /**
     * @return name of file
     */
    public String getFilename() {
        if (filename != null) {
            return filename;
        } else {
            // we're probably reading from stdin
            return NO_SOURCE_NAME;
        }
    }

    /**private attribute to store line*/
    private final int line;
    /**private attribute to store column*/
    private final int positionInLine;
    /**private attribute to store name of file*/
    private final String filename;

    /**
     * Store the filename, line and column 
     * @param line is a INT type
     * @param positionInLine is a INT type
     * @param filename is a STRING type
     */
    public Location(int line, int positionInLine, String filename) {
        super();
        this.line = line;
        this.positionInLine = positionInLine;
        this.filename = filename;
    }

}
