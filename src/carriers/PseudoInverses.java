package carriers;

import configs.Config;
import math.LinearAlgebra;
import math.Regression;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class PseudoInverses {
	Map<String, double[][]> pInvforDepth = new HashMap();
	
	Config cfg;

	public PseudoInverses(int x, int y, Config cfg) {
		this.cfg=cfg;
	}

	public void addPinverse(int x, int y) {
		pInvforDepth.put(x+" "+y,new LinearAlgebra(generateMatrixToInverse(x, y)).pseudoInv());
	}
	public void addTransposeAsInverse(int x, int y) {
		pInvforDepth.put(x+" "+y,getTransposedMatrixToInverseAtDepth(x, y));
	}

	public double[][] getPinverse(int x, int y) {
		if(!pInvforDepth.containsKey(x+" "+y)){
			//System.out.println("MISS!	x:"+x+"	y:"+y);
			if(x*y<=cfg.MAX_PINV_CELLS)addPinverse(x, y);
			else if(x*y<=cfg.MAX_TR_INV_CELLS)addTransposeAsInverse(x, y);
			else return null;
		}
		return pInvforDepth.get(x+" "+y);
	}

	public String stringifyInverse(int depth) {
		String matrix = "";
		double[][] mrx = pInvforDepth.get(depth);
		if (mrx != null) {
			matrix += "Inverse for size(" + mrx.length + ", " + mrx[0].length + ")\n";
			NumberFormat formatter = new DecimalFormat("#0.00");
			for (int i = 0; i < mrx.length; i++) {
				for (int j = 0; j < mrx[i].length; j++) {
					if (mrx[i][j] < 0)
						matrix += ("-" + formatter.format(-mrx[i][j]) + " ");
					else
						matrix += (" " + formatter.format(mrx[i][j]) + " ");

				}
				matrix += "\n";
			}
			matrix += "\n";
			matrix += "\n";
		} else {
			matrix += "MATRIX NOT SPECIFIED!!\n\n";
		}
		return matrix;
	}

	public void printInverses() {
		for (int i = 0; i < pInvforDepth.size(); i++) {
			System.out.println(stringifyInverse(i));
		}
	}

	public float[][] getNormalisedInverseForDepth(int depth) {
		if (getInverseForDepth(depth) == null)
			return null;
		return normaliseMatrix(getInverseForDepth(depth));
	}
	public float[][] getTransposedNormalisedInverseForDepth(int depth) {
		if (getInverseForDepth(depth) == null)
			return null;
		return normaliseMatrix(new LinearAlgebra().transposeGivenMatrix(getInverseForDepth(depth)));
	}

	public double[][] getInverseForDepth(int depth) {
		return pInvforDepth.get(depth);
	}

	public float[][] normaliseMatrix(double[][] inv) {
		float[][] ret = new float[inv.length][inv[0].length];
		double min = 0, max = 0;
		for (int i = 0; i < inv.length; i++) {
			for (int j = 0; j < inv[i].length; j++) {
				if (max < inv[i][j])
					max = inv[i][j];
				if (min > inv[i][j])
					min = inv[i][j];
			}
		}
		double diff = max - min;
		for (int i = 0; i < inv.length; i++) {
			for (int j = 0; j < inv[i].length; j++) {
				ret[i][j] = (float) ((inv[i][j] - min) / diff);
			}
		}
		return ret;
	}

	public int[] getSizeAtDepth(int depth) {
		double[][] mrx = pInvforDepth.get(depth);
		if (mrx == null)
			return new int[] { 0, 0 };
		return new int[] { mrx.length, mrx[0].length };
	}

	public double[][] getMatrixToInverseAtDepth(int x, int y) {

		return generateMatrixToInverse(x,y);
	}

	public double[][] getTransposedMatrixToInverseAtDepth(int x, int y) {

		return new LinearAlgebra().transposeGivenMatrix(getMatrixToInverseAtDepth(x, y));
	}

	public double[][] generateMatrixToInverse(int x, int y) {
		double[][] L = new double[2 * y + 2 * x][y * x];
		double[] xb, xa, yb, ya;
		Regression reg = new Regression();
		xb = reg.getReconstructionComponentsFor_b(x);
		xa = reg.getReconstructionComponentsFor_a(x);

		reg = new Regression();
		yb = reg.getReconstructionComponentsFor_b(y);
		ya = reg.getReconstructionComponentsFor_a(y);
		int start = 0;
		int add = 1;
		for (int i = 0; i < y; i++) {
			for (int j = start; j < start + xb.length; j += add) {
				L[i][j] = xb[j - start];
			}
			start += xb.length;
		}
		start = 0;
		add = x;
		for (int i = 0; i < x; i++) {
			for (int j = start; j < L[0].length; j += add) {
				L[i + y][j] = yb[j / add];
			}
			start++;
		}

		start = 0;
		add = 1;
		for (int i = 0; i < y; i++) {
			for (int j = start; j < start + xb.length; j += add) {
				L[i + y + x][j] = xa[j - start];
			}
			start += xb.length;
		}
		start = 0;
		add = x;
		for (int i = 0; i < x; i++) {
			for (int j = start; j < L[0].length; j += add) {
				L[i + 2 * y + x][j] = ya[j / add];
			}
			start++;
		}
		return L;
	}
}