package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represent the cross correlation method
 */
public class NCC implements DuplicateMethod{

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NCC.class);

    @Override
    public double getDist(ImagePlus i1, ImagePlus i2, boolean normalized) throws IllegalArgumentException {
        // Version avec des images de même taille
        int width1 = i1.getWidth();
        int height1 = i1.getHeight();

        int width2 = i2.getWidth();
        int height2 = i2.getHeight();

        LOGGER.debug("Width : {}; Height : {} / Width : {}; Height : {}", width1, height1, width2, height2);

        if(i1.getType() != ImagePlus.GRAY8 || i2.getType() != ImagePlus.GRAY8) {
            throw new IllegalArgumentException("Erreur les images doivent être en noir et blanc");
        }

        if(width1 != width2 || height1 != height2) {
            throw new IllegalArgumentException("Erreur les images doivent avoir les mêmes dimensions");
        }

        double i1Mean = 0;
        double i2Mean = 0;

        for(int i = 0; i < width1; i++) {
            for(int j = 0; j< height1; j++) {
                i1Mean += i1.getPixel(i, j)[0];
            }
        }

        i1Mean /= width1 * height1;

        for(int i = 0; i < width2; i++) {
            for(int j = 0; j< height2; j++) {
                i2Mean += i2.getPixel(i, j)[0];
            }
        }

        i2Mean /= width2 * height2;

        double sum = 0;

        double sqsum1 = 0;
        double sqsum2 = 0;

        for(int x = 0; x < width1; x++) {
            for(int y = 0; y < height1; y++) {

                int pixel1 = i1.getPixel(x, y)[0];
                int pixel2 = i2.getPixel(x, y)[0];

                sum += (pixel1 - i1Mean) * (pixel2 - i2Mean);
                sqsum1 += Math.pow(pixel1 - i1Mean, 2);
                sqsum2 += Math.pow(pixel2 - i2Mean, 2);

            }
        }
        if((Double.isNaN(sum / Math.sqrt(sqsum1 * sqsum2))))
            LOGGER.error("{}//{}", sum, sqsum1*sqsum2);
        return sum / Math.sqrt(sqsum1 * sqsum2);
    }
}
