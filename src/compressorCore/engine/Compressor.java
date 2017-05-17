package compressorCore.engine;

import configs.Config;
import carriers.ImageData;
import compressorCore.image.CompressAVG;
import compressorCore.image.CompressRegression;
import shifters.ImageManipulator;
import general.IO.ToImg;
import math.DCT;
import peripheral.Logger;
import shifters.ColorSpaceShifter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Compressor {
    static String _locOut = "C:/Users/Gasper/Desktop/Projekti/ImageCompression/RECONs/",
            _locIn = "C:/Users/Gasper/Desktop/Projekti/ImageCompression/ORGs/", _nam = "diag.png";
    ImageManipulator iMan = new ImageManipulator();
    public int x, y;
    ColorSpaceShifter css;
    Config cfg;
    Logger LOG = new Logger();
    int[][] rgb;
    double[][][] ycbcr;
    double[][] YCh, CbCh, CrCh;

    public Compressor(Config cfg) {
        this.cfg = cfg;
    }

    public ImageData start(double[][][] ycbcr) {
        long start=LOG.logFunctional("Starting compression");
        // Open image
        this.ycbcr = ycbcr;
        x = ycbcr[0].length;
        y = ycbcr[0][0].length;
        YCh=ycbcr[0];
        CbCh=ycbcr[1];
        CrCh=ycbcr[2];
        if (cfg.DOWN_SAMPLE_CHROMA_FIELDS) {
            CbCh = iMan.downsampleField(CbCh);
            CrCh = iMan.downsampleField(CrCh);
        }
        if (cfg.SAVE_STEPS)
            saveSteps();

        // Compress
        ArrayList[][] infos;
        infos = compressImageReg(YCh, CbCh, CrCh, 0);
        ImageData iData = new ImageData(infos, x, y, cfg.DOWN_SAMPLE_CHROMA_FIELDS, cfg.DOWN_SAMPLE_REGRESSIONS);
        LOG.logTime("Compression took", start);
        return iData;

    }

    public void videoCompress() throws IOException {
        /*OpenImg o = new OpenImg();
        int[][] a = o.getRGB(LOCATION_IN + FILE_IN_NAME + "156.jpg");
        for (int i = 157; i < 500; i++) {
            int[][] b = o.getRGB(LOCATION_IN + FILE_IN_NAME + i + ".jpg");
            int[][] diff = iMan.calculateDifference(a, b);
            int[][] ycbcr = ColorSpaceShifter.rgb2ycbcr(diff);
            int[][] YCh = new int[ycbcr.length][ycbcr[0].length], CbCh = new int[ycbcr.length][ycbcr[0].length],
                    CrCh = new int[ycbcr.length][ycbcr[0].length];
            iMan.splitColorChannels(ycbcr, YCh, CbCh, CrCh);
            ArrayList[][] infos = compressImageReg(YCh, CbCh, CrCh, i);
        }*/
    }

    public ArrayList[][] compressImageReg(double[][] YCh,double[][] CbCh,double[][] CrCh, int x) {
        LOG.logFunctional("Regressing Y channel");
        ArrayList[] Yarl = compressChReg(YCh, cfg.LUMA_SIMILARITY, true);
        LOG.logFunctional("Regressing Cb channel");
        ArrayList[] Cbarl = compressChReg(CbCh, cfg.CHROMA_SIMILARITY, true);
        LOG.logFunctional("Regressing Cr channel");
        ArrayList[] Crarl = compressChReg(CrCh, cfg.CHROMA_SIMILARITY, true);

        LOG.logFunctional("Combining regression data");
        ArrayList[][] ImageArl = new ArrayList[][]{Yarl, Cbarl, Crarl};

        return ImageArl;
    }

    public ArrayList[] compressChReg(double[][] ch, double lossyFactor, boolean writeSize) {

        CompressRegression com = new CompressRegression(ch, lossyFactor, cfg);
        return com.compress();
    }

    // methods for image compression AVERAGES

    public float[][] getDCTComponents(int[][] channel) {
        DCT dct = new DCT(channel);
        // float[][] c = dct.getDCTComponents();// no compression
        int[][] q = dct.getQuantizedDCT();
        // return c;
        return null;
    }


    private void compressImageAvg(int[][] YCh, int[][] CbCh, int[][] CrCh) throws IOException {
        /*
        // primary components
        iMan.saveFieldBW(YCh, LOCATION_OUT + "1-Y-FULL");
        iMan.saveFieldBW(CbCh, LOCATION_OUT + "2-Cb-FULL");
        iMan.saveFieldBW(CrCh, LOCATION_OUT + "3-Cr-FULL");

        YCh = compressChAvg(YCh, cfg.LUMA_SIMILARITY, true, true, "1-Y-R");
        CbCh = compressChAvg(CbCh, cfg.CHROMA_SIMILARITY, true, true, "2-Cb-R");
        CrCh = compressChAvg(CrCh, cfg.CHROMA_SIMILARITY, true, true, "3-Cr-R");

        CbCh[][] recycbcr = new int[YCh.length][YCh[0].length];
        for (int i = 0; i < YCh.length; i++) {
            for (int j = 0; j < YCh[0].length; j++) {
                recycbcr[i][j] += (YCh[i][j] << 16);
                recycbcr[i][j] += (CbCh[i][j] << 8);
                recycbcr[i][j] += (CrCh[i][j]);
            }
        }
        int[][] recRGB = ColorSpaceShifter.ycbcr2rgb(recycbcr);

        iMan.saveFieldRGB(recRGB, LOCATION_OUT + "4-RGB-RECONSTRUCTION");
        // to se ni vse! generiraj se energy field (prvi odvod vsakega kanala)
        // ki bo povedal ce je v kvadratu kaj pomembnega. ce je, potem naj se
        // kar avtomatsko slika razreze! energy field bo pomagal pri kvaliteti,
        // a bo povecal sliko in malo podaljsal postopek!
        // na koncu gres cez vse z huffmanom. slika z malo razlikami bo majhna
        // saj bodo enaki kvadratki pogosti. zadevo se da mnogo pohitriti z
        // threadanjem... a tudi upocasniti...
        //

        */
    }


    public int[][] compressChAvg(int[][] ch, double lossyFactor, boolean writeSize, boolean saveFile, String filename)
            throws FileNotFoundException, UnsupportedEncodingException {
        CompressAVG com = new CompressAVG(ch, lossyFactor, _locOut + filename, cfg.ADD_NOISE);
        com.compress(0, 0, 0);
        if (saveFile) {
            ToImg save = new ToImg(com.rec);
            save.outputBW(_locOut + filename);
        }
        ch = com.rec;// rec je celotna slika ; overlays prekrivanja
        if (writeSize)
            System.out.println("Velikost " + filename + " kanala:" + com.bytes);
        return ch;
    }

    public void saveSteps(){
        LOG.logFunctional("Preparing original channels");
        iMan.saveFieldYCbCr(ycbcr, cfg.LOCATION_OUT + "completePicture/" + "0-ORG");
        iMan.saveFieldY(YCh, cfg.LOCATION_OUT + "00Y/" + "1-Y-aFULL");
        iMan.saveFieldCb(CbCh, cfg.LOCATION_OUT + "0Cb/" + "2-Cb-aFULL");
        iMan.saveFieldCr(CrCh, cfg.LOCATION_OUT + "0Cr/" + "3-Cr-aFULL");
    }
}

