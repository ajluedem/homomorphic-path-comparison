import java.lang.*;
import java.math.*;
import java.util.*;

public class Main {

     static class BigIntPoint
     {
          BigInteger x;
          BigInteger y;

               public BigIntPoint(BigInteger x, BigInteger y)
               {
                    this.x = x;
                    this.y = y;
               }
     }

     public static BigInteger maxBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) == -1) return b;
          else return a;

     }
     public static BigInteger minBigInt(BigInteger a, BigInteger b)
     {
          if(a.compareTo(b) == -1) return a;
          else return b;

     }

     public static boolean geBigInt(BigInteger a, BigInteger b) {
          if (a.compareTo(b) == -1) return false;
          else return true;

     }
     public static boolean leBigInt(BigInteger a, BigInteger b){
          if(a.compareTo(b) == 1) return true;
          else return false;
     }
     static boolean onSegment(BigIntPoint p, BigIntPoint q, BigIntPoint r)
     {
          if (leBigInt(q.x,(maxBigInt(p.x, r.x))) && geBigInt(q.x,(minBigInt(p.x,r.x)))
                  && leBigInt(q.y,(maxBigInt(p.y,r.y))) && leBigInt(q.y,(minBigInt(p.y,r.y))))
               return true;

          return false;
     }

     static int orientation(BigIntPoint p, BigIntPoint q, BigIntPoint r)
     {
          BigInteger temp1 = q.y.subtract(p.y);
          BigInteger temp2 = r.x.subtract(q.x);
          BigInteger temp3 = q.x.subtract(p.x);
          BigInteger temp4 = r.y.subtract(q.y);
          BigInteger temp5 = temp1.multiply(temp2);
          BigInteger temp6 = temp3.multiply(temp4);
          BigInteger val = temp5.subtract(temp6);

          int ans = val.compareTo(BigInteger.ZERO);

          if (ans==0) return 0;

          else if (ans == 1) return 1;

          else return 2;
     }

     static boolean doIntersect(BigIntPoint p1, BigIntPoint q1, BigIntPoint p2, BigIntPoint q2)
     {
          int o1 = orientation(p1,q1,p2);
          int o2 = orientation(p1,q1,q2);
          int o3 = orientation(p2,q2,p1);
          int o4 = orientation(p2,q2,q1);

          if (o1 != o2 && o3 != o4)
               return true;

          if (o1 == 0 && onSegment(p1,p2,q1)) return true;
          if (o2 == 0 && onSegment(p1,q2,q1)) return true;
          if (o3 == 0 && onSegment(p2,p1,q2)) return true;
          if (o4 == 0 && onSegment(p2,q1,q2)) return true;

          return false;
     }
    public static void main (String[] args) {
         ArrayList<BigIntPoint> ownroute = new ArrayList<BigIntPoint>();
         ArrayList<BigIntPoint> cryptroute = new ArrayList<BigIntPoint>();

         // Clumsy way to build drones routes, probably update to allow user input
         BigInteger xone = BigInteger.valueOf(25);
         BigInteger yone = BigInteger.valueOf(25);
         BigInteger xtwo = BigInteger.valueOf(-25);
         BigInteger ytwo = BigInteger.valueOf(-25);
         BigIntPoint firstpoint = new BigIntPoint(xone,yone);
         BigIntPoint secondpoint = new BigIntPoint(xtwo,ytwo);

         //Clumsily building the cryptroute
         BigInteger cxone = BigInteger.valueOf(-25);
         BigInteger cyone = BigInteger.valueOf(25);
         BigInteger cxtwo = BigInteger.valueOf(25);
         BigInteger cytwo = BigInteger.valueOf(-25);
         BigIntPoint cfirstpoint = new BigIntPoint(cxone,cyone);
         BigIntPoint csecondpoint = new BigIntPoint(cxtwo,cytwo);

         //Assemble BigInt "points" into ArrayList
         ownroute.add(firstpoint);
         ownroute.add(secondpoint);

         cryptroute.add(cfirstpoint);
         cryptroute.add(csecondpoint);


         //Test for intersection
         for (int i = 0; i < (ownroute.size()-1); i++){
              for (int j = 0; j < (cryptroute.size()-1); j++){
                   if(doIntersect(ownroute.get(i),ownroute.get(i+1),cryptroute.get(j),cryptroute.get(j+1)))
                        System.out.println("Yes");
              }
         }

         }
}