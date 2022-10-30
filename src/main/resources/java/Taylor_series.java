
public class Taylor_series {
	
	double factorial (int n)
	{   double fact=1,
	    flag;
	   for (flag = 1; flag <= n; flag++)
	   {
	        fact *= flag;
	   }
	   return fact;
	}

	
	
	double sin(double rad) {
		
		Taylor_series t=new Taylor_series();
		double sum=0;
		int i;
		double sign=1;
		for(i=0;i<100;i++) {
			if(i%2==1) sign=-1; 
			else sign=1;
			sum+=(sign*( Math.pow(rad,(2*i)+1))/t.factorial((2*i)+1));
		}

		return sum;	
	}
	
	
	double cos(double rad) {
		
		Taylor_series t=new Taylor_series();
		double sum=0;
		int i;
		double sign=1;
		for(i=0;i<100;i++) {
			if(i%2==1) sign=-1; 
			else sign=1;
			sum+=(sign*( Math.pow(rad,2*i))/t.factorial(2*i));
		}

		return sum;	
	}
	public static void main(String[] args) {
		
		Taylor_series c = new Taylor_series();
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

	    
	    System.out.format("%.14f \n", signx*c.cos(rad));
//        System.out.format(
//        "Sin: %04.1f (rad: %.14f) TAYLOR: %.14f / java: %.14f %n", i, rad,signy*c.sin(rad), Math.sin(rad) );
//        System.out.format(
//                "Cos: %04.1f (rad: %.14f) TAYLOR: %.14f / java: %.14f %n", i, rad, signx*c.cos(rad), Math.cos(rad) );

	}

}
	
}
