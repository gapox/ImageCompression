package carriers;

import configs.Config;
import configs.ConfigImg;

import java.util.ArrayList;

/**
 * Created by Gasper on 15.4.2017.
 */
public class ImageData {
    public ArrayList[][] infos;
    public int x,y;
    public ConfigImg configImg = new ConfigImg();

    public ImageData(ArrayList[][] infos, int x, int y, boolean dsChroma, boolean dsRegs){
        this.infos=infos;
        this.x=x;
        this.y=y;
        configImg.setDownsampledChromaChannels(dsChroma);
        configImg.setDownsampledRegressions(dsRegs);
    }
    public String copyright= Config.COPYRIGHT_MESSAGE;
}
