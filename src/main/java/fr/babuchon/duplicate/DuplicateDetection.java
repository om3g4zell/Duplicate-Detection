package fr.babuchon.duplicate;

import ij.ImagePlus;

public class DuplicateDetection {

    public static int getDistWithSAD(ImagePlus image1, ImagePlus image2) {

        int width1 = image1.getWidth();
        int height1 = image1.getHeight();

        int width2 = image2.getWidth();
        int height2 = image2.getHeight();

        if(width1 != width2 || height1 != height2) {
            throw new IllegalArgumentException("Erreur les images doivent avoir les mêmes dimensions");
        }

        int sad = 0;
        for(int i = 0 ; i < width1; i++) {
            for(int j = 0 ; j < height1; j++) {
                int pixel1 = image1.getPixel(i, j)[0];
                int pixel2 = image2.getPixel(i, j)[0];

                sad += Math.abs(pixel1 - pixel2);
            }
        }

        // sad / something normalization

        return sad;
    }

}
