package shifters;

public class DynamicArray {
	private double[] arrD;

	DynamicArray(int[] x) {
		arrD = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			arrD[i] = (double) x[i];
		}
	}

	public DynamicArray(double[] x) {
		arrD = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			arrD[i] = x[i];
		}
	}

	public double valAt(double fract) {// fract from 0 to 1
		if (fract < 0 || fract > 1)
			return Double.NaN;// if index out of bounds

		double ret = 0;

		double idx = fract * (arrD.length - 1);
		ret += arrD[(int) idx];
		if ((int) idx < arrD.length - 1) {
			double fr = idx % 1;
			double diff = arrD[(int) idx + 1] - arrD[(int) idx];
			ret += fr * diff;
		}
		return ret;
	}


	public double periodicalValAt(double fract) {
		fract %= 1.0;
		return valAt(fract);
	}

	public double[] getArrayLinearInterpolation(int len) {
		double[] array = new double[len];
		for (int i = 0; i < array.length; i++)
			array[i] = valAt((i + 0.5) / (double) array.length);
		return array;
	}

	public double[] getArrayBicubicInterpolation(int len) {
		double[] array = new double[len];
		double[] grads = new double[arrD.length];
		double stepLen = (double) (arrD.length-1) / (double) len;
		for (int i = 1; i < grads.length - 1; i++) {
			grads[i] = arrD[i + 1] - arrD[i - 1];
			grads[i] /= 2;
		}
		if (arrD.length == 1) {
			for (int i = 0; i < len; i++)
				array[i] = arrD[0];
			return array;
		}
		for (int i = 0; i < array.length; i++) {
			double step = i * stepLen;
			int idxLow = (int) step;
			double grad1 = grads[idxLow];
			double grad2 = grads[idxLow + 1];
			double a=2*arrD[idxLow]-2*arrD[idxLow+1]+grad1+grad2;
			double b=-3*arrD[idxLow]+3*arrD[idxLow+1]-2*grad1-grad2;
			double c=grad1;
			double x=step-idxLow;
			array[i]=a*x*x*x+b*x*x+c*x+arrD[idxLow];
		}
		return array;
	}
}
