import java.math.BigInteger;
import java.util.Scanner;

public class Curves {
    private BigInteger p;
    private BigInteger a;
    private BigInteger b;

    public Curves(BigInteger p, BigInteger a, BigInteger b)
    {
        this.p = p;
        this.a = a;
        this.b = b;
    }
    private DotsGroup Inf = new DotsGroup(BigInteger.ZERO,BigInteger.ONE,BigInteger.ZERO);
    public class DotsGroup{
        private BigInteger x;
        private BigInteger y;
        private BigInteger z;
        public DotsGroup(BigInteger x, BigInteger y, BigInteger z)
        {
            /*if(z.equals(BigInteger.ONE)) {
                this.x = x;
                this.y = y;
            }
            else {
                this.x = x.multiply(z.modInverse(p)).mod(p);
                this.y = y.multiply(z.modInverse(p)).mod(p);
            }*/
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void IsPoint (BigInteger p, BigInteger a, BigInteger b, BigInteger x, BigInteger y)
        {
            BigInteger ysq= ((x.modPow(BigInteger.valueOf(3),p)).add(a.multiply(x))).add(b);
            ysq = ysq.mod(p);
            if(ysq.equals(y.modPow(BigInteger.TWO,p))) System.out.println("Consist");
            else System.out.println("OOPS... NOT Consist");
        }

        public DotsGroup PointDouble(DotsGroup P)
        {
            if(P == Inf) return Inf;
            else if(P.y.equals(BigInteger.ZERO)) return Inf;
            BigInteger W = a.multiply((P.z).modPow(BigInteger.TWO,p));
            W = W.add((P.x).modPow(BigInteger.TWO, p).multiply(BigInteger.valueOf(3)));
            BigInteger S = P.y.multiply(P.z).mod(p);
            BigInteger B = S.multiply(P.x.multiply(P.y)).mod(p);
            BigInteger H = W.modPow(BigInteger.TWO, p).subtract(B.multiply(BigInteger.valueOf(8))).mod(p);
            BigInteger x1 = BigInteger.TWO.multiply(H.multiply(S)).mod(p);
            BigInteger y1 = (W.multiply(((BigInteger.valueOf(4).multiply(B)).subtract(H)))).subtract((BigInteger.valueOf(8).multiply(P.y.modPow(BigInteger.TWO, p)).multiply(S.modPow(BigInteger.TWO, p))));
            y1 = y1.mod(p);
            BigInteger z1 = BigInteger.valueOf(8).multiply(S.modPow(BigInteger.valueOf(3), p)).mod(p);
            return new DotsGroup(x1,y1,z1);
        }

        public DotsGroup PointsAdd(DotsGroup P, DotsGroup Q)
        {
            if(P == Inf) return Q;
            else if(Q == Inf) return P;
            BigInteger u1 = Q.y.multiply(P.z).mod(p);
            BigInteger u2 = P.y.multiply(Q.z).mod(p);
            BigInteger v1 = Q.x.multiply(P.z).mod(p);
            BigInteger v2 = P.x.multiply(Q.z).mod(p);
            if(v1.equals(v2))
            {
                if(!u1.equals(u2)) return Inf;
                if(u1.equals(u2)) return PointDouble(P);
            }
            BigInteger u = u1.subtract(u2).mod(p);
            BigInteger v = v1.subtract(v2).mod(p);
            BigInteger w = P.z.multiply(Q.z);
            BigInteger A = ((u.modPow(BigInteger.TWO, p).multiply(w)).subtract(v.modPow(BigInteger.valueOf(3), p))).subtract(BigInteger.TWO.multiply(v.modPow(BigInteger.TWO, p)).multiply(v2)).mod(p);
            BigInteger x3 = v.multiply(A).mod(p);
            BigInteger y3 = u.multiply((v.modPow(BigInteger.TWO, p).multiply(v2)).subtract(A)).subtract(v.modPow(BigInteger.valueOf(3), p).multiply(u2)).mod(p);
            BigInteger z3 = v.modPow(BigInteger.valueOf(3), p).multiply(w).mod(p);
            return new DotsGroup(x3, y3, z3);
        }

        public DotsGroup ScalarMultipliacationMontgomery(DotsGroup P, BigInteger scalar)
        {
            DotsGroup r0 = Inf;
            DotsGroup r1 = P;
            String bitsline = scalar.toString(2);
            for (int i = 0; i < bitsline.length() ; i++)
            {
                if(bitsline.charAt(i) - 48 == 0)
                {
                    r1 = PointsAdd(r0,r1);
                    r0 = PointDouble(r0);
                }
                else {
                    r0 = PointsAdd(r0, r1);
                    r1 = PointDouble(r1);
                }
            }
            return r0;
        }

    }


    public static void main(String[] args) throws Exception {
        /*BigInteger p = new BigInteger("ffffffffffffffffffffffffffffffff000000000000000000000001", 16);
        BigInteger a = new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffffffffffe", 16);
        BigInteger b = new BigInteger("b4050a850c04b3abf54132565044b0b7d7bfd8ba270b39432355ffb4", 16);*/
        BigInteger p = new BigInteger("11", 10);
        BigInteger a = new BigInteger("1", 10);
        BigInteger b = new BigInteger("5", 10);
        Curves E = new Curves(p,a,b);
        Curves.DotsGroup P = E.new DotsGroup(BigInteger.valueOf(0),BigInteger.valueOf(4), BigInteger.ONE);
        P.IsPoint(E.p, E.a, E.b, P.x, P.y);
        DotsGroup DoubleP = P.PointDouble(P);
        System.out.println(DoubleP.x + " " + DoubleP.y + " " + DoubleP.z);

        Curves.DotsGroup Q = E.new DotsGroup(BigInteger.valueOf(2),BigInteger.valueOf(9), BigInteger.ONE);
        DotsGroup PSumQ = P.PointsAdd(P, Q);
        System.out.println(PSumQ.x + " " + PSumQ.y + " " + PSumQ.z);

        BigInteger scalar = new BigInteger("8", 10);
        DotsGroup Pscalar = P.ScalarMultipliacationMontgomery(P, scalar);
        System.out.println(Pscalar.x + " " + Pscalar.y + " " + Pscalar.z);
    }
}
