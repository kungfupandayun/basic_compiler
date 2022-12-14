
// Cordic implemented in Java. (C) 2014 DJ Greaves, University of Cambridge, Computer Laboratory.

public class Cordic 
{
    static final int iterations = 32;

    static double [] ctab = new double [iterations];

    public static void Table_Generate() 
    {
	double theta = 1.0;
	for (int i=0; i<iterations; i++)
	    {
		ctab[i] = Math.atan(theta);
		System.out.printf("Cordic table %d is arctan %.17f = %.17f\n", i, theta, ctab[i]);
		theta = theta / 2.0;
	    }
    }

    public static double cordic(double theta) 
    {
	int x=607252935 /*prescaled constant for iterations=32*/, y=0;
	// x = (int)(0.60725293500888 * 1e9);
	//System.out.printf("Starting value is %d\n", x);
	for (int k=0; k<iterations; ++k)
	    {
		int tx, ty;
		if (theta >= 0) // All nice simple integer arithmetic
		    {
			tx = x - (y>>k);
			ty = y + (x>>k);
			theta -= ctab[k];
		    }
		else
		    {
			tx = x + (y>>k);
			ty = y - (x>>k);
			theta += ctab[k];
		    }
		x = tx; y = ty;
	    }  
	return (double)x; // Returns cosine * 10^9.
    }

    public static void main(String[] args) 
    {
	Table_Generate();
	double [] tests = { 0.0, 1.1, Math.PI/2.0, Math.PI/3.0, Math.PI*2.0/3.0, };
	for (int i=0; i<tests.length; i++)
	    {
		double theta = tests[i];
		double answer = cordic(theta) / 1e9;
		System.out.printf("Cordic %f is %f cf %f\n", theta, answer, Math.cos(theta));
		double error = answer - Math.cos(theta);
		System.out.println(" Error  Cos :" +  error);
	    }
    }
}


