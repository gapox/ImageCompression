package general.IO;

import carriers.ImageData;
import carriers.ImageDataWriteObject;
import peripheral.Logger;
import shifters.Float8Bit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class ReadBin {
    String _pathIn;
    Float8Bit f8b;
    double[] tfTablef8bS;//signed tf table
    double[] tfTablef8bN;//normalized tf table - 0 to 1;
    Logger LOG = new Logger();
    public ReadBin(String pathIn) {
        // TODO Auto-generated constructor stub
        this._pathIn = pathIn;
        f8b = new Float8Bit();
        tfTablef8bS = new double[256];
        tfTablef8bN = new double[256];
        for (int i = 0; i < 256; i++) {
            byte x = (byte) (255 & i);
            tfTablef8bS[i] = f8b.getDoubleFromSigned8bitFloat(x);
            tfTablef8bN[i] = f8b.getDoubleFromNormalized8bitFloat(x);
        }
    }

    public ImageData readAndTransform() throws IOException{

        ImageDataWriteObject imgData = read();
        if (imgData == null) {
            throw new IOException("The file provided is corrupted!");
        }
        ArrayList[][] als;
        als = new ArrayList[3][];
        als[0] = transformChannel(imgData.dataCh0);
        als[1] = transformChannel(imgData.dataCh1);
        als[2] = transformChannel(imgData.dataCh2);
        ImageData iData = new ImageData(als, imgData.sizeX, imgData.sizeY, imgData.downsampledChromaChannel, imgData.downsampledRegressions);
        return iData;

    }

    @Deprecated
    private int[] getMetadata(byte[][] byteArray) {
            int[] ret = new int[2];
            byte[] nums=byteArray[0];
            ret[0]= ((255&nums[0])<<24) |
                ((255&nums[1])<<16) |
                ((255&nums[2])<<8) |
                ((255&nums[3]));
        ret[1]= ((255&nums[4])<<24) |
                ((255&nums[5])<<16) |
                ((255&nums[6])<<8) |
                ((255&nums[7]));
        return ret;
    }

    private ArrayList[] transformChannel(byte[][] byteArray) {
        ArrayList[] ch = new ArrayList[5];
        ch[0] = new ArrayList<Double>();
        int[] vals = getUnsignedValues(byteArray[0]);
        for (int i = 0; i < vals.length; i++)
            ch[0].add(tfTablef8bS[vals[i]]);

        ch[1] = new ArrayList<Double>();
        vals = getUnsignedValues(byteArray[1]);
        for (int i = 0; i < vals.length; i++)
            ch[1].add(tfTablef8bS[vals[i]]);


        ch[2] = new ArrayList<Double>();
        vals = getUnsignedValues(byteArray[2]);
        for (int i = 0; i < vals.length; i++)
            ch[2].add(tfTablef8bS[vals[i]]);


        ch[3] = new ArrayList<Byte>();
        for (int i = 0; i < byteArray[3].length; i++)
            ch[3].add(byteArray[3][i]);


        ch[4] = new ArrayList<Double>();
        vals = getUnsignedValues(byteArray[4]);
        for (int i = 0; i < vals.length; i++)
            ch[4].add(tfTablef8bN[vals[i]]);

        return ch;
    }

    private int[] getUnsignedValues(byte[] x) {
        int[] ret = new int[x.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (int) (255 & x[i]);
        }
        return ret;
    }

    private ImageDataWriteObject read() {
        ImageDataWriteObject imgData = null;
        try {
            FileInputStream fis = new FileInputStream(_pathIn);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gis);
            imgData = (ImageDataWriteObject) ois.readObject();
            LOG.logRead("Succesfully read the object!");
            ois.close();
            gis.close();
            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgData;
    }
}










