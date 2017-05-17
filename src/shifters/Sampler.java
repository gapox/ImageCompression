package shifters;

import math.LinearAlgebra;

public class Sampler {
	double[] array;
	DynamicArray dynA;
	LinearAlgebra la;
	public Sampler() {
		array = null;
		la= new LinearAlgebra();

	}

	public void setArray(double[] arr) {
		array = arr;
	}

	public double[][] downsampleRegression(double[][] regs){//regressions are described in math.Regression
		double[][] ret= la.transposeGivenMatrix(regs);
		setArray(ret[0]);
		ret[0]=downsampleArray();
		setArray(ret[1]);
		ret[1]=downsampleArray();
		setArray(ret[2]);
		ret[2]=downsampleArray();
		
		return la.transposeGivenMatrix(ret);
	}
	public double[] downsampleArray() {
		double[] subs;
		int len = downsampleLengthForLength(array.length);
		if (len == array.length)
			return array;
		dynA = new DynamicArray(array);
		subs = dynA.getArray(len);
		return subs;
	}

	public int downsampleLengthForLength(int origLen) {
		if(origLen <= 16)
			return origLen;
		double lenLog = Math.log(origLen);
		return (int)(2.0* lenLog*lenLog);
	}
	public int downsampleLengthForLength1(int origLen) {
		if(origLen <= 12)
			return origLen;
		double lenLog = (Integer.SIZE-Integer.numberOfLeadingZeros(origLen))-1;
		return (int)(lenLog*lenLog);
	}

	public double[][] upsampleRegression(double[][] regs, int len){
		double[][] ret= la.transposeGivenMatrix(regs);
		setArray(ret[0]);
		ret[0]=upsampleArray(len);
		setArray(ret[1]);
		ret[1]=upsampleArray(len);
		setArray(ret[2]);
		ret[2]=upsampleArray(len);
		
		return la.transposeGivenMatrix(ret);
	}
	
	public double[] upsampleArray(int len) {
		double[] ups;
		if(len==array.length)
			return array;
		dynA = new DynamicArray(array);
		ups = dynA.getArray(len);
		return ups;
	}

}
