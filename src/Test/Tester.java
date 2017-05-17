package Test;

import carriers.PseudoInverses;
import configs.Config;
import general.IO.FileSystemManagement;
import shifters.Float8Bit;

import java.util.List;

public class Tester {
    int x, y, maxPinvSize, maxTrInvCells;
    String outLoc;
    boolean useTransposeAsInverse;

    public Tester() {

    }

    public Tester(int x, int y, int maxPinvSize, int maxTrInvCells, String fileOutLocation, boolean useTransposeAsInverse) {
        this.x = x;
        this.y = y;
        this.maxPinvSize = maxPinvSize;
        this.maxTrInvCells = maxTrInvCells;
        this.useTransposeAsInverse = useTransposeAsInverse;
        outLoc = fileOutLocation + "Objects/";
    }



    public double testSignedFloatTransformation(double val) {
        Float8Bit feb = new Float8Bit();
        byte b = feb.getSigned8bitFloat(val);
        return feb.getDoubleFromSigned8bitFloat(b);

    }

    public void testValueSet() {
        Float8Bit feb = new Float8Bit();
        /*for(int i=0;i<256;i++){
			System.out.println(i+" "+feb.getDoubleFromNormalized8bitFloat((byte)i));
		}
		System.out.println("\n\n");*/

        for (int i = 0; i < 256; i++) {
            System.out.println(i + " " + feb.getDoubleFromSigned8bitFloat((byte) i));
        }

        System.out.println("\n\n");
        for (int i = 0; i < 1000; i++) {
            System.out.println((feb.getSigned8bitFloat(-1.0 + ((double) i / 500.0)) & 255) + " " + (-1.0 + ((double) i / 500.0)));
        }
    }



}
