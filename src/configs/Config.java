package configs;

import RegressionCODEC.CoDecFrame;

/**
 * Created by Gasper on 21.4.2017.
 */
public class Config {

    public static String LOCATION_OUT = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/RECONs/";
    public static String LOCATION_IN = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/ORGs/";
    public static String LOCATION_IN_PACK = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/Randomized/Tests/";
    public static String LOCATION_OUT_PACK = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/Randomized/Results/";
    public static String LOCATION_IN_VIDEO = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/VIDEO/ORGs";
    public static String LOCATION_OUTVIDEO = "C:/Users/Gasper/Desktop/Projekti/javaSE/ImageCompression/VIDEO/RECONs";
    public static String FILE_IN_NAME = "theRoom.png";
    public static String RECONSTRUCTION_NAME = "completePicture/1-RGB-RECONSTRUCTION.png";
    public static String BINARY_NAME = "compressed.gpeg";
    public static String COPYRIGHT_MESSAGE="\"created by: Gasper Primozic, 2017.\\n Copyright, don't fuck with it.\"";

    public static double LUMA_SIMILARITY = 0.2202;
    public static double CHROMA_SIMILARITY = 0.3*LUMA_SIMILARITY;
    public static boolean ADD_NOISE = true;
    public static boolean SAVE_STEPS = false;
    public static boolean SAVE_DIFF = true;
    public static int MINIMAL_SECTOR_SIZE = 4;
    public static boolean SAVE_HISTS = false;
    //public static final boolean SAVE_COMPRESSED = false;
    public static  boolean USE_TRANSPOSE_AS_INVERSE = true;
    public static  boolean DOWN_SAMPLE_REGRESSIONS = true;
    public static boolean DOWN_SAMPLE_CHROMA_FIELDS = true;
    public static boolean PRINT_STATS=true;
    public static int MAX_CONSEC_VIDEO_STILLS=20;
    public static int MAX_PINV_CELLS = 4096;
    // tells you the maximum size of a
    // block that is suitable for reconstruction
    // using the Moore ponrose pseudoinverse
    // of the transformational matrix

    public static int MAX_TR_INV_CELLS = 4096;
    // tells you the maximum size of a
    // block that is suitable for reconstruction
    // using the transposed transformational
    // matrix


    //LOG CONFIG

    public static boolean LOG_FUNC=true;
    public static boolean LOG_MATH=true;
    public static boolean LOG_READ=true;
    public static boolean LOG_WRITE=true;
    public static boolean LOG_SIZE=true;
	public static boolean LOG_TIME=true;
	public static boolean LOG_TESTS=true;

    public static boolean WRITE_LOGS=true;
    public static boolean WRITE_WARNS=true;



}
