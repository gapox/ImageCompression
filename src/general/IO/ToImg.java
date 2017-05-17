package general.IO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ToImg {
	int[][] x;
	public ToImg(int[][] x) {
		// TODO Auto-generated constructor stub
		this.x = x;
	}

	public ToImg(double[][] img){
		x= new int[img.length][img[0].length];
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[0].length;j++){
				x[i][j]=(int)(255*img[i][j]);// prej je blo 128 + x*127
			}
		}
	}
	public void outputBW(String filename) {
		BufferedImage image = new BufferedImage( x.length,x[0].length,
				BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		for (int w = 0; w < x.length; w++) {
			for(int h=0;h<x[0].length;h++){
					
					raster.setPixel(w, h, new int[]{x[w][h],x[w][h],x[w][h]});
			}
		}
		try {
			ImageIO.write(image, "PNG", new File(filename+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void outputRGB(String filename) {
		BufferedImage image = new BufferedImage( x.length,x[0].length,
				BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		for (int w = 0; w < x.length; w++) {
			for(int h=0;h<x[0].length;h++){
					
					raster.setPixel(w, h, new int[]{x[w][h]>>16,(x[w][h]>>8)&255,x[w][h]&255});
			}
		}
		try {
			ImageIO.write(image, "PNG", new File(filename+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

