
public class ULP {
	/*
	 * use a precalculated value for the ulp of Double.MAX_VALUE
	 */
	private static final double MAX_ULP = 1.9958403095347198E292;

	/**
	 * Returns the size of an ulp (units in the last place) of the argument.
	 * @param d value whose ulp is to be returned
	 * @return size of an ulp for the argument
	 */
	public double ulp(double d) {
	    if (Double.isNaN(d)) {
	        // If the argument is NaN, then the result is NaN.
	        return Double.NaN;
	    }

	    if (Double.isInfinite(d)) {
	        // If the argument is positive or negative infinity, then the
	        // result is positive infinity.
	        return Double.POSITIVE_INFINITY;
	    }

	    if (d == 0.0) {
	        // If the argument is positive or negative zero, then the result is Double.MIN_VALUE.
	        return Double.MIN_VALUE;
	    }

	    d = Math.abs(d);
	    if (d == Double.MAX_VALUE) {
	        // If the argument is Double.MAX_VALUE, then the result is equal to 2^971.
	        return MAX_ULP;
	    }
	    System.out.print("absStart2=>"+d+"\n");
	    return nextAfter(d, Double.MAX_VALUE) - d;
	}

	public double copySign(double x, double y) {
		
		return Math.copySign(x,y);
	}

	private boolean isSameSign(double x, double y) {
	    return copySign(x, y) == x;
	}

	/**
	 * Returns the next representable floating point number after the first
	 * argument in the direction of the second argument.
	 *
	 * @param start starting value
	 * @param direction value indicating which of the neighboring representable
	 *  floating point number to return
	 * @return The floating-point number next to {@code start} in the
	 * direction of {@direction}.
	 */
	public double nextAfter(final double start, final double direction) {
	    if (Double.isNaN(start) || Double.isNaN(direction)) {
	        // If either argument is a NaN, then NaN is returned.
	        return Double.NaN;
	    }
	    System.out.print("start=>"+start+"\n");

	    if (start == direction) {
	        // If both arguments compare as equal the second argument is returned.
	        return direction;
	    }

	    final double absStart = Math.abs(start);
	    final double absDir = Math.abs(direction);
	    System.out.print("absDir=>"+direction+"\n");
	    System.out.print("absStart=>"+start+"\n");
	    final boolean toZero = !isSameSign(start, direction) || absDir < absStart;

	    if (toZero) {
	        // we are reducing the magnitude, going toward zero.
	        if (absStart == Double.MIN_VALUE) {
	        	System.out.print("start1=>"+start+"\n");
	            return copySign(0.0, start);
	        }
	        if (Double.isInfinite(absStart)) {
	        	System.out.print("absstart1=>"+absStart+"\n");
	            return copySign(Double.MAX_VALUE, start);
	        }
	        return copySign(Double.longBitsToDouble(Double.doubleToLongBits(absStart) - 1L), start);
	    } else {
	        // we are increasing the magnitude, toward +-Infinity
	        if (start == 0.0) {
	        	System.out.print("start2=>"+start+"\n");
	            return copySign(Double.MIN_VALUE, direction);
	        }
	        if (absStart == Double.MAX_VALUE) {
	        	System.out.print("absStart2=>"+absStart+"\n");
	            return copySign(Double.POSITIVE_INFINITY, start);
	        }
	        System.out.print("valeur=>"+(Double.longBitsToDouble(Double.doubleToLongBits(absStart)+1L))+"\n");
	        return copySign(Double.longBitsToDouble(Double.doubleToLongBits(absStart) + 1L), start);
	    }
	}
	

    public static void main(String[] args) {
    	ULP c = new ULP();
    	System.out.print("hello"+c.ulp(14)+"\n");
    	double x=1.2222+c.ulp(-14);
    	System.out.print(x+"\n");
    	
    }
}

