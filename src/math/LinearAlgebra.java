package math;

import org.ejml.alg.dense.decomposition.svd.SvdImplicitQrDecompose_D64;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import peripheral.Logger;

public class LinearAlgebra {
	Logger LOG = new Logger();
	double[][] matrix;
	double[] vector;
	double scalar;
	public LinearAlgebra() {
		// TODO Auto-generated constructor stub
		scalar=0;
	}
	public LinearAlgebra(double[][] matrix){


		this.matrix=matrix;
	}
	
	public double[][] transposeGivenMatrix(double[][] mrx){
		double[][] transpose= new double[mrx[0].length][mrx.length];
		for(int i=0;i<mrx.length;i++){
			for(int j=0;j< mrx[i].length;j++){
				transpose[j][i]=mrx[i][j];
			}
		}
		return transpose;
	}

	public double[][] transposeMatrix(){
		return transposeGivenMatrix(matrix);
	}

	public double[][] pseudoInv(){
		DenseMatrix64F mrx= new DenseMatrix64F(matrix);
		boolean transposed=false;
		/*if(matrix.length<matrix[0].length){
			mrx=mrx.;
			transposed=true;
		}*/
		SvdImplicitQrDecompose_D64 svd= new SvdImplicitQrDecompose_D64(false, true, true, false);
		svd.decompose(mrx);
		
		DenseMatrix64F u,v;
		u=svd.getU(null, true);
		double[] sVals=svd.getSingularValues();
		v=svd.getV(null, false);
		for(int i=0;i<sVals.length;i++){
			if(sVals[i]>0.0) {
				if (sVals[i] > 0.0001)
					sVals[i] = 1.0 / sVals[i];
				else sVals[i] = 0;
			}
			else{
				if (sVals[i] < -0.0001)
					sVals[i] = 1.0 / sVals[i];
				else sVals[i] = 0;
			}
		}
		double[][] smrx= new double[v.getNumRows()][u.getNumCols()];
		for(int i=0;i<sVals.length;i++)smrx[i][i]=sVals[i];
		DenseMatrix64F s= new DenseMatrix64F(smrx);
		/*DenseMatrix64F us=new DenseMatrix64F(new double[u.getNumRows()][s.getNumCols()]);
		CommonOps.mult(u,s, us);
		DenseMatrix64F usv=new DenseMatrix64F(new double[us.getNumRows()][v.getNumCols()]);
		CommonOps.mult(us,v,usv);*/
		DenseMatrix64F vsp=new DenseMatrix64F(new double[v.getNumRows()][s.getNumCols()]);
		CommonOps.mult(v,s,vsp);
		DenseMatrix64F pInv= new DenseMatrix64F(new double[vsp.getNumRows()][u.getNumCols()]);
		CommonOps.mult(vsp,u,pInv);
		DenseMatrix64F ident=new DenseMatrix64F(new double[pInv.getNumRows()][mrx.getNumCols()]);
		CommonOps.mult(pInv, mrx, ident);
		/*DenseMatrix64F pInv=v.times(s);
		pInv=pInv.times(u.transpose());
		if(transposed)
			pInv=pInv.transpose();
		multiply(pInv.getArrayLinearInterpolation(), matrix);
		return pInv.getArrayLinearInterpolation();*/
		double[] pinvData=pInv.getData();
		double[][] pinv= new double[matrix[0].length][matrix.length];
		int cols=pInv.getNumCols();
		for(int i=0;i<pinvData.length;i++){
			pinv[i/cols][i%cols]=pinvData[i];
		}
		return  pinv;
	}

	public double[][] multiply(double[][] A, double[] x){
		DenseMatrix64F a= new DenseMatrix64F(A);
		double[][] X=new double[x.length][1];
		for(int i=0;i<x.length;i++)X[i][0]=x[i];
		DenseMatrix64F vect= new DenseMatrix64F(X);
		DenseMatrix64F m= new DenseMatrix64F(new double[a.getNumRows()][vect.getNumCols()]);
		CommonOps.mult(a, vect, m);
		
		/*Matrix mrx=  new Matrix(A);
		Matrix v= new Matrix(new double[][]{x});
		mrx=mrx.times(v.transpose());
		
		return mrx.getArrayLinearInterpolation();*/
		return new double[][]{m.data};
	}

	public double[][] multiply(double[][] A, double[][] B){
		DenseMatrix64F a= new DenseMatrix64F(A);
		DenseMatrix64F b= new DenseMatrix64F(B);
		DenseMatrix64F c= new DenseMatrix64F(new double[a.getNumRows()][b.getNumCols()]);
		CommonOps.mult(a, b, c);
		
		double[][] cdat= new double[c.getNumRows()][c.getNumCols()];
		int cols=c.getNumCols();
		for(int i=0;i<c.getData().length;i++){
			cdat[i/cols][i%cols]=c.getData()[i];
		}
		return  cdat;
	}

	public double[][] reconstructOriginalWithInverse(double[][] A, double[] x){
		matrix= A;
		return multiply(pseudoInv(),x);
	}

	public void svdDecomposition(){
		/*boolean transposed=false;
		Matrix mrx= new Matrix(matrix);
		System.out.println("ORG m:"+matrix.length+", n:"+matrix[0].length);
		if(matrix.length<matrix[0].length){
			mrx=mrx.transpose();
			transposed=true;
		}
		SingularValueDecomposition svd= new SingularValueDecomposition(mrx);
		Matrix u,s,v;
		u=svd.getU();
		s=svd.getS();
		v=svd.getV();
		Matrix rec= u.times(s);
		rec=rec.times(v);
		if(transposed)rec=rec.transpose();
		System.out.println("REC m:"+rec.getArrayLinearInterpolation().length+", n:"+rec.getArrayLinearInterpolation()[0].length);*/
		return;
	}
}
