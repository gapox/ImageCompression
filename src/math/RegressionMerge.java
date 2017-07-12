package math;

/**
 * Created by Gasper on 1.7.2017.
 */
public class RegressionMerge {
	Regression reg = new Regression();

	public double[] getMergedRegressions(double[] r1, int n1, double[] r2, int n2){
		int n=n1+n2;
		double a1,b1,a2,b2, a,b;
		double cxxAll=reg.cxsq(n);
		a1=r1[0];
		b1=r1[1];
		a2=r2[0];
		b2=r2[1];
		double f1=-(double)n2/2;
		double f2=(double)n1/2;
		double avg1=reg.getAverageOfReg(r1, n1);
		double avg2=reg.getAverageOfReg(r2, n2);
		double avg= (avg1*n1+avg2*n2)/n;
		b=getFactoredBeta(b1,f1, n1, avg1)+getFactoredBeta(b2,f2, n2, avg2);
		b/=cxxAll;
		a=avg-b*((double)(n-1))/2;

		double[] newReg=new double[3];
		newReg[0]=a;
		newReg[1]=b;
		newReg[2]=r1[2]+r2[2];
		return newReg;
	}

	private double getFactoredBeta(double beta, double factor, double n, double avg){
		double newBeta;
		double cxxN=reg.cxsq(n);
		newBeta=(beta+factor*(n/cxxN)*avg)*cxxN;

		return newBeta;
	}

	public double leftRemoveBeta(double beta, double n, double avg, double a){
		double cxxAll=reg.cxsq(n);
		double cxxNr=reg.cxsq(n-1);
		double nr=n-1;
		double betaR=(cxxAll*beta+((n-1)/2)*a)/(cxxNr)-0.5*(nr/cxxNr)*(avg*n-a)/nr;
		return betaR;
	}

	public double rightRemoveBeta(double beta, double n, double avg, double z){
		double cxxAll=reg.cxsq(n);
		double cxxNr=reg.cxsq(n-1);
		double nl=n-1;
		double betaL=(cxxAll*beta-((n-1)/2)*z)/(cxxNr)+0.5*(nl/cxxNr)*(avg*n-z)/nl;
		return betaL;
	}

	public double ithRemoveBeta(double beta, double n, double i, double avg, double z){
		double cxxAll=reg.cxsq(n);
		double cxxNr=reg.cxsq(n-1);
		double nl=n-1;
		double betaL=(cxxAll*beta+((n-1)/2)*z)/(cxxNr)+0.5*(nl/cxxNr)*(avg*n-z)/nl;
		return betaL;
	}

	public double[] removeLeftMostValFromReg(double[] r, int n, double val){
		if(n==2){
			double err=val-reg.getValAt(r, 0);
			double v=reg.getValAt(r, 1)-err;
			return new double[]{v, 0, 0};
		}
		double avg=reg.getAverageOfReg(r, n);
		double newBeta=leftRemoveBeta(r[1], n, avg, val);
		double a=(avg*n-val)/(n-1.0)-newBeta*((double)((n-1))/2);

		double[] newReg=new double[3];
		newReg[0]=a;
		newReg[1]=newBeta;
		newReg[2]=r[2];
		return newReg;
	}

	public double[] removeRightMostValFromReg(double[] r, int n, double val){
		if(n==2){
			double err=val-reg.getValAt(r, 1);
			double v=reg.getValAt(r, 0)-err;
			return new double[]{v, 0, 0};
		}
		double avg=reg.getAverageOfReg(r, n);
		double newBeta=rightRemoveBeta(r[1], n, avg, val);
		double a=(avg*n-val)/(n-1.0)-newBeta*((double)(n-1))/2;

		double[] newReg=new double[3];
		newReg[0]=a;
		newReg[1]=newBeta;
		newReg[2]=r[2];
		return newReg;
	}

}
