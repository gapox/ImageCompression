package math;

public class Subsectors {
	/*
	 * Quadrants
	 * 1|0
	 * ---
	 * 2|3
	 * 
	 */
	private int[] getCuts(int x, int y) {
		int[] ret = new int[2];
		ret[0] = x / 2;
		ret[1] = y / 2;
		return ret;
	}

	public int[][] getSubsectors(int xs, int ys, int xe, int ye) {
		int[][] subs = new int[4][4];
		int[] cuts = getCuts(xe - xs, ye - ys);

		subs[1][0] = xs;
		subs[1][1] = xs + cuts[0];
		subs[1][2] = ys;
		subs[1][3] = ys + cuts[1];

		subs[0][0] = subs[1][1];
		subs[0][1] = xe;
		subs[0][2] = subs[1][2];
		subs[0][3] = subs[1][3];

		subs[2][0] = subs[1][0];
		subs[2][1] = subs[1][1];
		subs[2][2] = subs[1][3];
		subs[2][3] = ye;

		subs[3][0] = subs[1][1];
		subs[3][1] = xe;
		subs[3][2] = subs[1][3];
		subs[3][3] = ye;

		return subs;
	}

	public int[][] getSubsectors(int[] dims) {
		return getSubsectors(dims[0], dims[2], dims[1], dims[3]);
	}
}
