package general.IO;

import shifters.ColorSpaceShifter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class OpenImg {
	public BufferedImage readImg(String fileLoc) throws IOException{
		return ImageIO.read(new File(fileLoc));
	}

	public int[][] returnInteger(BufferedImage img){
		int[][] values;
		int x=img.getWidth();
		int y= img.getHeight();
		values= new int[x][y];
		for (int i=0; i<x;i++){
			for(int j=0; j<y;j++){
				values[i][j]=img.getRGB(i, j)&((1<<24)-1);

			}
		}
		return values;
	}
	public int[][] getAvg(BufferedImage img){
		int[][] rgb= returnInteger(img);
		int x=img.getWidth();
		int y= img.getHeight();
		for (int i=0; i<x;i++){
			for(int j=0; j<y;j++){
				rgb[i][j]=((rgb[i][j]>>16)&255)+((rgb[i][j]>>8)&255)+(rgb[i][j]&255);
				rgb[i][j]/=3;
			}
		}
		return rgb;
	}
	public int[][] getBW(String loc) throws IOException{
		BufferedImage img= readImg(loc);
		return getAvg(img);
	}
	public int[][] getRGB(String loc) throws IOException{
		BufferedImage img= readImg(loc);
		return returnInteger(img);
	}
	public double[][][] getYCbCr(String loc) throws IOException{
		BufferedImage img= readImg(loc);
		int[][] rgb= returnInteger(img);
		double[][][] ycbcr= ColorSpaceShifter.rgb2ycbcr(rgb);
		return ycbcr;
	}
}
