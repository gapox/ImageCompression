package Test.Unit;

import carriers.PseudoInverses;
import configs.Config;
import general.IO.ToImg;
import junit.framework.Assert;
import math.LinearAlgebra;
import org.junit.Test;
import peripheral.Logger;

/**
 * Created by Gasper on 25.5.2017.
 */
public class TestPseudoInverses {

	PseudoInverses pi = new PseudoInverses();
	int x=20;
	int y=10;
	@Test
	public void shouldGetAndPrintPseudoInverses(){
		double[][] mrx= pi.getMatrixToInverseAtDepth(x,y);
		double[][] mrxI= pi.getTransposedMatrixToInverseAtDepth(x,y);
		double[][] pInv=pi.getPinverseOut(x,y);
		Logger.printMrx("Matrix",mrx);
		Logger.printMrx("Pseudo-inverse",pInv);
		Logger.printMrx("Matrix transpose",mrxI);

	}

	@Test
	public void shouldMultiplyMrxAndInverse(){
		LinearAlgebra la = new LinearAlgebra();
		double[][] mrx= pi.getMatrixToInverseAtDepth(x,y);
		double[][] mrxI= pi.getTransposedMatrixToInverseAtDepth(x,y);

		double[][] pInv=pi.getPinverseOut(x,y);
		double [][] r=la.multiply(mrx,pInv);
		ToImg img = new ToImg(r,  false, true);
		img.outputBW(Config.LOCATION_OUT+"/Objects/multiplyTranspose/pInvMrx");
		r=la.multiply(mrx,mrxI);
		img = new ToImg(r, false, true);
		img.outputBW(Config.LOCATION_OUT+"/Objects/multiplyTranspose/mrxIMrx");


		img = new ToImg(pInv,  true, true);
		img.outputBW(Config.LOCATION_OUT+"/Objects/multiplyTranspose/pInv");

		img = new ToImg(mrxI, true, true);
		img.outputBW(Config.LOCATION_OUT+"/Objects/multiplyTranspose/mrxI");

		img = new ToImg(mrx, true, true);
		img.outputBW(Config.LOCATION_OUT+"/Objects/multiplyTranspose/mrx");
	}


}
