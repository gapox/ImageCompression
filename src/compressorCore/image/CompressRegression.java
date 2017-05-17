package compressorCore.image;

import configs.Config;
import math.Regression;
import math.Subsectors;
import peripheral.Logger;
import shifters.Sampler;

import java.util.ArrayList;

public class CompressRegression {
	Regression r;
	public double[][] img;

	public double simmilarity = 1;// 0-255; 0=loseless compression 255=averaged
	// picture
	public String fileName;
	public int cnt = 0;
	public int dataByte = 0;
	public int bytes = 0;
	public int sectors = 0;
	public int minSize= 3;
	public ArrayList<Byte> partitions;
	public ArrayList<Double>  dataa, datab, datav;
	public ArrayList<Double> normals;
	private Sampler sampler;
	public boolean addRandom = false;
	Subsectors sub;
	Config cfg;
	int sizeX,sizeY;
	Logger LOG = new Logger();
	public CompressRegression(final double[][] image) {
		r = new Regression();
		sub = new Subsectors();
		img = new double[image.length][image[0].length];
		for(int i=0;i<image.length;i++){
			for(int j=0;j<image[0].length;j++){
				img[i][j]=image[i][j];
			}
		}
		partitions = new ArrayList<Byte>();
		dataa = new ArrayList<Double>();
		datab = new ArrayList<Double>();
		datav = new ArrayList<Double>();
		normals = new ArrayList<Double>();
		sampler= new Sampler();
		sizeX=image.length;
		sizeY=image[0].length;
		this.cfg=cfg;
	}

	public CompressRegression(double[][] image, double lossyness, Config cfg){
		this(image);
		this.cfg=cfg;
		simmilarity =  lossyness;
	}

	
	
	public ArrayList[] compress() {
		Regression r = new Regression();
		sectors++;
		double[][] rX;
		double[][] rY;
		int xs = 0;
		int xe = sizeX;
		int ys = 0;
		int ye = sizeY;
		rX = new double[sizeX][3];
		rY = new double[sizeY][3];
		for (int i = xs; i < xe; i++) {
			double[] col = new double[sizeY];
			for (int j = ys; j < ye; j++)
				col[j - ys] = img[i][j];
			r.setField(col);
			rX[i - xs] = r.getRegression();
		}
		for (int i = ys; i < ye; i++) {
			double[] col = new double[sizeX];
			for (int j = xs; j < xe; j++)
				col[j - xs] = img[j][i];
			r.setField(col);
			rY[i - ys] = r.getRegression();
		}
		compress(new int[]{0,sizeX,0,sizeY}, rX, rY);
		while(cnt>0)addBit(0);
		LOG.logFunctional("sectors on channel: "+sectors);
		return new ArrayList[]{dataa,datab,datav, partitions, normals};
	}

	public void compress(int[] dims, double[][] rX, double[][] rY) {
		int xs=dims[0];
		int xe=dims[1];
		int ys=dims[2];
		int ye=dims[3];
		if (xe - xs <= cfg.MINIMAL_SECTOR_SIZE || ye - ys <= cfg.MINIMAL_SECTOR_SIZE) {
			addBit(0);
			double normal=normalise(xs,ys,xe,ye, rX, rY);
			normals.add(normal);
			storeData(rX,rY);
			return;
		}
		int[][] subDims=sub.getSubsectors(dims);
		
		double[][][] upLeftRegs = getRegressions(subDims[1]);
		double[][][] upRightRegs = getRegressions(subDims[0]);
		double[][][] downLeftRegs = getRegressions(subDims[2]);
		double[][][] downRightRegs = getRegressions(subDims[3]);

		if (areSimmilar(rX, rY, upLeftRegs, 0)
				&& areSimmilar(rX, rY, upRightRegs, 1)
				&& areSimmilar(rX, rY, downLeftRegs, 2)
				&& areSimmilar(rX, rY, downRightRegs, 3)) {
			addBit(0);
			double normal=normalise(xs,ys,xe,ye, rX, rY);
			normals.add(normal);
			storeData(rX,rY);
		} else {
			sectors+=4;
			addBit(1);
			compress(subDims[1], upLeftRegs[0], upLeftRegs[1]);
			compress(subDims[0], upRightRegs[0], upRightRegs[1]);
			compress(subDims[2], downLeftRegs[0], downLeftRegs[1]);
			compress(subDims[3], downRightRegs[0], downRightRegs[1]);
		}

	}

	public boolean areSimmilar(double[][] rx, double[][] ry, double[][][] pr, int part) {// part: 0=zgoraj levo, 1=zgoraj desno, 2=spodaj levo 3=
						// spodaj desno
		int xadd = 0;
		int yadd = 0;
		if (part == 1)
			yadd = ry.length / 2;
		if (part == 2)
			xadd = rx.length / 2;
		if (part == 3) {
			yadd = ry.length / 2;
			xadd = rx.length / 2;
		}

		for (int i = 0; i < pr[0].length; i++) {
			// rx[xadd+i][0]~=pr[0][i][0]
			if (Math.abs(rx[xadd + i][0] - pr[0][i][0]) > simmilarity
					|| Math.abs(rx[xadd + i][1] - pr[0][i][1]) > simmilarity
					|| Math.abs(rx[xadd + i][2] - pr[0][i][2]) > simmilarity/2)
				return false;
		}
		for (int i = 0; i < pr[1].length; i++) {
			// ry[yadd+i][0]~=pr[1][i][0]
			if (Math.abs(ry[yadd + i][0] - pr[1][i][0]) > simmilarity
					|| Math.abs(ry[yadd + i][1] - pr[1][i][1]) > simmilarity
					|| Math.abs(ry[yadd + i][2] - pr[1][i][2]) > simmilarity/2)
				return false;
		}
		return true;
	}

	public int getColor(int xs, int ys, int x, int y, double[] rX, double[] rY) { //returns actual color from calculated regressions
		int c = 0;
		double cX = rX[0] + rX[1] * (y - ys);
		if (addRandom)
			cX += 2*(Math.random() - 0.5) * rX[2];
		double cY = rY[0] + rY[1] * (x - xs);
		if (addRandom)
			cY += 2*(Math.random() - 0.5) * rY[2];
		cX += cY;
		cX /= 2;
		if (cX > 255)
			cX = 255;
		if (cX < 0)
			cX = 0;
		return (int) cX;
	}

	public double[][][] getRegressions(int[] dims) {
		int xs=dims[0];
		int xe=dims[1];
		int ys=dims[2];
		int ye=dims[3];
		double[][][] regs = new double[2][][];
		double[][] rX = new double[xe - xs][3];
		double[][] rY = new double[ye - ys][3];
		for (int i = xs; i < xe; i++) {
			double[] col = new double[ye - ys];
			for (int j = ys; j < ye; j++)
				col[j - ys] = img[i][j];
			r.setField(col);
			rX[i - xs] = r.getRegression();
		}
		for (int i = ys; i < ye; i++) {
			double[] col = new double[xe - xs];
			for (int j = xs; j < xe; j++) {
				col[j - xs] = img[j][i];
			}

			r.setField(col);
			rY[i - ys] = r.getRegression();
		}


		
		regs[0] = rX;
		regs[1] = rY;
		return regs;
	}

	public boolean addBit(int bit) {// bit0 pomeni da barvamo zadevo, bit1 pomeni
									// da bomo sliko se razrezali
		//if(bit<0 || bit>1)
		//	throw new Exception("The bit that the system tried to put in was not a bit!");
		
		dataByte <<= 1;
		dataByte += bit;
		cnt++;
		bytes += 0.125;
		if (cnt == 8) {
			// bajt je sestavljen!
			// System.out.println(dataByte);
			partitions.add((byte)dataByte);
			dataByte = 0;
			cnt = 0;
			return true;
		}
		return false;
	}
	
	public void storeData(double[][] rX, double[][] rY){
		double fieldVar=0, fieldVarFactor = rX.length*rY.length;
		
		//downsample regression factors
		if(cfg.DOWN_SAMPLE_REGRESSIONS){
			rX=sampler.downsampleRegression(rX);
			rY=sampler.downsampleRegression(rY);
		}
		
		for(int i=0;i<rX.length;i++)dataa.add(rX[i][0]);
		for(int i=0;i<rX.length;i++)datab.add(rX[i][1]);
		for(int i=0;i<rX.length;i++)fieldVar+=(rX[i][2]);
		for(int i=0;i<rY.length;i++)dataa.add(rY[i][0]);
		for(int i=0;i<rY.length;i++)datab.add(rY[i][1]);
		for(int i=0;i<rY.length;i++)fieldVar+=(rY[i][2]);
		
		fieldVar/=fieldVarFactor;
		datav.add(fieldVar);
	}

	public double normalise(int xs, int ys, int xe, int ye, double[][] rX, double[][] rY){
		double normalFactor=0;
		for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++){
				normalFactor+=img[i][j];
			}
		}
		normalFactor/=(1+ye-ys)*(1+xe-xs);
		/*for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++){
				img[i][j]-=normalFactor;
			}
		}*/
		//upscale derrivative for whole field.
		for(int i=0;rY.length>1 && i<rX.length;i++){
			rX[i][1]*=(rY.length-1);
		}
		for(int i=0;rX.length>1 && i<rY.length;i++){
			rY[i][1]*=(rX.length-1);
		}

		for(int i=0;i<rX.length;i++){
			rX[i][0]-=normalFactor;
		}
		for(int i=0;i<rY.length;i++){
			rY[i][0]-=normalFactor;
		}
		return normalFactor;
	}

	public byte[] getFullPictureByteArr(){
		
		byte[] b= new byte[partitions.size()+dataa.size()+datab.size()];
		int arrayIterator=0;
		for(int i=0;i<partitions.size();i++)
			b[arrayIterator++]=(byte)((int)partitions.get(i));
		for(int i=0;i<dataa.size();i++)
			b[arrayIterator++]=(byte)((double)dataa.get(i));
		for(int i=0;i<datab.size();i++)
			b[arrayIterator++]=(byte)((double)datab.get(i));
		
		return b;
	}
}
