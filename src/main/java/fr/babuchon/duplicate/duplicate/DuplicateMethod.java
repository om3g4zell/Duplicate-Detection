package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;

/**
 * This interface represent a duplicate detection method
 */
public interface DuplicateMethod {

    /**
     *
     * @param image1 : The first image in grayScale 8
     * @param image2: The second image in grayScale 8
     * @param normalized : boolean : if we normalize the distance
     * @return The distance between the 2 images
     * @throws IllegalArgumentException : If the images don't have the same size or if they are not
     * in grayScale 8
     */
    double getDist(ImagePlus image1, ImagePlus image2, boolean normalized) throws IllegalArgumentException;
}
