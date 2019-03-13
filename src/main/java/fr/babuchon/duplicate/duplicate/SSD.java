package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represent the SSD method
 */
public class SSD implements DuplicateMethod{

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SSD.class);

    @Override
    public double getDist(ImagePlus image1, ImagePlus image2, boolean normalized) throws IllegalArgumentException {
        int width1 = image1.getWidth();
        int height1 = image1.getHeight();

        int width2 = image2.getWidth();
        int height2 = image2.getHeight();

        LOGGER.debug("Width : {}; Height : {} / Width : {}; Height : {}", width1, height1, width2, height2);

        if(image1.getType() != ImagePlus.GRAY8 || image2.getType() != ImagePlus.GRAY8) {
            throw new IllegalArgumentException("Erreur les images doivent être en noir et blanc");
        }

        if(width1 != width2 || height1 != height2) {
            throw new IllegalArgumentException("Erreur les images doivent avoir les mêmes dimensions");
        }

        int dist = 0;
        for(int i = 0 ; i < width1; i++) {
            for(int j = 0 ; j < height1; j++) {
                int pixel1 = image1.getPixel(i, j)[0];
                int pixel2 = image2.getPixel(i, j)[0];

                dist += Math.pow(pixel1 - pixel2, 2);

            }
        }

        if(normalized)
            return (double)(dist / (width1 * height1)) / (255f * 255f);

        return dist;
    }
}
