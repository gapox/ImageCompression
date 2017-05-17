package shifters;

import peripheral.Logger;

public class Float8Bit {
    double[] signedTransformations;
    Logger LOG = new Logger();
    public Float8Bit() {
        signedTransformations = new double[256];
        for (int i = 0; i < 256; i++) {
            signedTransformations[i] = getDoubleFromSigned8bitFloat((byte) i);
            //Logger.logMath(i+" "+signedTransformations[i]+"\n"+i+"   "+unsignedTransformations[i]);
        }
    }

    @Deprecated
    public byte getSigned8bitFloatCalc(double a) {
        long x = Double.doubleToLongBits(a);
        boolean negative = (x & 0x8000000000000000L) != 0;
        long exp = x & 0x7ff0000000000000L;
        long mantissa = x & 0x000fffffffffffffL;
        exp >>= 52;
        mantissa >>= 48;
        byte B = 0;

        exp -= 1023;
        if (exp > 3) exp = 7;
        else if (exp < -4) exp = 0;
        else exp += 4;
        exp <<= 4;


        if (negative) {
            B += (1 << 7);
        }
        B += exp;
        B += mantissa;
        return B;
    }

    public byte getSigned8bitFloat(double a) {
        double aPositive = a >= 0 ? a : -a;
        int add = 32;
        int i = 64;
        if(aPositive>1.0){
            LOG.warnMath("Float8Bit transformation num out of bounds:"+a);
            if(a<-1.0)return (byte)255;
            return (byte)127;
        }
        while (!(signedTransformations[i - 1] <= aPositive && signedTransformations[i] >= aPositive)) {
            if (signedTransformations[i] < aPositive) i += add;
            else i -= add;
            add /= 2;
        }
        if (-signedTransformations[i] + aPositive < signedTransformations[i - 1] - aPositive)
            i-=1;
        if (a < 0)//0->127
            i += 128;
        return (byte) ((i) & 255);
    }


    public byte getByteFromNormalized(double a) {
        return (byte) ((int) (a * 255.0));
    }

    public double getDoubleFromNormalized8bitFloat(byte a) {
        int x = 255 & a;
        return ((double) x / 255.0);
    }

    public double getDoubleFromSigned8bitFloat(byte a) {
        if (a == 0) return 0;
        long D = 0;
        long sig = a & 0x80;
        long exp = a & 0x70;
        long man = a & 0x0f;
        sig >>= 7;
        sig <<= 63;
        D += sig;


        exp >>= 4;
        exp -= 4;
        exp += 1023;

        exp <<= 52;
        D += exp;
        man <<= 48;
        D += man;
        return Double.longBitsToDouble(D) / 15.5;
    }

    private int exp(int n) {
        int ret = 1;
        while (n-- > 0) ret *= 2;
        return ret;
    }
}
