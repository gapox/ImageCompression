package compressorCore.image;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CompressAVG {
	public int[][] img;
	public int[][] rec;
	public int[][] dims;
	public double bytes = 0;
	public int i = 0;
	public int dataByte = 0;
	public double simmilarity = 0.5;// 0-255; 0=loseless compression 255=averaged
								// picture
	public String fileName;
	public ArrayList<Integer> partitions, data;
	public boolean addHiss;
	
	//test vars
	public int[][] overlays;
	
	
	CompressAVG(int[][] image) {
		img = image;
		int maxDepth;
		if (image.length > image[0].length)
			maxDepth = image.length;
		else
			maxDepth = image[0].length;
		rec = new int[img.length][img[0].length];
		dims = new int[(int) Math.ceil((Math.log(maxDepth) / Math.log(2))) + 1][2];
		dims[0][0] = image.length;
		dims[0][1] = image[0].length;
		// System.out.println(dims[0][0]+" "+dims[0][1]);
		for (int i = 1; i < dims.length; i++) {
			int x = dims[i - 1][0] >> 1;
			int y = dims[i - 1][1] >> 1;
			if (dims[i - 1][0] % 2 == 1)
				x++;
			if (dims[i - 1][1] % 2 == 1)
				y++;
			dims[i] = new int[] { x, y };
			// System.out.println(dims[i][0]+" "+dims[i][1]);
		}
		partitions = new ArrayList<Integer>();
		data = new ArrayList<Integer>();
		overlays= new int[image.length][image[0].length];
	}

	public CompressAVG(int[][] image, double lossyness, String fileName, boolean noise)throws FileNotFoundException, UnsupportedEncodingException {
		this(image);
		simmilarity = lossyness * 255;
		this.fileName = fileName;
		addHiss=noise;
	}

	public double getQuotient(int xs, int xe, int ys, int ye) {// lahk je kaj
																// drugega, bolj
																// pametnega kot
																// povprecje!
		double pixelCnt = (xe - xs) * (ye - ys);
		double sum = 0;
		for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++) {
				sum += img[i][j];
			}
		}
		return sum / pixelCnt;
	}

	public void compress(int x, int y, int depth) {
		double all = getQuotient(0, img.length, 0, img[0].length);
		compress(x, y, depth, all);
		if(i!=0){
			dataByte<<=(8-i);
			partitions.add(dataByte);
			bytes++;
		}
		int maxOverlays=0;
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[i].length;j++){
				if(overlays[i][j]>maxOverlays)maxOverlays=overlays[i][j];
			}
		}
		System.out.println("PREKRIVANJA MAX:"+maxOverlays);
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[i].length;j++){
				overlays[i][j]*=(255/maxOverlays);
			}
		}
	}

	public void compress(int x, int y, int depth, double thisQuotient) {
		if (depth == dims.length - 1) {
			// System.out.println(thisQuotient);
			bytes += 1;
			data.add((int)thisQuotient);
			colorIn(x, y, depth, (int) thisQuotient);
			return;
		}
		int xs = x;
		int xe = x + dims[depth][0];
		int ys = y;
		int ye = y + dims[depth][1];

		int ym = ys + (ye - ys) / 2;
		int xm = xs + (xe - xs) / 2;

		double upLeftQuotient = getQuotient(xs, xm, ys, ym);
		double upRightQuotient = getQuotient(xm, xe, ys, ym);
		double downLeftQuotient = getQuotient(xs, xm, ym, ye);
		double downRightQuotient = getQuotient(xm, xe, ym, ye);
		if (Math.abs(thisQuotient - upLeftQuotient) > simmilarity
				|| Math.abs(thisQuotient - upRightQuotient) > simmilarity
				|| Math.abs(thisQuotient - downLeftQuotient) > simmilarity
				|| Math.abs(thisQuotient - downRightQuotient) > simmilarity) {
			addBit(1);
			bytes += 0.125;
			compress(xs, ys, depth + 1, upLeftQuotient);
			compress(xm, ys, depth + 1, upRightQuotient);
			compress(xs, ym, depth + 1, downLeftQuotient);
			compress(xm, ym, depth + 1, downRightQuotient);
		} else {
			addBit(0);
			bytes += 0.125;
			bytes += 1;
			// System.out.print("\nNasel kvadrat! Loc:\nx:"+x+"y:"+y+" Barva pa:"+thisQuotient);
			colorIn(x, y, depth, (int) Math.round(thisQuotient));
		}
	}

	public void colorIn(int x, int y, int depth, int color) {
		int xs = x;
		int xe = x + dims[depth][0];
		int ys = y;
		int ye = y + dims[depth][1];
		for (int i = xs; i < xe; i++) {
			for (int j = ys; j < ye; j++) {
				rec[i][j] = color;
				overlays[i][j]++;
				if(addHiss)rec[i][j] += (int)((Math.random()-0.5)*(dims.length-depth));
			}
		}
		data.add(color);
		/*
		 * int room=8-i; int colorC=color; colorC>>=i; dataByte<<=room;
		 * dataByte+=colorC; //System.out.println(dataByte); colorC=color;
		 * room=1<<(i); room--; colorC&=room dataByte=colorC;
		 */
	}

	public void addBit(int bit) {
		dataByte <<= 1;
		dataByte += bit;
		i++;
		if (i == 8) {
			// bajt je sestavljen!
			// System.out.println(dataByte);
			partitions.add(dataByte);
			dataByte = 0;
			i = 0;
		}
	}
}
