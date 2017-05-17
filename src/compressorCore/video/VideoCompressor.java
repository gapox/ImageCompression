package compressorCore.video;

import RegressionCODEC.CoDecFrame;
import carriers.ImageData;
import carriers.VideoData;
import compressorCore.image.CompressRegression;
import configs.Config;
import general.IO.FileSystemManagement;
import general.IO.OpenImg;
import shifters.ColorSpaceShifter;

import javax.naming.ConfigurationException;
import javax.naming.LimitExceededException;
import java.io.IOException;
import java.util.List;


public class VideoCompressor {
	public void start() throws IOException, ConfigurationException {
		if(Config.MAX_CONSEC_VIDEO_STILLS<=0){
			throw new ConfigurationException("The field is set to zero or less. MAX_CONSEC_VIDEO_STILLS:"+Config.MAX_CONSEC_VIDEO_STILLS);
		}
		CoDecFrame coDecFrame= new CoDecFrame();
		List<String> stills= FileSystemManagement.getEntriesInDirectory(Config.LOCATION_IN_VIDEO);
		VideoData vData= new VideoData();
		for(String fName :stills){
			ImageData stillData=coDecFrame.openAndCompressStdImg(Config.LOCATION_IN_VIDEO+"/"+fName);
			try{
				boolean canAddAnother=vData.addStill(stillData);
				if(!canAddAnother){

				}
			}
			catch (LimitExceededException e){
				//save chunk
			}
		}
	}
	private void saveChunk(VideoData vData){

	}
}
