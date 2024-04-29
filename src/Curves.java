import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
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
    private static BigInteger BlumBlumaBits(BigInteger r1, BigInteger p) {
        String bits = p.toString(2);
        StringBuilder blbl = new StringBuilder("1");
        StringBuilder temp = new StringBuilder();
        BigInteger P = new BigInteger("D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
        BigInteger Q = new BigInteger("425D2B9BFDB25B9CF6C416CC6E37B59C1F", 16);
        BigInteger n = P.multiply(Q);
        BigInteger r2;
        int i = 0;
        while (i == 0)
        {
            blbl = new StringBuilder();
            i = 1;
            while (blbl.length() != bits.length()) {
                r2 = r1.modPow(BigInteger.TWO, n);
                String last = r2.toString(2);
                temp.append(last.charAt(last.length() - 1));
                blbl.append(temp);
                temp = new StringBuilder();
                r1 = r2;
            }
            if(new BigInteger(blbl.toString(),2).compareTo(p) == -1) i = 1;
        }

        BigInteger result = new BigInteger(blbl.toString(), 2);
        return result;
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

        public void IsPoint (DotsGroup P)
        {
            BigInteger ysq= ((P.x.modPow(BigInteger.valueOf(3),p)).add(a.multiply(P.x))).add(b);
            ysq = ysq.mod(p);
            if(ysq.equals(P.y.modPow(BigInteger.TWO,p))) System.out.println("Consist");
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

        public void ToAffine(DotsGroup P)
        {
            if(P == Inf) System.out.println("P = Oe");
            else {
                System.out.println("P = (" + P.x.multiply(P.z.modInverse(p)).mod(p) + ", " + P.y.multiply(P.z.modInverse(p)).mod(p) + ")");
            }
        }

        public void ToProjective(BigInteger x, BigInteger y, BigInteger z)
        {
            System.out.println("P = (" + x.multiply(z.modInverse(p)).mod(p) + ", " + y.multiply(z.modInverse(p)).mod(p) + ", " + z + ")");
        }

        public void Pointsout()
        {
            System.out.println("P = (" + x + ", " + y + ", " + z + ")");
        }
    }


    public static void main(String[] args) throws Exception {
        BigInteger p = new BigInteger("ffffffffffffffffffffffffffffffff000000000000000000000001", 16);
        BigInteger a = new BigInteger("fffffffffffffffffffffffffffffffefffffffffffffffffffffffe", 16);
        BigInteger b = new BigInteger("b4050a850c04b3abf54132565044b0b7d7bfd8ba270b39432355ffb4", 16);
        BigInteger n = new BigInteger("ffffffffffffffffffffffffffff16a2e0b8f03e13dd29455c5c2a3d", 16);

        Random rand = new Random();
        int len = rand.nextInt(63) + 2;
        BigInteger res = new BigInteger(len, rand);
        //System.out.println("The random BigInteger = " + res);

        Curves E = new Curves(p,a,b);
        Curves.DotsGroup Base = E.new DotsGroup(new BigInteger("b70e0cbd6bb4bf7f321390b94a03c1d356c21122343280d6115c1d21", 16), new BigInteger("bd376388b5f723fb4c22dfe6cd4375a05a07476444d5819985007e34", 16), BigInteger.ONE);

        Base.IsPoint(Base);

        System.out.println("Lets generate our new point P = kBase");
        BigInteger k = BlumBlumaBits(res, n);
        System.out.println(" k = " + k.toString(16));
        DotsGroup P = Base.ScalarMultipliacationMontgomery(Base, k);
        P.Pointsout();
        P.ToAffine(P);
        DotsGroup kP = P.ScalarMultipliacationMontgomery(P, n);
        kP.Pointsout();
        kP.ToAffine(kP);
    }
}
