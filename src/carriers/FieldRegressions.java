package carriers;

public class FieldRegressions {
	public double[][] rX;
	public double[][] rY;
	public int xs, ys, xe, ye,  depth;
	public double var;
	public double normal;
	public FieldRegressions(int xstart,  int ystart, int xend, int yend){
		xs=xstart;
		ys=ystart;
		xe=xend;
		ye=yend;
		rX= new double[getX()][3];
		rY= new double[getY()][3];
	}
	public int getX(){
		return xe-xs;
	}
	public int getY(){
		return ye-ys;
	}
	public int getArea(){
		return getX()*getY();
	}
	public int[] getDims(){
		return new int[]{xs,xe,ys,ye};
	}
	public void downscaleRegressions(){
		for(int i=0;i<rX.length;i++){
			rX[i][1]/=((double)rY.length);
		}
		for(int i=0;i<rY.length;i++){
			rY[i][1]/=((double)rX.length);
		}
	}
	public void addNormal(double normal){
		this.normal=normal;
		for(int i=0;i<rX.length;i++){
			rX[i][0]+=normal;
		}
		for(int i=0;i<rY.length;i++){
			rY[i][0]+=normal;
		}
	}
	
}





