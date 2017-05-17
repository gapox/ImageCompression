package math;
import java.io.IOException;

public class EdgeFinder {
	int[][] img;
	int SobelTresh= 180;
	EdgeFinder(int[][] image) {
		img = new int[image.length][image[0].length];
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[0].length; j++)
				img[i][j] = image[i][j];
		}
	}

	public int[][] findEdges() throws IOException {
		int[][] ret = new int[img.length][img[0].length];
		ret= sobel();
		return ret;
	}
	public int[][] gaussianBlur(){
		int w = img.length;
		int h = img[0].length;
		int[][] returnImg= new int[w][h];
		
		return returnImg;
	}
		public int[][] sobel() {
		int w = img.length;
		int h = img[0].length;
		int sobel_x[][] = { 
				{ -1, 0, 1 },
				{ -2, 0, 2 }, 
				{ -1, 0, 1 } };
		int sobel_y[][] = {
				{ -1, -2, -1 },
				{  0,  0,  0 },
				{  1,  2,  1 } };
		int pixel_x;
		int pixel_y;
		int[][] returnImg= new int[w][h];
		for (int x = 1; x < w - 2; x++) {
			for (int y = 1; y < h - 2; y++) {
				pixel_x = (sobel_x[0][0] * img[x - 1][y - 1])
						+ (sobel_x[0][1] * img[x][y-1])
						+ (sobel_x[0][2] * img[x+1][y-1])
						+ (sobel_x[1][0] * img[x-1][y])
						+ (sobel_x[1][1] * img[x][y])
						+ (sobel_x[1][2] * img[x+1][y])
						+ (sobel_x[2][0] * img[x-1][y+1])
						+ (sobel_x[2][1] * img[x][y+1])
						+ (sobel_x[2][2] * img[x+1][y+1]);
				pixel_y = (sobel_y[0][0] * img[x-1][y-1])
						+ (sobel_y[0][1] * img[x][y-1])
						+ (sobel_y[0][2] * img[x+1][y-1])
						+ (sobel_y[1][0] * img[x-1][y])
						+ (sobel_y[1][1] * img[x][y])
						+ (sobel_y[1][2] * img[x+1][y])
						+ (sobel_y[2][0] * img[x-1][y+1])
						+ (sobel_y[2][1] * img[x][y+1])
						+ (sobel_x[2][2] * img[x+1][y+1]);

				int val = (int) Math.sqrt((pixel_x * pixel_x)
						+ (pixel_y * pixel_y));
				//if(val<50)val=0;
				//if(val>200)val=255;
				/*if (val < SobelTresh)
					val = 0;
				else
					val = 255;*/
				returnImg[x][y]=val;
			}
		}
		return returnImg;
	}
}
