package general.IO;

import shifters.DynamicArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OutAsHistogram {
	String _locOut;
	double[] arr;
	int _imgHeight=512;
	int _imgWidth=1024;
	
	public OutAsHistogram(String locOut){
		_locOut=locOut;
	}
	public OutAsHistogram(String locOut, ArrayList<Double> arrayList) {
		this(locOut);
		setNewArr(arrayList);
	}
	public void setNewArr( ArrayList<Double> arrayList){
		arr = new double[arrayList.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = arrayList.get(i);
		}
	}
	public void outputThis(String filename, ArrayList<Double> arrayList){
		setNewArr(arrayList);
		output(filename);
	}
	
	public void output(String filename) {
		BufferedImage image = new BufferedImage(_imgWidth, _imgHeight, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		double[][] hist=getHist(_imgWidth);
		for (int w = 0; w <_imgWidth; w++) {
			for (int h = 0; h < _imgHeight; h++) {
				if ( hist[0][w]/hist[2][0] < (double) h / (double) _imgHeight)
					raster.setPixel(w, _imgHeight - 1 - h, new int[] { 255, 255, 255 });
				else
					raster.setPixel(w, _imgHeight - 1 - h, new int[] { 0, 0, 0 });
			}
		}
		try {
			ImageIO.write(image, "PNG", new File(_locOut+filename+ "    min"+hist[1][0]+"max"+hist[1][1]+ ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double[][] getHist(int numOfBoxes){
		double[] boxes = new double[numOfBoxes];
		double[] minmax= new double[2];
		double min=0, max=0;
		int maxInBox=0;
		for(int i=0;i<arr.length;i++){
			if(min>arr[i])min=arr[i];
			if(max<arr[i])max=arr[i];
		}
		minmax[0]=min;
		minmax[1]=max;
		DynamicArray da= new DynamicArray(new double[]{min, max});
		double[] bounds=da.getArrayLinearInterpolation(_imgWidth+1);
		double lowerBound=bounds[0];
		double upperBound=bounds[1];
		int numInBox=0;
		for(int j=0;j<arr.length;j++){
			if(arr[j]>=lowerBound && arr[j]<upperBound)
				numInBox++;
		}
		boxes[0]=numInBox;
		if(numInBox>maxInBox)
			maxInBox=numInBox;
		for(int i=1;i<numOfBoxes;i++){
			numInBox=0;
			lowerBound=bounds[i];
			upperBound=bounds[i+1];
			for(int j=0;j<arr.length;j++){
				if(arr[j]>=lowerBound && arr[j]<upperBound)
					numInBox++;
			}
			boxes[i]=numInBox;
			if(numInBox>maxInBox)
				maxInBox=numInBox;
		}
		
		
		return new double[][]{boxes, minmax, new double[]{maxInBox}};
		
	}
	
	
	
	
	
	
	
}
