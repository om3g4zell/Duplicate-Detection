package fr.babuchon.duplicate;

import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuplicateDetection {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateDetection.class);

    public static double getDist(ImagePlus image1, ImagePlus image2, boolean normalized, String method) {

        int width1 = image1.getWidth();
        int height1 = image1.getHeight();

        int width2 = image2.getWidth();
        int height2 = image2.getHeight();

        LOGGER.debug("Width : {}; Height : {} / Width : {}; Height : {}", width1, height1, width2, height2);

        // TODO CHECK SI LES IMAGES SONT BIEN EN NOIR ET BLANC

        if(width1 != width2 || height1 != height2) {
            throw new IllegalArgumentException("Erreur les images doivent avoir les mêmes dimensions");
        }

        if(method.equals("ncc")) {
            return crossCorrelation(image1, image2);
        }

        int dist = 0;
        for(int i = 0 ; i < width1; i++) {
            for(int j = 0 ; j < height1; j++) {
                int pixel1 = image1.getPixel(i, j)[0];
                int pixel2 = image2.getPixel(i, j)[0];

                if(method.equals("sad"))
                    dist += Math.abs(pixel1 - pixel2);
                else if(method.equals("ssd")) {
                    dist += Math.pow(pixel1 - pixel2, 2);
                }
            }
        }

        if(normalized)
            return (double)dist / (width1 * height1);
        return dist;
    }

    private static double crossCorrelation(ImagePlus i1, ImagePlus i2) {

        // Version avec des images de même taille

        int width1 = i1.getWidth();
        int height1 = i1.getHeight();

        int width2 = i2.getWidth();
        int height2 = i2.getHeight();

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
