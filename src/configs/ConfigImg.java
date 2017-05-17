package configs;

/**
 * Created by Gasper on 21.4.2017.
 */
public class ConfigImg {
    static boolean DOWNSAMPLED_REGRESSIONS;
    static boolean DOWNSAMPLED_CHROMA_CHANNELS;

    public static boolean isDownsampledRegressions() {
        return DOWNSAMPLED_REGRESSIONS;
    }

    public static void setDownsampledRegressions(boolean downsampledRegressions) {
        DOWNSAMPLED_REGRESSIONS = downsampledRegressions;
    }

    public static boolean isDownsampledChromaChannels() {
        return DOWNSAMPLED_CHROMA_CHANNELS;
    }

    public static void setDownsampledChromaChannels(boolean downsampledChromaChannels) {
        DOWNSAMPLED_CHROMA_CHANNELS = downsampledChromaChannels;
    }
}
