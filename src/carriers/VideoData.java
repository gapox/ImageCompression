package carriers;

import configs.Config;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gasper on 14.5.2017.
 */
public class VideoData {
    List<ImageData> stills;
    public VideoData(){
        stills=new ArrayList<>();
    }
    public boolean addStill(ImageData still)throws LimitExceededException{
        if(stills.size()+1>Config.MAX_CONSEC_VIDEO_STILLS)
            throw new LimitExceededException("The limit of consecutive stills exceeded! Current number of stills:"+stills.size());
        stills.add(still);
        if(stills.size()==Config.MAX_CONSEC_VIDEO_STILLS)
            return false;
        return true;
    }
    public String copyright= Config.COPYRIGHT_MESSAGE;
}
