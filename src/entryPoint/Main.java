package entryPoint;

import RegressionCODEC.CoDecFrame;
import Test.Tester;
import configs.Config;
import general.IO.FileSystemManagement;
import general.IO.ReadBin;
import peripheral.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	static Config cfg= new Config();
	static Logger LOG = new Logger();


	public static void main(String[] args) throws IOException {
		//ConfigIO.FILE_IN_NAME="slope.png";
		//Config.LUMA_SIMILARITY=0.12;
		setCfg(args);


		String doWhat = "cycle";
		switch (doWhat) {
			case "cycle": {
				imageCompressAndDecompress();
				break;
			}

			case "compress": {
				imageCompress();
				break;
			}
			case "decompress": {
				imageDecompress();
				break;
			}
			case "video": {
				videoCompress();
				break;
			}
			case "realData": {
				compressMultipleImages();
				break;
			}
			case "binTest": {
				binTest();
				break;
			}
			case "test": {
				test();
				break;
			}
		}

	}

	private static void compressMultipleImages(){
		List<String> files= FileSystemManagement.getEntriesInDirectory(cfg.LOCATION_IN_PACK);
		long startAll=LOG.logFunctional("Sarting compression of whole pack of images. number of images:"+files.size());
		int i=0;
		double ratio=0;
		for(String file : files){
			LOG.logFunctional("Starting file "+(++i));
			String[] name=file.split("\\.");
			try{
				CoDecFrame codec = new CoDecFrame();
				ratio += codec.imageCompressStdImg(cfg.LOCATION_IN_PACK +file, cfg.LOCATION_OUT_PACK+name[0]+".gpeg");
				LOG.logTime("Everything together for the image took:", startAll);
			}
			catch (IOException e){
				LOG.logErr(e.getMessage(), e);
			}
		}
		ratio/=(double)i;
		LOG.logFunctional("The average ratio of compression at simmilarity="+cfg.LUMA_SIMILARITY+" for the collection of images provided is:"+ratio);
	}
	
	private static void imageCompressAndDecompress() {
		try{
			long startAll=LOG.logFunctional("Sarting pass through");
			CoDecFrame codec = new CoDecFrame();
			codec.imageCompressStdImg(cfg.LOCATION_IN +cfg.FILE_IN_NAME, cfg.LOCATION_OUT+cfg.BINARY_NAME);
			codec.imageOpenDecompressAndSave(cfg.LOCATION_OUT +cfg.BINARY_NAME, cfg.LOCATION_OUT + "completePicture/1-RGB-RECONSTRUCTION");
			if(Config.SAVE_DIFF)
				codec.calcDiff(cfg.LOCATION_IN +cfg.FILE_IN_NAME, cfg.LOCATION_OUT + "completePicture/1-RGB-RECONSTRUCTION.png", cfg.LOCATION_OUT + "completePicture/1-RGB DIFF");
			LOG.logTime("Everything together for the image took:", startAll);
		}
		catch (IOException e){
			LOG.logErr(e.getMessage(), e);
		}
	}

	private static void imageCompress(){
		try{
			CoDecFrame codec = new CoDecFrame();
			codec.imageCompressStdImg(cfg.LOCATION_IN +cfg.FILE_IN_NAME, cfg.LOCATION_OUT+cfg.BINARY_NAME);
		}
		catch (IOException e){
			LOG.logErr(e.getMessage(), e);
		}
	}

	private static void imageDecompress(){
		try{
			CoDecFrame codec = new CoDecFrame();
			codec.imageOpenDecompressAndSave(cfg.LOCATION_OUT +cfg.BINARY_NAME, cfg.LOCATION_OUT + "completePicture/1-RGB-RECONSTRUCTION");
		}
		catch (IOException e){
			LOG.logErr(e.getMessage(), e);
		}
	}

	private static void videoCompress() {
		// com.videoCompress();

		// double[][] x= la.pseudoInv();
		// tests with averages // compressImageAvg(YCh,CbCh, CrCh);
		// WORKING COMPRESSION // getDCTComponents(YCh);

	}
	
	private static void binTest(){
		ReadBin rb = new ReadBin(cfg.LOCATION_OUT + cfg.BINARY_NAME);
		try{
			rb.readAndTransform();
		}
		catch (IOException e){
			LOG.logErr(e.getMessage(), e);
		}
	}

	private static void sizesPrint(ArrayList[][] infos) {
		System.out.println("Sizes:");
		System.out.println("	Y channel:");
		System.out.println("		a:" + infos[0][0].size());
		System.out.println("		b:" + infos[0][1].size());
		System.out.println("		v:" + infos[0][2].size());
		System.out.println("	Cb channel:");
		System.out.println("		a:" + infos[1][0].size());
		System.out.println("		b:" + infos[1][1].size());
		System.out.println("		v:" + infos[1][2].size());
		System.out.println("	Cr channel:");
		System.out.println("		a:" + infos[2][0].size());
		System.out.println("		b:" + infos[2][1].size());
		System.out.println("		v:" + infos[2][2].size());
	}

	private static void test() throws IOException {
		Tester test = new Tester(64, 64, cfg.MAX_PINV_CELLS, cfg.MAX_TR_INV_CELLS, cfg.LOCATION_OUT, cfg.USE_TRANSPOSE_AS_INVERSE);
		//test.testPinverses();
		// Tester test = new Tester();
		/*test.testValueSet();
		CoDecFrame coDecFrame= new CoDecFrame(cfg);
		ImageData i1d=coDecFrame.imageCompress();
		ImageData i2d=coDecFrame.binaryOpen();
		double a= -0.1647727272727273;
		Float8Bit f = new Float8Bit();
		byte x= f.getSigned8bitFloat(a);
		double aMock = f.getDoubleFromSigned8bitFloat(x);*/

	}

	public static void setCfg(String[] args){
		LOG.warnFunctional("Setting a configuration is not yet implemented.");
	}

}







