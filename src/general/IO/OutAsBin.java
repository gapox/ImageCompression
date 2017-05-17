package general.IO;

import configs.Config;
import carriers.ImageData;
import carriers.ImageDataWriteObject;
import peripheral.Logger;
import shifters.Float8Bit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class OutAsBin {
    ImageData infos;
    String _pathOut;
    Float8Bit f8b;
    Logger LOG = new Logger();
    Config cfg = new Config();
    public OutAsBin(ImageData infos, String pathOut) {
        // TODO Auto-generated constructor stub
        this.infos = infos;
        this._pathOut= pathOut;
        f8b = new Float8Bit();
    }

    public byte[] getFloat8bitfromDoubleArr(double[] a) {
        byte[] ret = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = f8b.getSigned8bitFloat(a[i]);
        }
        return ret;
    }

    public byte[] getFactor8bitfromDoubleArr(double[] a) {
        byte[] ret = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = f8b.getByteFromNormalized(a[i]);
        }
        return ret;
    }

    public byte[][] getFloat8bitForChannel(ArrayList[] a) {
        byte[][] ret = new byte[5][];
        double[] x = new double[a[0].size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = (double) a[0].get(i);
        }
        ret[0] = getFloat8bitfromDoubleArr(x);

        x = new double[a[1].size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = (double) a[1].get(i);
        }
        ret[1] = getFloat8bitfromDoubleArr(x);

        x = new double[a[2].size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = (double) a[2].get(i);
        }
        ret[2] = getFloat8bitfromDoubleArr(x);

        ret[3] = new byte[a[3].size()];
        for (int i = 0; i < ret[3].length; i++) {
            ret[3][i] = (byte) a[3].get(i);
        }

        x = new double[a[4].size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = (double) a[4].get(i);
        }
        ret[4] = getFactor8bitfromDoubleArr(x);
        return ret;
    }

    public byte[][] transformMetadata(ImageData iData) {
        byte[] ret = new byte[8];
        int x = iData.x;
        ret[0] = (byte) ((x >> 24) & 255);
        ret[1] = (byte) ((x >> 16) & 255);
        ret[2] = (byte) ((x >> 8) & 255);
        ret[3] = (byte) ((x >> 0) & 255);
        int y = iData.y;
        ret[4] = (byte) ((y >> 24) & 255);
        ret[5] = (byte) ((y >> 16) & 255);
        ret[6] = (byte) ((y >> 8) & 255);
        ret[7] = (byte) ((y >> 0) & 255);

        return new byte[][]{ret};
    }

    private ImageDataWriteObject transform(){
        ImageDataWriteObject imgData = new ImageDataWriteObject();
        imgData.dataCh0=getFloat8bitForChannel(infos.infos[0]);
        imgData.dataCh1=getFloat8bitForChannel(infos.infos[1]);
        imgData.dataCh2=getFloat8bitForChannel(infos.infos[2]);
        imgData.chromaSim=cfg.CHROMA_SIMILARITY;
        imgData.lumaSim=cfg.LUMA_SIMILARITY;
        imgData.downsampledChromaChannel=cfg.DOWN_SAMPLE_CHROMA_FIELDS;
        imgData.downsampledRegressions=cfg.DOWN_SAMPLE_REGRESSIONS;
        imgData.dateCreated= LocalDateTime.now().toString();
        imgData.sizeX=infos.x;
        imgData.sizeY=infos.y;
        return imgData;
    }
    public long transformAndWrite() throws IOException {
        LOG.logWrite("Saving compressed file.");
        ImageDataWriteObject imgData=transform();

        if(cfg.PRINT_STATS){
            //TO-DO add stat print
        }
        long size = write(imgData);
        LOG.logWrite("Compressed file saved");
        LOG.logSize(size);
        return size;
    }

    public long write(Object data) throws IOException{
        FileOutputStream fileOut = new FileOutputStream(_pathOut);
        GZIPOutputStream gzOut = new GZIPOutputStream(fileOut);
        ObjectOutputStream binOut = new ObjectOutputStream(gzOut);
        binOut.writeObject(data);
        binOut.close();
        gzOut.close();
        fileOut.close();
        File f = new File(_pathOut);
        return f.length();
    }


}
