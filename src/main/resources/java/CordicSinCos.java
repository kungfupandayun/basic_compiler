
public class CordicSinCos {
	 
    private double[] lookup;
    private final int iterations = 100;
    private final double K = 0.60725293510314;
    private final double halfPi = 1.57079632679;
 
    CordicSinCos() {
        createLookupTable();
    }
 
    public double sin(double theta) {
        return sinCos(theta)[1];
    }
 
    public double cos(double theta) {
        return sinCos(theta)[0];
    }
 
    public double[] sinCos(double theta) {
      if (theta < 0 || theta >halfPi) {
            throw new IllegalArgumentException("Required value 0 < x < Pi/2");
        }
        double x = K;
        double y = 0;
        double z = theta;
        double v = 1.0;
        for (int i=0; i<iterations; i++) {
            double d = (z >= 0)? +1 : -1;
            double tx = x - d * y * v;
            double ty = y + d * x * v;
            double tz = z - d * lookup[i];
            x = tx; y= ty; z = tz;
            v *= 0.5;
        }
        double[] result = {x,y};
        return result;
    }
 
    private void createLookupTable() {
        lookup = new double[iterations];
        for (int i=0; i<iterations; i++) {
            lookup[i] = Math.atan(1 / Math.pow(2,i));
            System.out.format("Tan (%02d): %.14f / %018.3f %n", i,  lookup[i], Math.pow(2,i));
        }
    }
 
    public static void main(String[] args) {
        CordicSinCos c = new CordicSinCos();
   	 	int signx;
   	 	int signy;
        double j;
        for (double i=0; i<90; i++) {
        	 j=i; 
        	 signx=1;
             signy=1;
        	 if(j<0) { signy=-1; 	j=-j;}
        	 if(j>=360) {j=(j%360);}
        	 if(j>=270 && j<360) {signy=signy*-1;
        	 					  j=360-j;}
        	 if(j>=180 && j<270) { signy=signy*-1;
        	 					   signx=-1;
        	 					   j=j-180;}
        	 if(j>=90&& j<180){    signx=-1;
        	 						j=180-j-1;}
            double rad = j * (Math.PI / 180);
            System.out.format("%.14f \n",signx*c.cos(rad));
//            System.out.format(
//            "Sin: %04.1f (rad: %.14f) CORDIC: %.14f ",signy*c.sin(rad), Math.sin(rad) );
//            System.out.format(
//                    "Cos: %04.1f (rad: %.14f) CORDIC: %.14f / java: %.14f %n", i, rad, signx*c.cos(rad), Math.cos(rad) );
        }
        	
    }
 
}
