package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

/**
 * This interface represent a duplicate detection method
 */
public interface DuplicateMethod {

    final int[] POW_2 = {16, 32, 64, 128, 256};

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

    /**
     * Resize and crop the images
     *
     * @param i1   : The image 1
     * @param i2   : The 2 image
     * @param crop : IF True we crop the images following the max defined power of 2
     * @return The 2 images, image1 and image2 at index 0 and 1
     */
    default ImagePlus[] resizeImages(ImagePlus i1, ImagePlus i2, boolean crop) {

        ImageConverter ic1 = new ImageConverter(i1);
        ImageConverter ic2 = new ImageConverter(i2);

        ic1.convertToGray8();
        ic2.convertToGray8();

        ImageProcessor ip1 = i1.getProcessor();
        ImageProcessor ip2 = i2.getProcessor();

        // RESIZE METHOD à voir pour changer
        ip1.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip2.setInterpolationMethod(ImageProcessor.BILINEAR);

        // On resize sur la plus petite image car il faut mieux perdre de l'information que d'en créer
        // Voir pour ne pas redimensioner l'image original car ça impact les résultats
        if (i1.getHeight() < i2.getHeight())
            ip2 = ip2.resize(i1.getWidth(), i1.getHeight());
        else
            ip1 = ip1.resize(i2.getWidth(), i2.getHeight());

        if (crop) {
            // On recupère le plus grand crop possible suivant le tableau des puissances de 2
            int width = ip1.getWidth();
            int height = ip2.getHeight();
            int cropSize = POW_2[0];

            int index = POW_2.length - 1;
            for (int k = index; k >= 0; k--) {
                if (POW_2[k] <= width && POW_2[k] <= height) {
                    cropSize = POW_2[k];
                    break;
                }
            }

            // On effectue le crop
            ip1.setRoi((width - cropSize) / 2, (height - cropSize) / 2, cropSize, cropSize);
            ip2.setRoi((width - cropSize) / 2, (height - cropSize) / 2, cropSize, cropSize);

            ip1 = ip1.crop();
            ip2 = ip2.crop();
        }
        return new ImagePlus[]{new ImagePlus(i1.getTitle(), ip1), new ImagePlus(i2.getTitle(), ip2)};
    }
}
