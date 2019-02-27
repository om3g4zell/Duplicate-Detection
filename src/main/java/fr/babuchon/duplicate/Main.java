package fr.babuchon.duplicate;

import fr.babuchon.duplicate.duplicate.DuplicateDetection;
import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PATH = "D:\\Images\\PRD\\Doublon_exact_scale";
    private static final boolean CROP = true;
    private static final boolean NORMALIZED = true;
    private static final String METHOD = "ncc";
    private static final String STATS_PATH = "res/stats_" + System.currentTimeMillis() + "_" + METHOD + "_" + NORMALIZED + "_" + CROP + ".txt";
    private static final int[] POW_2 = {16, 32, 64, 128, 256};
    private static HashMap<Double, Integer> duplicateStats;
    private static HashMap<Double, Integer> differentStats;


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        duplicateStats = new HashMap<>();
        differentStats = new HashMap<>();


        File folder = new File(PATH);
        File[] files = folder.listFiles();

        LOGGER.debug("lengh : {}", files.length);

        int duplicateNumber = 0;
        int differentNumber = 0;

        for (int i = 0; i < files.length; i++) {
            File currentFolder = files[i];
            System.out.println("-----------------" + currentFolder.getName() + "-----------------");
            File[] different = currentFolder.listFiles(pathname -> {
                if (pathname.getName().equals("different")) return true;
                return false;
            });
            File[] duplicate = currentFolder.listFiles(pathname -> {
                if (pathname.getName().equals("duplicate")) return true;
                return false;
            });


            if (different == null || different[0] == null) {
                return;
            }
            if (duplicate == null || duplicate[0] == null) {
                return;
            }

            List<ImagePlus> imagesDuplicate = new ArrayList<>();
            List<ImagePlus> imagesDifferent = new ArrayList<>();

            for (File f : different[0].listFiles()) {
                imagesDifferent.add(new ImagePlus(f.getPath()));
                differentNumber++;
            }

            for (File f : duplicate[0].listFiles()) {
                imagesDuplicate.add(new ImagePlus(f.getPath()));
                duplicateNumber++;
            }

            // Lorsque le nombre de dupliqué n'est pas = 0 on lance la pipeline
            if (imagesDuplicate.size() != 0) {
                // Test si le format est bon
                // Chaque est-ce que {a,b} == {b,a}
                for (int j = 0; j < imagesDuplicate.size(); j++) {
                    ImagePlus i1 = imagesDuplicate.get(j);

                    for (int x = 0; x < imagesDuplicate.size(); x++) {
                        if (x <= j) continue;
                        ImagePlus i2 = imagesDuplicate.get(x);

                        ImagePlus[] computed = resizeImages(i1, i2, CROP);

                        ImagePlus i1_computed = computed[0];
                        ImagePlus i2_computed = computed[1];

                        // On calcule la distance souhaité
                        double dist = DuplicateDetection.getDist(i1_computed, i2_computed, NORMALIZED, METHOD);


                        LOGGER.debug("Duplicate : " + i1_computed.getTitle() + " / " + i2_computed.getTitle() + " " + METHOD +" : " + dist);
                        if (duplicateStats.containsKey(dist)) {
                            duplicateStats.put(dist, duplicateStats.get(dist) + 1);
                        } else {
                            duplicateStats.put(dist, 1);
                        }

                    }

                    for (int x = 0; x < imagesDifferent.size(); x++) {
                        ImagePlus i2 = imagesDifferent.get(x);

                        ImagePlus[] computed = resizeImages(i1, i2, CROP);
                        ImagePlus i1_computed = computed[0];
                        ImagePlus i2_computed = computed[1];

                        double dist = DuplicateDetection.getDist(i1_computed, i2_computed, NORMALIZED, METHOD);
                        LOGGER.debug("Different : " + i1_computed.getTitle() + " / " + i2_computed.getTitle() + " " + METHOD +" : " + dist);
                        if (differentStats.containsKey(dist)) {
                            differentStats.put(dist, differentStats.get(dist) + 1);
                        } else {
                            differentStats.put(dist, 1);
                        }

                    }

                }
            }

        }
        LOGGER.info("Duplicates : {} / Differents : {}", duplicateNumber, differentNumber);
        try (final PrintWriter pw = new PrintWriter(new File(STATS_PATH))) {
            duplicateStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEachOrdered(entry -> pw.println(entry.getKey() + ";" + entry.getValue()));
            pw.println();
            differentStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEachOrdered(entry -> pw.println(entry.getKey() + ";" + entry.getValue()));
        } catch (Exception e) {
            LOGGER.error("Aie", e);
        }
        LOGGER.info("COMPUTED IN : " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * Resize and crop the images
     *
     * @param i1   : The image 1
     * @param i2   : The 2 image
     * @param crop : IF True we crop the images following the max defined power of 2
     * @return The 2 images, image1 and image2 at index 0 and 1
     */
    public static ImagePlus[] resizeImages(ImagePlus i1, ImagePlus i2, boolean crop) {

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
