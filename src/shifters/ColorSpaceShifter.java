package shifters;

import peripheral.Logger;

public class ColorSpaceShifter {

	public static int[][] rgb2ycbcr1(int [][] in){
		Logger.logMath("Transforming from RGB to YCbCr color scheme.");
		if(in==null)return null;
		double factor=8;
		int[][] ret= new int[in.length][in[0].length];
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[0].length;j++){
				int r,g,b,y,cb,cr, pix=in[i][j];
				r=(pix&(255<<16))>>16;
				g=(pix&(255<<8))>>8;
				b=(pix&(255));
				y=(int)(((double)r)*0.299+((double)g)*0.587+((double)b)*0.114);
				cb=(int)((1.0/factor)*(((double)r)*-0.168935+((double)g)*-0.331665+((double)b)*0.50059));
				cr=(int)((1.0/factor)*(((double)r)*0.499813+((double)g)*-0.418531+((double)b)*-0.081282));
				cb+=128;
				//cb&=240;//korak za kompresijo
				cr+=128;
				//cr&=240;//korak za kompresijo
				//if(cb<0||cr<0)System.out.print("PIKOLA\n");
				ret[i][j]=((y&255)<<16)+((cb&255)<<8)+(cr&255);
			}
		}
		return ret;
	}

	public static int[][] ycbcr2rgb1(int[][] in){
		Logger.logMath("Transforming from YCbCr to RGB color scheme.");
		if(in==null)return null;
		double factor=8;
		int[][] ret= new int[in.length][in[0].length];
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[0].length;j++){
				int r,g,b,y=0,cb=0,cr=0, pix=in[i][j];
				y=(pix&(255<<16))>>16;
				cb=(pix&(255<<8))>>8;
				cb-=128;
				cr=(pix&(255));
				cr-=128;
				r=(int)(((double)y)*1.0+((double)(factor*cb))*0.0+((double)(factor*cr))*1.403);
				g=(int)(((double)y)*1.0+((double)(factor*cb))*-0.344+((double)(factor*cr))*-0.714);
				b=(int)(((double)y)*1.0+((double)(factor*cb))*1.77+((double)(factor*cr))*0.0);
				if(r<0)r=0;
				if(g<0)g=0;
				if(b<0)b=0;
				if(r>255)r=255;
				if(g>255)g=255;
				if(b>255)b=255;
				ret[i][j]=((r&255)<<16)+((g&255)<<8)+(b&255);
			}
		}
		return ret;
	}

	public static int[][] rgb2ycbcr2(int [][] in){
		Logger.logMath("Transforming from RGB to YCbCr color scheme.");
		if(in==null)return null;
		int[][] ret= new int[in.length][in[0].length];
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[0].length;j++){
				double r,g,b,y,cb,cr;
				int pix=in[i][j];
				r=(pix&(255<<16))>>16;
				g=(pix&(255<<8))>>8;
				b=(pix&(255));
				y= 0+	(r*0.299+g*0.587+b*0.114);
				cb=128+	(r*-0.169+g*-0.331+b*0.500);
				cr=128+	(r*0.500+g*-0.419+b*-0.081);
				if(y-(int)y>0.5) y++;
				if(cb-(int)cb>0.5)cb++;
				if(cr-(int)cr>0.5)cr++;
				ret[i][j]=((((int)y)&255)<<16)+((((int)cb)&255)<<8)+(((int)cr)&255);
			}
		}
		return ret;
	}

	public static int[][] ycbcr2rgb2(int[][] in){
		Logger.logMath("Transforming from YCbCr to RGB color scheme.");
		if(in==null)return null;
		double factor=8;
		int[][] ret= new int[in.length][in[0].length];
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[0].length;j++){
				double r,g,b,y,cb,cr;
				int pix=in[i][j];
				y=(pix&(255<<16))>>16;
				cb=(pix&(255<<8))>>8;
				cr=(pix&(255));

				cb-=128;
				cr-=128;
				//r=(int)(y*1.0+cb*0.0+cr*1.403);
				//g=(int)(y*1.0+cb*-0.343+cr*-0.711);
				//b=(int)(y*1.0+cb*1.765+cr*0.0);

				r=(int)(y*1.0+cb*-0.0000926745+cr*1.40169);
				g=(int)(y*1.0+cb*-0.343695+cr*-0.714169);
				b=(int)(y*1.0+cb*1.77216+cr*0.000990221);

				if(Math.abs(r-(int)r)>0.5)r++;
				if(Math.abs(g-(int)g)>0.5)g++;
				if(Math.abs(b-(int)b)>0.5)b++;
				if(r<0)r=0;
				if(g<0)g=0;
				if(b<0)b=0;
				if(r>255)r=255;
				if(g>255)g=255;
				if(b>255)b=255;
				ret[i][j]=((((int)r)&255)<<16)+((((int)g)&255)<<8)+(((int)b)&255);
			}
		}
		return ret;
	}

	public static double[][][] rgb2ycbcr(int [][] in){
		Logger.logMath("Transforming from RGB to YCbCr color scheme.");
		if(in==null)return null;
		double[][][] ret= new double[3][in.length][in[0].length];
		for(int i=0;i<ret[0].length;i++){
			for(int j=0;j<ret[0][0].length;j++){
				double r,g,b,y,cb,cr;
				int pix=in[i][j];
				r=(pix&(255<<16))>>16;
				g=(pix&(255<<8))>>8;
				b=(pix&(255));
				y= 0+	(r*1.0*0.299+g*1.0*0.587+b*1.0*0.114);
				cb=128+	(r*1.0*-0.169+g*1.0*-0.331+b*1.0*0.500);
				cr=128+	(r*1.0*0.500+g*1.0*-0.419+b*1.0*-0.081);
				ret[0][i][j]=y/255.0;
				ret[1][i][j]=cb/255.0;
				ret[2][i][j]=cr/255.0;
			}
		}
		return ret;
	}

	public static int[][] ycbcr2rgb(double[][][] in){
		Logger.logMath("Transforming from YCbCr to RGB color scheme.");
		if(in==null)return null;
		double factor=8;
		int[][] ret= new int[in[0].length][in[0][0].length];
		for(int i=0;i<ret.length;i++){
			for(int j=0;j<ret[0].length;j++){
				double r,g,b,y,cb,cr;
				y=in[0][i][j]*255.0;
				cb=in[1][i][j]*255.0;
				cr=in[2][i][j]*255.0;

				cb-=128;
				cr-=128;
				//r=(int)(y*1.0+cb*0.0+cr*1.403);
				//g=(int)(y*1.0+cb*-0.343+cr*-0.711);
				//b=(int)(y*1.0+cb*1.765+cr*0.0);

				r=(y+cb*-0.0000926745+cr*1.40169);
				g=(y*0.99999+cb*0.99999*-0.343695+cr*0.99999*-0.714169);
				b=(y+cb*1.77216+cr*0.000990221);


				if(Math.abs(r-(int)r)>0.5)r++;
				if(Math.abs(g-(int)g)>0.5)g++;
				if(Math.abs(b-(int)b)>0.5)b++;
				if(r<0)r=0;
				if(g<0)g=0;
				if(b<0)b=0;
				if(r>255)r=255;
				if(g>255)g=255;
				if(b>255)b=255;
				ret[i][j]=((((int)r)&255)<<16)+((((int)g)&255)<<8)+(((int)b)&255);
			}
		}
		return ret;
	}


}
