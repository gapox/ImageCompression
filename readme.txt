Image compression

This is a compression algorithm using linear regression. The idea behind it is that a lot of the images taken are flat.
You can try using the DCT approach on these kinds of signals, but you will have to save a lot of data for an image that way.
This project does not to be better than the modern approaches to image compression, it only shows a different idea of
lossy compression that does the job. But the truth of the matter is, that this algorithm is better on flat images and
videos like cartoons and images of the sky.

Basic idea of compression:

Load image -> RGBtoYCbCr -> Sectorize image -> Fit linear regressions -> save coefficients of linear regression lines fitted.

Basic idea of decompression

Load data -> calculate sectors -> fetch coefficients of linear regressions in sector -> try to reconstruct the original sector -> reatach the sectors to form the original image.





The details of each of these steps will be explained in the future. Right now I am still developing on the idea.

