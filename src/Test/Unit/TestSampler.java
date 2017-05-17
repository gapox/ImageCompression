package Test.Unit;

import org.junit.Test;
import shifters.Sampler;

/**
 * Created by Gasper on 21.4.2017.
 */
public class TestSampler {

    Sampler s = new Sampler();

    @Test
    public void testDownsamplingLengths(){
        for(int i=1;i<1024;i++){
            System.out.println(i+" "+s.downsampleLengthForLength(i));
        }
    }
}
