
public class Algorithme {
	  public static void main(String[] args) {
		  /**
		   * Calsin prend un float en radian
		   */
		  double javaSin = Math.sin(3.14); 
		  float decaSin = CalSin((float)1);
		  double erreur =  javaSin - decaSin ;
		  System.out.println("Sin java  : "  + javaSin);
		  System.out.println("Sin deca : " + decaSin);
		  System.out.println("Erreur : " + erreur);
	  }

	private static float CalSin(float x) {
		  int NMAX = 10000000 ;
		  float sum = 0;
		  int n = 1;
		  boolean pair = false;
		  float dom = 1;
		  float num = x;
		  while (n <= NMAX )
		  {
			  dom = dom*(dom + 1)*(dom + 2);
			  num = num*num*num;
			  if (pair) {
				  sum = sum + (num/dom);
				  pair = false;
			  } else {
				  sum = sum - (num/dom);
				  pair = true;
			  }
			  n = n + 1;
		  }
		  return sum;
	}
	
//	private static float CordicTan(float beta) {
//		
//		// x in radians
//		float epsilon = (float)0.00000000000001;
//		float pi = (float) 3.1415926535897932384626433832795;
//		while(beta >= epsilon)
//		{
//			beta = beta - 
//		}
		
		
//	}
	
}
