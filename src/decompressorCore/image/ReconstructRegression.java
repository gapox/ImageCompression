package decompressorCore.image;

import configs.Config;
import carriers.FieldRegressions;
import carriers.PseudoInverses;
import configs.ConfigImg;
import math.LinearAlgebra;
import math.Regression;
import math.RegressionMerge;
import math.Subsectors;
import shifters.Sampler;

import java.util.ArrayList;
import java.util.Random;

public class ReconstructRegression {
	double[][] rec, recLines;
	int cnt = 0, bcnt = 7;
	public ArrayList<Double> dataa, datab, datav;
	public ArrayList<Double> normals;
	int itera = 0, iterb = 0, iterv = 0;
	byte[] partitions;
	int _maxPixelsForSVD=1024;
	ArrayList<FieldRegressions> fields;
	FieldRegressions[] field;
	PseudoInverses pinv;
	Random rnd;
	Sampler sampler;
	Subsectors sub;
	Config cfg;
	ConfigImg cimg;
	public ReconstructRegression(int x, int y, ArrayList<Double> dataa, ArrayList<Double> datab, ArrayList<Double> datav, ArrayList<Double> normals,
								 ArrayList<Byte> partitions, ConfigImg cimg) {
		fields = new ArrayList();
		sub= new Subsectors();
		this.dataa = dataa;
		this.datab = datab;
		this.datav = datav;
		this.normals=normals;
		pinv= new PseudoInverses();
		this.cimg=cimg;
		sampler= new Sampler();
		partitionGetArray(partitions);
		rec = new double[x][y];
		recLines = new double[1 + 2 * x][1 + 2 * y];
		reconstructPartitions(new int[]{0,x,0, y});
		fieldsGetArray(fields);
		for (int i = 0; i < field.length; i++) {
			int xlen = field[i].getX();
			int ylen = field[i].getY();
			field[i].rX = new double[xlen][3];
			field[i].rY = new double[ylen][3];
			
			//UPSAMPLING
			int xlendsampled=xlen;
			int ylendsampled=ylen;
			if(cimg.isDownsampledRegressions()){
				xlendsampled=sampler.downsampleLengthForLength(xlen);
				ylendsampled=sampler.downsampleLengthForLength(ylen);
			}
			double[][] rXdsampled, rYdsampled;
			rXdsampled=new double[xlendsampled][3];
			rYdsampled=new double[ylendsampled][3];
			for (int j = 0; j < xlendsampled; j++) {
				rXdsampled[j][0] = dataa.get(itera++);
				rXdsampled[j][1] = datab.get(iterb++);
				field[i].var = datav.get(iterv);
			}
			for (int j = 0; j < ylendsampled; j++) {
				rYdsampled[j][0] = dataa.get(itera++);
				rYdsampled[j][1] = datab.get(iterb++);
				field[i].var = datav.get(iterv);
			}
			iterv++;
			if(cimg.isDownsampledRegressions()){
				field[i].rX=sampler.upsampleRegression(rXdsampled, xlen);
				field[i].rY=sampler.upsampleRegression(rYdsampled,ylen);
			}
			else{
				field[i].rX=rXdsampled;
				field[i].rY=rYdsampled;
			}
			field[i].addNormal(normals.get(i));
			field[i].downscaleRegressions();
		}
		rnd=new Random(System.currentTimeMillis());
	}

	public double[][][] reconstruct() {
		for (int i = 0; i < field.length; i++) {
			colorIn(field[i]);
		}
		return new double[][][] { rec, recLines };
	}

	public void partitionGetArray(ArrayList<Byte> p) {
		partitions = new byte[p.size()];
		for (int i = 0; i < p.size(); i++)
			partitions[i] = p.get(i);
	}

	public void fieldsGetArray(ArrayList<FieldRegressions> f) {
		field = new FieldRegressions[f.size()];
		for (int i = 0; i < f.size(); i++)
			field[i] = f.get(i);
	}

	public void colorIn(FieldRegressions f) {
		int[] dims= f.getDims();
		if(cfg.USE_REGRESSION_FITTING)
			colorInRegFitV2(dims, f.rX, f.rY);
		else if(cfg.USE_SVD_FITTING && f.getArea()<=_maxPixelsForSVD)
			colorInSVD(dims, f.rX, f.rY);
		else
			colorInAvg(dims, f.rX, f.rY);
		if(cfg.ADD_NOISE){
			addRandom(dims, f.var);
		}
	}
	private void addRandom(int[] dims, double var) {
		int xs = dims[0];
		int xe = dims[1];
		int ys = dims[2];
		int ye = dims[3];
		if (xe > rec.length)
			xe = rec.length;
		if (ye > rec[0].length)
			ye = rec[0].length;
		for(int i=xs;i<xe;i++){
			for(int j=ys;j<ye;j++){
				rec[i][j]+=(rnd.nextDouble()*var)*(rnd.nextInt(2)==1?-1:1);
				if(rec[i][j]>1.0)
					rec[i][j]=1.0;
				if(rec[i][j]<0)
					rec[i][j]=0;
			}
		}
	}

	public void colorInRegFit(int[] dims, double[][] rX, double[][] rY){
		RegressionMerge regMrg= new RegressionMerge();
		int xs = dims[0];
		int xe = dims[1];
		int ys = dims[2];
		int ye = dims[3];
		if (xe > rec.length)
			xe = rec.length;
		if (ye > rec[0].length)
			ye = rec[0].length;
		// for showing where the cuts of the image were.
		lineInsert(xs, xe, ys, ye);


		for (int i = xs; i < xe; i++) {
			int x=i-xs;
			for (int j = ys; j < ye; j++) {
				int y=j-ys;
				double color=getColor(xs, ys, i, j, rX[x], rY[y]);
				rX[x]=regMrg.removeLeftMostValFromReg(rX[x], (ye-ys)-y, color);
				rY[y]=regMrg.removeLeftMostValFromReg(rY[y], (xe-xs)-x, color);
				rec[i][j] = color;
				recLines[2 * i + 1][2 * j + 1] = color;
			}
		}
	}


	public void colorInRegFitV2(int[] dims, double[][] rX, double[][] rY){
		RegressionMerge regMrg= new RegressionMerge();
		Regression reg = new Regression();
		int xs = dims[0];
		int xe = dims[1];
		int ys = dims[2];
		int ye = dims[3];
		if (xe > rec.length)
			xe = rec.length;
		if (ye > rec[0].length)
			ye = rec[0].length;
		// for showing where the cuts of the image were.
		lineInsert(xs, xe, ys, ye);


		for (int i = xs; i < xe; i++) {
			int x=i-xs;
			for (int j = ys; j < ye; j++) {
				int y=j-ys;
				double color=getColor(xs, ys, i, j, rX[x], rY[y]);
				double xCol=reg.getValAt(rX[x], x);
				double yCol=reg.getValAt(rY[y], y);
				rX[x]=regMrg.removeLeftMostValFromReg(rX[x], 2*(ye-ys)-y, (yCol+color)/2.0);
				rY[y]=regMrg.removeLeftMostValFromReg(rY[y], 2*(xe-xs)-x, (xCol+color)/2.0);
				rec[i][j] = color;
				recLines[2 * i + 1][2 * j + 1] = color;
			}
		}
	}

	public void colorInSVD(int[] dims, double[][] rX, double[][] rY) {
		int xs = dims[0];
		int xe = dims[1];
		int ys = dims[2];
		int ye = dims[3];
		if (xe > rec.length)
			xe = rec.length;
		if (ye > rec[0].length)
			ye = rec[0].length;
		// for showing where the cuts of the image were.
		lineInsert(xs, xe, ys, ye);

		// you can downsample regressions here.
		// rX=downsampleRegressions(rX);
		// rY=downsampleRegressions(rY);

		int start = 0;
		int add;
		double[] coefs = new double[2 * rX.length + 2 * rY.length];
		add = 0;
		for (int i = 0; i < rX.length; i++)
			coefs[add + i] = rX[i][1];
		add += rX.length;
		for (int i = 0; i < rY.length; i++)
			coefs[add + i] = rY[i][1];

		add += rY.length;
		for (int i = 0; i < rX.length; i++)
			coefs[add + i] = rX[i][0];
		add += rX.length;
		for (int i = 0; i < rY.length; i++)
			coefs[add + i] = rY[i][0];
		double[][] reconVect = new LinearAlgebra().multiply(pinv.getPinverse(ye-ys, xe-xs), coefs);
		//double[][] reconVect = new LinearAlgebra().multiply(pinv.getUpscaledZerolessPinverse(ye-ys, xe-xs), coefs);
		double[][] reconstructed = new double[rX.length][rY.length];
		for (int i = 0; i < reconVect[0].length; i++) {
			reconstructed[i / rY.length][i % rY.length] = reconVect[0][i];
		}
		for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++) {
				if (reconstructed[i - xs][j - ys] > 1)
					reconstructed[i - xs][j - ys] = 1;
				if (reconstructed[i - xs][j - ys] < 0)
					reconstructed[i - xs][j - ys] = 0;
				// TUKAJ SE VPISUJE V REC!!!!!!!!!!!!!!
				recLines[i * 2+1][j * 2+1] = rec[i][j] = (reconstructed[i - xs][j - ys]);
			}
		}
	}

	public void colorInAvg(int[] dims, double[][] rX, double[][] rY) {
		int xs = dims[0];
		int xe = dims[1];
		int ys = dims[2];
		int ye = dims[3];
		if (xe > rec.length)
			xe = rec.length;
		if (ye > rec[0].length)
			ye = rec[0].length;
		// for showing where the cuts of the image were.
		lineInsert(xs, xe, ys, ye);
		double randomness=0;
		for(int i=0;i<rX.length;i++){
			randomness+=rX[i][2];
		}
		for(int i=0;i<rY.length;i++){
			randomness+=rY[i][2];
		}

		// for getting real filtered regression field
		//double[][][] fields = getFilteredRegressionFields(rX, rY);
		//double[][] rFieldX = fields[0];
		//double[][] rFieldY = fields[1];
				
		
		
		for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++) {
				double color = getColor(xs, ys, i, j, rX[i - xs], rY[j - ys]);
				rec[i][j] = color;
				recLines[2 * i + 1][2 * j + 1] = color;
			}
		}
		
		
	}

	public double getColor(int xs, int ys, int x, int y, double[] rX, double[] rY) { //returns actual color from calculated regressions
		int c = 0;
		
		double cX = rX[0] + rX[1] * (y - ys);
		double cY = rY[0] + rY[1] * (x - xs);

		cX += cY;
		cX /= 2;
		if (cX > 1)
			cX = 1.0;
		if (cX < 0)
			cX = 0;
		return cX;
	}

	private void lineInsert(int xs, int xe, int ys, int ye) {
		for (int i = xs; i < xe; i++) {
			int ii = 2 * i;
			recLines[ii][2 * ys] = 1;
			recLines[ii][2 * ye] = 1;
			recLines[ii + 1][2 * ys] = 1;
			recLines[ii + 1][2 * ye] = 1;
		}
		for (int i = ys; i < ye; i++) {
			int ii = 2 * i;
			recLines[2 * xs][ii] = 1;
			recLines[2 * xe][ii] = 1;
			recLines[2 * xs][ii + 1] = 1;
			recLines[2 * xe][ii + 1] = 1;
		}
	}

	public void reconstructPartitions(int[] dims) {
		if (getPartitionBit()) {
			// rezemo dalje.
			int[][] subDims=sub.getSubsectors(dims);
			
			reconstructPartitions(subDims[1]);//quad:1
			reconstructPartitions(subDims[0]);//quad:0
			reconstructPartitions(subDims[2]);//quad:2
			reconstructPartitions(subDims[3]);//quad:3
		} else {
			// pripravljamo na barvanje
			FieldRegressions fr = new FieldRegressions(dims[0],dims[2], dims[1], dims[3]);
			fields.add(fr);
		}

	}

	public boolean getPartitionBit() {
		byte b = partitions[cnt];
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

}
