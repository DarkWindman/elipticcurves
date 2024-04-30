import java.math.BigInteger;
import java.util.Random;

public class CurveP224 {
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
        BigInteger k = E.BlumBlumaBits(res, n);
        System.out.println(" k = " + k.toString(16));
        Curves.DotsGroup P = Base.ScalarMultipliacationMontgomery(Base, k);
        P.Pointsout();
        P.ToAffine(P);
        P.IsPoint(P);
        Curves.DotsGroup kP = P.ScalarMultipliacationMontgomery(P, n);
        kP.Pointsout();
        kP.ToAffine(kP);
    }

}
