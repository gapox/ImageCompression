package Test.Unit;

import math.Regression;
import math.RegressionMerge;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Gasper on 1.7.2017.
 */
public class TestRegressions {
	Regression reg = new Regression();
	RegressionMerge regMg= new RegressionMerge();
	Random rnd= new Random(System.currentTimeMillis());
	@Test
	public void shouldBeEqualRegressions(){
		int max=20;
		for(int n=4;n<max;n++){
			for(int k=2;k<n-1;k++){
				double[] f= new double[n];
				for(int i=0;i<n;i++){
					f[i]=5*rnd.nextDouble();
				}
				double[] r=reg.getRegression(f);
				double[] f1=new double[n-k];
				double[] f2= new double[k];
				for(int i=0;i<n-k;i++)
					f1[i]=f[i];
				for(int i=n-k;i<n;i++)
					f2[i-n+k]=f[i];
				double[] r1=reg.getRegression(f1);
				double[] r2=reg.getRegression(f2);
				double[] rr=regMg.getMergedRegressions(r1, n-k, r2, k);
				if(Math.abs(rr[1]-r[1])>0.0005){
					Assert.fail("The values are different:"+rr[1]+",	"+r[1]);
				}
			}
		}

	}


	@Test
	public void shouldGetSmallerRegressions(){
		int max=20;
		for(int n=2;n<max;n++){
			double[] f= new double[n];
			double[] f1=new double[n-1];
			double[] f2= new double[n-1];

			for(int i=0;i<n;i++)
				f[i] = 5 * rnd.nextDouble();
			for(int i=0;i<n-1;i++)
				f1[i]=f[i];
			for(int i=0;i<n-1;i++)
				f2[i]=f[i+1];


			double[] fullReg=reg.getRegression(f);
			double[] lReg=reg.getRegression(f2);
			double[] rReg=reg.getRegression(f1);
			double[] lRem=regMg.removeLeftMostValFromReg(fullReg, n, f[0]);
			double[] rRem=regMg.removeRightMostValFromReg(fullReg, n, f[n-1]);

			if(Math.abs(lRem[1]-lReg[1])>0.0005){
				Assert.fail("The values are different:"+lRem[1]+",	"+lReg[1]);
			}
			if(Math.abs(rRem[1]-rReg[1])>0.0005){
				Assert.fail("The values are different:"+lRem[1]+",	"+lReg[1]);
			}
		}

	}
}
