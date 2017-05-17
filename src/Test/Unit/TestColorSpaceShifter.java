package Test.Unit;
import RegressionCODEC.CoDecFrame;
import configs.Config;
import shifters.ImageManipulator;
import org.junit.*;
import peripheral.Logger;
import shifters.ColorSpaceShifter;

import java.io.IOException;

/**
 * Created by Gasper on 17.4.2017.
 */
public class TestColorSpaceShifter {

    Config cfg = new Config();

    @Test
    public void testRgb2YcbCr() throws IOException {
        ImageManipulator iMan = new ImageManipulator();
        //int[][] img = iMan.openFieldRGB(Config.LOCATION_OUT+"cssMappings/AllColors.png");
        int[][] img = iMan.openFieldRGB(cfg.LOCATION_IN + cfg.FILE_IN_NAME);
        int[][] img2 = passThrough(img);
        int[][] diff = new int[img.length][img[0].length];
        //iMan.saveFieldRGB(img2, Config.LOCATION_OUT+"cssMappings/PassedThrough");
        int different=0;
        int[] diffs = new int[256];
        for(int i=0;i<img.length;i++){
            for(int j=0;j<img[0].length;j++){
                diff[i][j]=img[i][j]-img2[i][j];
                if(diff[i][j]!=0) {
                    different++;
                    diffs[Math.abs(((diff[i][j] & (255))))]++;
                    diffs[Math.abs((diff[i][j] & (255 << 8)) >> 8)]++;
                    diffs[Math.abs((diff[i][j] & (255 << 16)) >> 16)]++;
                    diff[i][j] = 256 * 256 * 256 - 1;
                }
                else
                    diff[i][j] = 0;

            }
        }
        System.out.println("sizes of difference (0-255):");
        for(int i=0;i<256;i++){
            if(diffs[i]!=0)
                System.out.println(i+" "+diffs[i]);
        }

        Logger.logTest("percentage of different pixels:"+((double)different/(double)(4096*4096)));
        //iMan.saveFieldRGB(diff, cfg.LOCATION_OUT +"cssMappings/diff");
        Assert.assertTrue("barve niso iste", passThroughAndCheckSimmilarity(img));
    }

    @Test
    public void testLoopThrough(){
        int[][] img = new int[][]{{128, 128<<8, 128<<16}};
        int n=15;
        for(int i=0;i<n;i++){
            int[][] img2=passThrough(img);
            img=img2;
        }
    }

    @Test
    public void testLoopThroughImg(){
        int n=30;
        try{
            long startAll=System.currentTimeMillis();
            ImageManipulator iMan = new ImageManipulator();
            double[][][] imgYCBCR=iMan.openFieldYcbCr(cfg.LOCATION_IN + cfg.FILE_IN_NAME);
            CoDecFrame codec = new CoDecFrame();

            for(int i=0;i<n;i++){
                codec.imageCompressAndSave(imgYCBCR,  cfg.LOCATION_OUT+cfg.BINARY_NAME);
                double[][][] ycbcr = codec.imageOpenAndDecompress(cfg.LOCATION_IN +cfg.BINARY_NAME);
                iMan.saveFieldYCbCr(ycbcr, cfg.LOCATION_OUT + "completePicture/" + "1-RGB-RECONSTRUCTION");
                imgYCBCR=iMan.openFieldYcbCr(cfg.LOCATION_OUT + cfg.RECONSTRUCTION_NAME);

            }
            Logger.logTime("Looping of "+n+" items took", System.currentTimeMillis()-startAll);
        }
        catch (IOException e){
            Logger.logErr(e.getMessage(), e);
        }
    }

    public boolean passThroughAndCheckSimmilarity(int[][] img){

        ImageManipulator imageManipulator = new ImageManipulator();

        return imageManipulator.equal(img, passThrough(img));

    }

    public int[][] passThrough(int[][] img){
        int[][] r= new int[img.length][img[0].length],
                g= new int[img.length][img[0].length],
                b= new int[img.length][img[0].length],
                br= new int[img.length][img[0].length],
                bg= new int[img.length][img[0].length],
                bb= new int[img.length][img[0].length];
        ImageManipulator imageManipulator = new ImageManipulator();
        imageManipulator.splitColorChannels(img, r,g,b);

        double[][][] midx = ColorSpaceShifter.rgb2ycbcr(img);
        //imageManipulator.splitColorChannels(img, y, cb, cr);
        int[][] mid = ColorSpaceShifter.ycbcr2rgb(midx);
        return mid;

    }

}
