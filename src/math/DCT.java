package math;

import shifters.DynamicArray;


public class DCT {
	public int[][] img;
	public int[][] quotients;
	public DynamicArray cos;
	public DCT(int[][] image){
		img=image;
		int maxLen=image.length;
		if(maxLen<image[0].length)maxLen=image[0].length;
		maxLen*=4;
		double[] cosFactors=new double[maxLen];
		for(int i=0;i<maxLen; i++){
			cosFactors[i]= Math.cos(2*Math.PI*(double)i/(double)maxLen);
		}
		cos= new DynamicArray(cosFactors);
	}
	public float[][] getDCTComponents(){
		float[][] ret= new float[img.length][img[0].length];
		float xBorder=0,yBorder=0;
		for(int i=0; i<img.length;i++){
			for(int j=0;j<img[0].length;j++){	
				float factor=0;
				float average=0;
				//float[][] ret1= new float[img.length][img[0].length];
				for(int k=0;k<img.length;k++){
					float xcos=(float) (xBorder*(((float)k+0.5)/(float)img.length));
					for(int l=0;l<img[0].length;l++){
						float ycos= (float) (yBorder*(((float)l+0.5)/(float)img[0].length));
						factor+=img[i][j]*(cos.periodicalValAt(xcos)*cos.periodicalValAt(ycos));
						//ret1[k][l]=cos.periodicalValAt(xcos)*cos.periodicalValAt(ycos);
						average+=cos.periodicalValAt(xcos)*cos.periodicalValAt(ycos);
					}
				}
				factor/=(img.length*img[0].length);
				//ToImg save= new ToImg(ret1);
				//save.outputBW("C:/Users/Gasper/Desktop/Projekti/ImageCompression/DCTFACTORS/DCT"+i+" "+j);
				System.out.println(average);
				yBorder+=0.5;
				ret[i][j]=factor;
			}
			yBorder=0;
			xBorder+=0.5;
		}
		
		
		return ret;
	}
	public int[][] getQuantizedDCT(){
		float[][] dct=getDCTComponents();
		int[][] qDct= new int[dct.length][dct[0].length];
		for(int i=0; i<img.length;i++){
			for(int j=0;j<img[0].length;j++){	
				qDct[i][j]=(int)(dct[i][j]*Math.round(Math.pow(Math.E, (float)(-1*i)))*Math.round(Math.pow(Math.E, (float)(-1*j))));
			}
		}
		
		return qDct;
	}
	
	
	
	
}
