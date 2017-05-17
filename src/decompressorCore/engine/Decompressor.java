package decompressorCore.engine;

import configs.Config;
import carriers.FieldRegressions;
import carriers.ImageData;
import carriers.PseudoInverses;
import configs.ConfigImg;
import decompressorCore.image.ReconstructRegression;
import shifters.ImageManipulator;
import peripheral.Logger;
import shifters.ColorSpaceShifter;

import java.util.ArrayList;

public class Decompressor {
    Config cfg;
    ConfigImg cimg;
    ImageManipulator iMan = new ImageManipulator();
    int sizeX, sizeY;
    byte[] data;
    ColorSpaceShifter css;

    int[][] rgb;
    double[][][] ycbcr, YCh, CbCh, CrCh;
    int cnt = 0, bcnt = 7;
    ArrayList<FieldRegressions> fields;
    FieldRegressions[] field;
    PseudoInverses pInv;
    //each channel has its own a, b and variances, and also the partitioning tree.
    ArrayList<Double> ya, yb, yv, ynormals, cba, cbb, cbv, cbnormals, cra, crb, crv, crnormals;
    ArrayList<Byte> yp, cbp, crp;
    Logger LOG = new Logger();


    Decompressor(byte[] bytes) {
        data = bytes;
        //do some magic(not yet implemented, to get channels and fields)
    }

    public Decompressor(ImageData iData) {
        this.sizeX = iData.x;
        this.sizeY = iData.y;
        this.cimg = iData.configImg;

        ya = iData.infos[0][0];
        yb = iData.infos[0][1];
        yv = iData.infos[0][2];
        yp = iData.infos[0][3];
        ynormals = iData.infos[0][4];
        cba = iData.infos[1][0];
        cbb = iData.infos[1][1];
        cbv = iData.infos[1][2];
        cbp = iData.infos[1][3];
        cbnormals = iData.infos[1][4];
        cra = iData.infos[2][0];
        crb = iData.infos[2][1];
        crv = iData.infos[2][2];
        crp = iData.infos[2][3];
        crnormals = iData.infos[2][4];
        pInv = new PseudoInverses(iData.x, iData.y, cfg);
        //pInv.generatePinverses();
    }

    public double[][][] start() {
        //torej zelim rekonstruirati tukaj! to pomeni da iz yp,cbp in crp sestavim drevesa. eno zapored druge.
        long start = Logger.logFunctional("recreating: Y channel");
        YCh = reconstructChannel(ya, yb, yv, yp, ynormals, sizeX, sizeY);
        if (cimg.isDownsampledChromaChannels()) {
            Logger.logFunctional("recreating: Cb channel");
            CbCh = reconstructChannel(cba, cbb, cbv, cbp, cbnormals, iMan.calcDownsampledSize(sizeX), iMan.calcDownsampledSize(sizeY));
            Logger.logFunctional("recreating: Cr channel");
            CrCh = reconstructChannel(cra, crb, crv, crp, crnormals, iMan.calcDownsampledSize(sizeX), iMan.calcDownsampledSize(sizeY));
            LOG.logFunctional("Upsampling chroma channels");
            CbCh[0] = iMan.upsampleField(CbCh[0], sizeX, sizeY);
            CrCh[0] = iMan.upsampleField(CrCh[0], sizeX, sizeY);
        } else {
            Logger.logFunctional("recreating: Cb channel");
            CbCh = reconstructChannel(cba, cbb, cbv, cbp, cbnormals, sizeX, sizeY);
            Logger.logFunctional("recreating: Cr channel");
            CrCh = reconstructChannel(cra, crb, crv, crp, crnormals, sizeX, sizeY);
        }

        LOG.logFunctional("Recombining  channels");
        ycbcr = iMan.combineChannels(YCh[0], CbCh[0], CrCh[0]);

        if (cfg.SAVE_STEPS)
            saveSteps();
        LOG.logTime("Decompression took:", start);
        return ycbcr;
    }

    public double[][][] reconstructChannel(ArrayList<Double> a, ArrayList<Double> b, ArrayList<Double> v, ArrayList<Byte> p, ArrayList<Double> normals, int X, int Y) {
        ReconstructRegression r = new ReconstructRegression(X, Y, a, b, v, normals, p, pInv, cimg);
        return r.reconstruct();
    }


    public boolean getPartitionBit() {
        byte b = 0;//data[cnt];
        b >>= bcnt--;
        b &= 1;
        if (bcnt == -1) {
            bcnt = 7;
            cnt++;
        }
        if (b == 0)
            return false;
        return true;
    }

    public void getFieldsColors() {
        while (bcnt != 7)
            getPartitionBit();
        field = (FieldRegressions[]) fields.toArray();
        // these are represented as simple bytes
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].rX.length; j++)
                field[i].rX[j][0] = getByte();
            for (int j = 0; j < field[i].rY.length; j++)
                field[i].rY[j][0] = getByte();
        } // these are represented as signedfloat8bit
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].rX.length; j++)
                field[i].rX[j][1] = getByte();
            for (int j = 0; j < field[i].rY.length; j++)
                field[i].rY[j][1] = getByte();
        } // these are represented as unsignedfloat8bit
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].rX.length; j++)
                field[i].rX[j][2] = getByte();
            for (int j = 0; j < field[i].rY.length; j++)
                field[i].rY[j][2] = getByte();
        }

    }

    public byte getByte() {
        return (byte) (255 & data[cnt++]);
    }

    private void saveSteps() {

        iMan.saveFieldY(YCh[0], cfg.LOCATION_OUT + "00Y/" + "1-Y-Combined");
        iMan.saveFieldCb(CbCh[0], cfg.LOCATION_OUT + "0Cb/" + "1-Cb-Combined");
        iMan.saveFieldCr(CrCh[0], cfg.LOCATION_OUT + "0Cr/" + "1-Cr-Combined");
        iMan.saveFieldBW(YCh[1], cfg.LOCATION_OUT + "00Y/" + "2-Y-aCuts");
        iMan.saveFieldBW(CbCh[1], cfg.LOCATION_OUT + "0Cb/" + "2-Cb-aCuts");
        iMan.saveFieldBW(CrCh[1], cfg.LOCATION_OUT + "0Cr/" + "2-Cr-aCuts");
    }
}
