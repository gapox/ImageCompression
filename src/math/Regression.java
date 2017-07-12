package math;

public class Regression {


	public double[] getGeneralizedRegression(double[] field) {
		double[] reg = new double[3];
		double x, y, x2, xy;
		x = getAvgPos(field);
		x2 = getSqAvgPos(field);
		y = getAvgVals(field);
		xy = getAvgComp(field);
		double b = xy - (x * y);
		b /= (x2 - (x * x));
		double a = y * x2 - (x * xy);
		a /= (x2 - (x * x));
		double var = 0;
		for (int i = 0; i < field.length; i++) {
			double regVal = (a + b * i);
			double f = field[i];
			var += Math.abs((regVal - f) * (regVal - f));
		}
		var /= field.length;
		var = (double) Math.sqrt(var);
		reg[0] = a;
		reg[1] = b;
		reg[2] = var;
		return reg;
	}

	public double[] getRegression(double[] field) {
		if(field.length==1)
			return new double[]{field[0],0,0};
		double[] reg = new double[3];
		double b = 0, cxy = 0, a = 0;
		double factor=(double) (field.length-1) / 2.0;
		double average=0.0;
		for (int i = 0; i < field.length; i++) {
			cxy += (i- factor) * field[i];
			average += field[i];
		}
		average /= field.length;
		
		b = (cxy / cxsq(field.length));
		double mult=((double) (field.length - 1) / 2.0);
		a = (average - b * mult) ;

		double var = 0;
		for (int i = 0; i < field.length; i++) {
			double regVal = (a + b * i);
			double f = field[i];
			var += Math.abs((regVal - f) * (regVal - f));
		}
		var /= field.length;
		var = (double) Math.sqrt(var);
		reg[0] = a;
		reg[1] = b;
		reg[2] = var;
		return reg;
	}

	public double[] getMulFactors(double[] field) {
		return getMulFactors(field.length);
	}

	public double[] getMulFactors(int n) {
		double[] ret = new double[n];
		for (int i = 0; i < n; i++)
			ret[i] = (double) ((i + 1 - (double) (n + 1) / 2.0));
		return ret;
	}

	public double[] getReconstructionComponentsFor_b(int n) {
		double[] ret = getMulFactors(n);
		double cx2 = cxsq(n);
		for (int i = 0; i < ret.length; i++)
			ret[i] /= cx2;
		return ret;
	}

	public double[] getReconstructionComponentsFor_a(int n) {
		double[] ret = getMulFactors(n);
		double cx2 = cxsq(n);
		double yavg = 1/(double)n;
		for (int i = 0; i < ret.length; i++)
			ret[i] = (double) (yavg - (ret[i] / cx2) * ((double) (n - 1) / 2.0));
		return ret;
	}

	private double getAvgVals(double[] field) {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += field[i];
		avg /= field.length;
		return avg;
	}

	private double getSqAvgVals(double[] field) {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += (field[i] * field[i]);
		avg /= field.length;
		return avg;
	}

	private double getAvgPos(double[] field) {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += i;
		avg /= field.length;
		return avg;
	}

	private double getSqAvgPos(double[] field) {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += i * i;
		avg /= field.length;
		return avg;
	}

	private double getAvgComp(double[] field) {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += field[i] * i;
		avg /= field.length;
		return avg;
	}


	public double cxsq(double n) {
		double x;
		//n -= 2;
		//x = n * n * n * 0.75 / 9 + n * n * 0.5 + n * 8.25 / 9 + 0.5;
		x=(n*n*n-n)/12;
		return x;
	}

	public static double getAverageOfReg(double[] r, int n){
		return r[0]+r[1]*((double)n-1)/2.0;
	}

	public static double getValAt(double[] r, int i){
		return r[0]+r[1]*i;
	}
}
