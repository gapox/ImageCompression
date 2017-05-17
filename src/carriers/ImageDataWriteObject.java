package carriers;

import java.io.Serializable;

/**
 * Created by Gasper on 16.4.2017.
 */
public class ImageDataWriteObject implements Serializable {
    public int sizeX;
    public int sizeY;
    public boolean downsampledRegressions;
    public boolean downsampledChromaChannel;
    public String dateCreated;
    public double chromaSim;
    public double lumaSim;

    public byte[][] dataCh0;
    public byte[][] dataCh1;
    public byte[][] dataCh2;
}
