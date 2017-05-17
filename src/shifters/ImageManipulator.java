package shifters;

import carriers.ChannelInformation;
import general.IO.OpenImg;
import general.IO.ToImg;
import peripheral.Logger;
import shifters.ColorSpaceShifter;

import java.io.IOException;

public class ImageManipulator {
	Logger LOG = new Logger();
	public void saveFieldBW(int[][] arr, String fName) {
		ToImg save;
		save = new ToImg(arr);
		LOG.logWrite("Saving BLACK-WHITE image to:"+fName);
		save.outputBW(fName);
	}
	public void saveFieldBW(double[][] arr, String fName) {
		ToImg save;
		save = new ToImg(arr);
		LOG.logWrite("Saving BLACK-WHITE image to:"+fName);
		save.outputBW(fName);
	}

	public void saveFieldY(double[][] arr, String fName) {
		ToImg save;
		double[][][] x= new double[3][arr.length][arr[0].length];
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				x[0][i][j]=arr[i][j];
				x[1][i][j]=0.50001;
				x[2][i][j]=0.50001;
			}
		}
		saveFieldYCbCr(x, fName);
	}
	
	public void saveFieldCb(double[][] arr, String fName) {
		ToImg save;
		double[][][] x= new double[3][arr.length][arr[0].length];
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				x[0][i][j]=0.50001;
				x[1][i][j]=arr[i][j];
				x[2][i][j]=0.50001;
			}
		}
		saveFieldYCbCr(x, fName);
	}

	public void saveFieldCr(double[][] arr, String fName) {
		ToImg save;
		double[][][] x= new double[3][arr.length][arr[0].length];
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				x[0][i][j]=0.50001;
				x[1][i][j]=0.50001;
				x[2][i][j]=arr[i][j];
			}
		}
		saveFieldYCbCr(x,fName);
	}
	
	public void saveFieldRGB(int[][] arr, String fName) {
		ToImg save;
		save = new ToImg(arr);
		LOG.logWrite("Saving RGB image to:"+fName);
		save.outputRGB(fName);

	}
	
	public void saveFieldYCbCr(double[][][] arr, String fName) {
		ToImg save;
		save = new ToImg(ColorSpaceShifter.ycbcr2rgb(arr));
		LOG.logWrite("Saving RGB image to:"+fName);
		save.outputRGB(fName);

	}

	public double[][][] openFieldYcbCr( String fName) throws IOException{

		OpenImg open = new OpenImg();
		double[][][] ycbcr = open.getYCbCr(fName);
		return ycbcr;

	}

	public int[][] openFieldRGB( String fName) throws IOException{

		OpenImg open = new OpenImg();
		int[][] ycbcr = open.getRGB(fName);
		return ycbcr;

	}

	public void splitColorChannels(int[][] ycbcr, int[][] YCh, int[][] CbCh, int[][] CrCh) {
		for (int i = 0; i < ycbcr.length; i++) {
			for (int j = 0; j < ycbcr[0].length; j++) {
				YCh[i][j] = ycbcr[i][j] >> 16;
				CbCh[i][j] = (ycbcr[i][j] >> 8) & 255;
				CrCh[i][j] = (ycbcr[i][j]) & 255;
			}
		}
	}

	public int calcDownsampledSize(int x){
		return x-x/2;
	}

	public int[][] downsampleField(int[][] a){
		int[][] ret = new int[calcDownsampledSize(a.length)][calcDownsampledSize(a[0].length)];
		for(int i=1;i<a.length;i+=2){
			for(int j=1;j<a[0].length;j+=2){
				ret[i/2][j/2]=(a[i-1][j-1]+a[i-1][j]+a[i][j-1]+a[i][j])/4;
			}
		}
		if(a.length%2==1){
			for(int i=1;i<a[0].length;i+=2){
				ret[ret.length-1][i/2]=(a[a.length-1][i-1]+a[a.length-1][i])/2;
			}
		}
		if(a[0].length%2==1){
			for(int i=1;i<a.length;i+=2){
				ret[i/2][ret[0].length-1]=(a[i-1][a[0].length-1]+a[i][a[0].length-1])/2;
			}
		}
		if(a[0].length%2==1 && a.length%2==1)
			ret[ret.length-1][ret[0].length-1]=a[a.length-1][a[0].length-1];
		return ret;
	}

	public int[][] upsampleField(int[][] ds, int orgX, int orgY){
		int[][] ret = new int[orgX][orgY];
		double factX=(double)ds.length/(double)orgX;
		double factY=(double)ds[0].length/(double)orgY;
		for(int i=0;i<orgX;i++){
			for(int j=0;j<orgY;j++){
				ret[i][j]=ds[(int)(factX*i)][(int)(factY*j)];
			}
		}

		return ret;
	}

	public double[][] downsampleField(double[][] a){
		double[][] ret = new double[calcDownsampledSize(a.length)][calcDownsampledSize(a[0].length)];
		for(int i=1;i<a.length;i+=2){
			for(int j=1;j<a[0].length;j+=2){
				ret[i/2][j/2]=(a[i-1][j-1]+a[i-1][j]+a[i][j-1]+a[i][j])/4;
			}
		}
		if(a.length%2==1){
			for(int i=1;i<a[0].length;i+=2){
				ret[ret.length-1][i/2]=(a[a.length-1][i-1]+a[a.length-1][i])/2;
			}
		}
		if(a[0].length%2==1){
			for(int i=1;i<a.length;i+=2){
				ret[i/2][ret[0].length-1]=(a[i-1][a[0].length-1]+a[i][a[0].length-1])/2;
			}
		}
		if(a[0].length%2==1 && a.length%2==1)
			ret[ret.length-1][ret[0].length-1]=a[a.length-1][a[0].length-1];
		return ret;
	}

	public double[][] upsampleField(double[][] ds, int orgX, int orgY){
		double[][] ret = new double[orgX][orgY];
		double factX=(double)ds.length/(double)orgX;
		double factY=(double)ds[0].length/(double)orgY;
		for(int i=0;i<orgX;i++){
			for(int j=0;j<orgY;j++){
				ret[i][j]=ds[(int)(factX*i)][(int)(factY*j)];
			}
		}

		return ret;
	}

	public int[][] calculateDifference(int[][] a, int[][] b) {
		int[][] diff = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				int ra = (a[i][j] >> 0) & 255;
				int rb = (b[i][j] >> 0) & 255;
				int ga = (a[i][j] >> 8) & 255;
				int gb = (b[i][j] >> 8) & 255;
				int ba = (a[i][j] >> 16) & 255;
				int bb = (b[i][j] >> 16) & 255;
				ra -= rb;
				if (ra > 127)
					ra = 127;
				if (ra < -128)
					ra = -128;
				ra += 128;
				ra &= 255;
				ga -= gb;
				if (ga > 127)
					ga = 127;
				if (ga < -128)
					ga = -128;
				ga += 128;
				ga &= 255;
				ba -= bb;
				if (ba > 127)
					ba = 127;
				if (ba < -128)
					ba = -128;
				ba += 128;
				ba &= 255;
				diff[i][j] = (ba << 16) + (ga << 8) + ra;
			}
		}

		return diff;
	}

	public void addDifference(int[][] a, int[][] b) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				int ra = (a[i][j] >> 0) & 255;
				int rb = (b[i][j] >> 0) & 255;
				int ga = (a[i][j] >> 8) & 255;
				int gb = (b[i][j] >> 8) & 255;
				int ba = (a[i][j] >> 16) & 255;
				int bb = (b[i][j] >> 16) & 255;
				rb -= 128;
				ra -= rb;
				if (ra > 255)
					ra = 255;
				if (ra < 0)
					ra = 0;
				ra &= 255;
				gb -= 128;
				ga -= gb;
				if (ga > 255)
					ga = 255;
				if (ga < 0)
					ga = 0;
				ga &= 255;
				bb -= 128;
				ba -= bb;
				if (ba > 255)
					ba = 255;
				if (ba < 0)
					ba = 0;
				ba &= 255;
				a[i][j] = (ba << 16) + (ga << 8) + ra;
			}
		}
	}

	public boolean equal(int[][] a, int[][] b){
		boolean ret = true;
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[i].length;j++){
				int aColor=a[i][j];
				int bColor=b[i][j];

				if(aColor!=bColor){
					Logger.warnMath("The fields are not equal; color a: #"+Integer.toHexString(aColor)+"; color b: #"+Integer.toHexString(bColor));
					ret = false;
				}
			}
		}

		return ret;
	}

	public void out(int[][] a, String name) {
		System.out.println("Tabela " + name);
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				System.out.print("	" + a[i][j]);
			}
			System.out.println();
		}
	}

	public int[][] getReconstruction(ChannelInformation[] infos, int type) {
		int[][] rec = new int[infos[0].sum.length][infos[0].sum[0].length];
		float[][] t0, t1, t2;
		if (type == 0) {
			t0 = infos[0].rec;
			t1 = infos[1].rec;
			t2 = infos[2].rec;
		} else if (type == 1) {
			t0 = infos[0].diff;
			t1 = infos[1].diff;
			t2 = infos[2].diff;
		} else if (type == 2) {
			t0 = infos[0].mul;
			t1 = infos[1].mul;
			t2 = infos[2].mul;
		} else {
			t0 = infos[0].sum;
			t1 = infos[1].sum;
			t2 = infos[2].sum;
		}
		for (int i = 0; i < rec.length; i++) {
			for (int j = 0; j < rec[0].length; j++) {
				int color = ((int) (t0[i][j] * 255) << 16);
				color += ((int) (t1[i][j] * 255) << 8);
				color += ((int) (t2[i][j] * 255) << 0);
				rec[i][j] = color;
			}
		}

		return rec;
	}

	public double[][][] combineChannels(double[][] a, double[][]b, double[][]c){
		double[][][] rec = new double[][][]{a,b,c};
		return rec;
	}

}
