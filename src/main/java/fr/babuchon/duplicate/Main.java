package fr.babuchon.duplicate;

import fr.babuchon.duplicate.duplicate.*;
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
    private static DuplicateMethod method;
    private static ArrayList<Double> differentResults;
    private static ArrayList<Double> duplicateResults;



    public static void main(String[] args) {
        if(METHOD.equals("ncc")) {
            method = new NCC();
        }
        else if(METHOD.equals("sad")) {
            method = new SAD();
        }
        else if(METHOD.equals("ssd")) {
            method = new SSD();
        }
        else {
            LOGGER.error("Invalid method");
            return;
        }


        long start = System.currentTimeMillis();

        duplicateStats = new HashMap<>();
        differentStats = new HashMap<>();

        differentResults = new ArrayList<>();
        duplicateResults = new ArrayList<>();


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

                        ImagePlus[] computed = method.resizeImages(i1, i2, CROP);

                        ImagePlus i1_computed = computed[0];
                        ImagePlus i2_computed = computed[1];

                        // On calcule la distance souhaité
                        double dist = method.getDist(i1_computed, i2_computed, NORMALIZED);


                        LOGGER.debug("Duplicate : " + i1_computed.getTitle() + " / " + i2_computed.getTitle() + " " + METHOD +" : " + dist);
                        if (duplicateStats.containsKey(dist)) {
                            duplicateStats.put(dist, duplicateStats.get(dist) + 1);
                        } else {
                            duplicateStats.put(dist, 1);
                        }
                        duplicateResults.add(dist);

                    }

                    for (int x = 0; x < imagesDifferent.size(); x++) {
                        ImagePlus i2 = imagesDifferent.get(x);

                        ImagePlus[] computed = method.resizeImages(i1, i2, CROP);
                        ImagePlus i1_computed = computed[0];
                        ImagePlus i2_computed = computed[1];

                        double dist = method.getDist(i1_computed, i2_computed, NORMALIZED);
                        LOGGER.debug("Different : " + i1_computed.getTitle() + " / " + i2_computed.getTitle() + " " + METHOD +" : " + dist);
                        if (differentStats.containsKey(dist)) {
                            differentStats.put(dist, differentStats.get(dist) + 1);
                        } else {
                            differentStats.put(dist, 1);
                        }
                        differentResults.add(dist);

                    }

                }
            }

        }
        LOGGER.info("Duplicates : {} / Differents : {}", duplicateNumber, differentNumber);
        try (final PrintWriter pw = new PrintWriter(new File(STATS_PATH))) {
            //duplicateStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEachOrdered(entry -> pw.println(entry.getKey() + ";" + entry.getValue()));
            duplicateResults.stream().sorted().forEachOrdered(entry -> pw.println(entry));
            pw.println();
            //differentStats.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEachOrdered(entry -> pw.println(entry.getKey() + ";" + entry.getValue()));
            differentResults.stream().sorted().forEachOrdered(entry -> pw.println(entry));
        } catch (Exception e) {
            LOGGER.error("Aie", e);
        }
        LOGGER.info("COMPUTED IN : " + (System.currentTimeMillis() - start) + " ms");
    }
}
