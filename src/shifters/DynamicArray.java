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
		/*
		 * int idxl=(int)(fract*(double)arr.length),
		 * idxu=(int)Math.ceil(fract*(double)arr.length);
		 * if(idxl<0)idxl=arr.length-1; if(idxu>=arr.length)idxu=arr.length-1;
		 * if(idxl>=arr.length)idxl=arr.length-1; double der=
		 * arr[idxu]-arr[idxl];
		 * ret=(double)arr[idxl]+der*((fract*(double)arr.length)-(double)idxl);
		 */
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

	public double[] getArray(int len) {
		double[] array = new double[len];
		//double stepSize = (double) arrD.length / (double) (len);
		for(int i=0;i<array.length;i++)
			array[i]=valAt((i+0.5)/(double)array.length);
		/*
		 * int cnt=0; for(int i=0;i<arr.length;i++){
		 * avg+=valAt((double)i/(double)arr.length);
		 * if(i>(cnt+1)*((double)arr.length/(double)len)){
		 * array[cnt]=avg/((double)arr.length/(double)len); avg=0; cnt++; } }
		 */
		// array[cnt]=avg/((double)arr.length/(double)len);
		return array;
	}
}
