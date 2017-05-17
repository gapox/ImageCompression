package math;

public final class GeneralMath {
	//ALLFUNCTIONS GHERE ARE DEPRECATED AND UNUSED!
	//THE CURRENT IMPLEMENTATION IS FOUND IN SUBSECTORS CLASS!!!
	
	
	public int[][] getSubsectionDims(int xOrg, int yOrg){//computes subSection dimentions
		int maxDepth = xOrg;
		if (yOrg < xOrg)
			maxDepth = xOrg;
		int[][] dims;
		dims = new int[(int) Math.ceil((Math.log(maxDepth) / Math.log(2))) + 1][2];
		dims[0][0] = xOrg;
		dims[0][1] = yOrg;
		// System.out.println(dims[0][0]+" "+dims[0][1]);
		for (int i = 1; i < dims.length; i++) {
			int x = dims[i - 1][0] >> 1;
			int y = dims[i - 1][1] >> 1;
			if (dims[i - 1][0] % 2 == 1)
				x++;
			if (dims[i - 1][1] % 2 == 1)
				y++;
			dims[i] = new int[] { x, y };
			// System.out.println(dims[i][0]+" "+dims[i][1]);
		}
		return dims;
	}
}
