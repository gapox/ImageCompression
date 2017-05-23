package math;

public class Regression {
	private double[] field;

	public Regression() {
		field = null;
	}

	public Regression(double[] array) {
		field = array;
	}

	public void setField(double[] array) {
		field = array;
	}

	public double[] getGeneralizedRegression() {
		double[] reg = new double[3];
		double x, y, x2, xy;
		x = getAvgPos();
		x2 = getSqAvgPos();
		y = getAvgVals();
		xy = getAvgComp();
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

	public double[] getRegression() {

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

	public double[] getMulFactors() {
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

	private double getAvgVals() {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += field[i];
		avg /= field.length;
		return avg;
	}

	private double getSqAvgVals() {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += (field[i] * field[i]);
		avg /= field.length;
		return avg;
	}

	private double getAvgPos() {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += i;
		avg /= field.length;
		return avg;
	}

	private double getSqAvgPos() {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += i * i;
		avg /= field.length;
		return avg;
	}

	private double getAvgComp() {
		double avg = 0;
		for (int i = 0; i < field.length; i++)
			avg += field[i] * i;
		avg /= field.length;
		return avg;
	}


	private double cxsq(double n) {
		double x;
		//n -= 2;
		//x = n * n * n * 0.75 / 9 + n * n * 0.5 + n * 8.25 / 9 + 0.5;
		n--;
		x=0.41666666666*n*n*n-0.75*n*n+0.08333333333*n;
		return x;
	}

}
