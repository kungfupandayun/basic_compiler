package fr.ensimag.deca.tree;

import java.io.PrintStream;

/**
 * Exception corresponding to an error at a particular location in a file.
 *
 * @author gl07
 * @date 01/01/2020
 */
public class LocationException extends Exception {
	
	/**@return Location */
    public Location getLocation() {
        return location;
    }

    /**
     * Print out the location of the error
     */
    public void display(PrintStream s) {
        Location loc = getLocation();
        String line;
        String column;
        if (loc == null) {
            line = "<unknown>";
            column = "";
        } else {
            line = Integer.toString(loc.getLine());
            column = ":" + loc.getPositionInLine();
        }
        s.println(location.getFilename() + ":" + line + column + ": " + getMessage());
    }

    private static final long serialVersionUID = 7628400022855935597L;
    /** Private attribute to store the location*/
    protected Location location;

    /**
     * Constructor to save the message and the location of the error
     * @param message
     * @param location
     */
    public LocationException(String message, Location location) {
        super(message);
        assert(location == null || location.getFilename() != null);
        this.location = location;
    }

}
