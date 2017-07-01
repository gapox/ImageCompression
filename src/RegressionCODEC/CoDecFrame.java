package RegressionCODEC;

import configs.Config;
import carriers.ImageData;
import compressorCore.engine.Compressor;
import decompressorCore.engine.Decompressor;
import shifters.ImageManipulator;
import general.IO.OutAsBin;
import general.IO.OutAsHistogram;
import general.IO.ReadBin;
import peripheral.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CoDecFrame {
    Config cfg = new Config();
    ImageManipulator iMan = new ImageManipulator();
    Logger LOG = new Logger();

    //COMPRESS

    public ImageData openAndCompressStdImg(String imgNam) throws IOException{
        double[][][] imgYCBCR=iMan.openFieldYcbCr(imgNam);
        return imageCompress(imgYCBCR);
    }

    public double imageCompressStdImg(String imgNam, String binNam) throws IOException{
        double[][][] imgYCBCR=iMan.openFieldYcbCr(imgNam);
        double ratio = imageCompressAndSave(imgYCBCR, binNam);
        LOG.logFunctional("The compression ratio for file"+imgNam+" is: "+ratio);
        return ratio;
    }

    public double imageCompressAndSave(double[][][] imgYCBCR, String binName) {

        ImageData data = imageCompress(imgYCBCR);
        if (cfg.SAVE_HISTS)
            histOut(data.infos);
        long diskSize = binarySave(data, binName);
        long orgSize = imgYCBCR.length*imgYCBCR[0].length*imgYCBCR[0][0].length;
        double ratio=((double)diskSize)/((double)orgSize);

        return ratio;
    }

    public ImageData imageCompress(double[][][] imgYCBCR) {
        Compressor com = new Compressor(cfg);
        ImageData data = com.start(imgYCBCR);
        return data;
    }

    private long binarySave(ImageData infos, String pathOut) {
        OutAsBin outAsBin = new OutAsBin(infos, pathOut);
        try {
            return outAsBin.transformAndWrite();
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return -1L;
        }
    }


    //DECOMPRESS
    public void imageOpenDecompressAndSave(String gpegIn, String pngOut)throws IOException{
        double[][][] imgYCBCR=imageOpenAndDecompress(gpegIn);
        if(imgYCBCR==null)
            throw new IOException("Tha data could not be successfully decompressed.");
        iMan.saveFieldYCbCr(imgYCBCR, pngOut);
    }

    public double[][][] imageOpenAndDecompress(String gpegIn) {
        ImageData iData = null;
        try {
            iData = binaryOpen(gpegIn);
            try {
                return imageDecompress(iData);
            } catch (Exception e) {
                LOG.logErr("The read file is corrupted! Cannot reproduce image.", e);
            }
        } catch (IOException e) {
            LOG.logErr(e.getMessage(), e);
        }
        return null;
    }

    public double[][][] imageDecompress(ImageData imageData) {

        Decompressor dec = new Decompressor(imageData);
        LOG.logFunctional("Starting decompression");
        return dec.start();
    }

    public ImageData binaryOpen(String path) throws IOException {
        ReadBin rb = new ReadBin(path);
        return rb.readAndTransform();
    }



    //OTHER
    private void histOut(ArrayList[][] infos) {
        OutAsHistogram outAsHist = new OutAsHistogram(cfg.LOCATION_OUT);
        outAsHist.outputThis("Hists/Ya", infos[0][0]);
        outAsHist.outputThis("Hists/Yb", infos[0][1]);
        outAsHist.outputThis("Hists/Yv", infos[0][2]);

        outAsHist.outputThis("Hists/CBa", infos[1][0]);
        outAsHist.outputThis("Hists/CBb", infos[1][1]);
        outAsHist.outputThis("Hists/CBv", infos[1][2]);

        outAsHist.outputThis("Hists/CRa", infos[2][0]);
        outAsHist.outputThis("Hists/CRb", infos[2][1]);
        outAsHist.outputThis("Hists/CRv", infos[2][2]);
    }

    public void calcDiff(String org, String rec, String outDiff)throws IOException{

        int[][] orgRGB=iMan.openFieldRGB(org);
        int[][] recRGB=iMan.openFieldRGB(rec);
        int[][] diff=iMan.calculateDifference(orgRGB, recRGB);

        iMan.saveFieldRGB(diff, outDiff);
    }

}
