package fr.babuchon.duplicate;

import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PATH = "D:\\Images\\PRD\\Doublon_exact_scale";
    private static HashMap<Integer, Integer> duplicateStats;
    private static HashMap<Integer, Integer> differentStats;


    public static void main(String[] args) {
        duplicateStats = new HashMap<>();
        differentStats = new HashMap<>();


        File folder = new File(PATH);
        File[] files = folder.listFiles();

        LOGGER.debug("lengh : {}", files.length);

        int duplicateNumber = 0;
        int differentNumber = 0;

        for(int i = 0 ; i < files.length; i++) {
            File currentFolder = files[i];
            File[] different = currentFolder.listFiles(pathname -> {
                if(pathname.getName().equals("different")) return true;
                return false;
            });
            File[] duplicate = currentFolder.listFiles(pathname -> {
                if(pathname.getName().equals("duplicate")) return true;
                return false;
            });


            if(different == null || different[0] == null) {
                return;
            }
            if(duplicate == null || duplicate[0] == null) {
                return;
            }

            List<ImagePlus> imagesDuplicate = new ArrayList<>();
            List<ImagePlus> imagesDifferent = new ArrayList<>();

            for(File f : different[0].listFiles()) {
                imagesDifferent.add(new ImagePlus(f.getPath()));
                differentNumber++;
            }

            for(File f : duplicate[0].listFiles()) {
                imagesDuplicate.add(new ImagePlus(f.getPath()));
                duplicateNumber++;
            }

            //LOGGER.info(Arrays.toString(images.toArray()));
            // Lorsque le nombre de dupliqué n'est pas = 0 on lance la pipeline
            if(imagesDuplicate.size() != 0) {
                // Test si le format est bon
                // Chaque est-ce que {a,b} == {b,a}
                for(int j = 0; j < imagesDuplicate.size(); j++) {
                    ImagePlus i1 = imagesDuplicate.get(j);
                    //float ratio = (float)i1.getWidth() / i1.getHeight();

                    for(int x = 0; x < imagesDuplicate.size() / 2; x++) {
                        if(x == j) continue;
                        ImagePlus i2 = imagesDuplicate.get(x);
//                        float ratio2 = (float)i2.getWidth() / i2.getHeight();
//                        if(ratio != ratio2) {
//                            LOGGER.error("{} : Les images n'ont pas le même format : {}:{},{}:{}", currentFolder.getName(), i1, ratio, i2, ratio2);
//                        }
//                        ratio = ratio2;
//                        i1 = i2;
                        resizeImages(i1, i2);
                        // Voir si on crop ou pas;


                        // On calcule la distance souhaité
                        int sad = DuplicateDetection.getDistWithSAD(i1, i2);
                        if(duplicateStats.containsKey(sad)) {
                            duplicateStats.put(sad, duplicateStats.get(sad) + 1);
                        }
                        else {
                            duplicateStats.put(sad, 1);
                        }

                    }
                    // Pas fou car les redimensionnement d'au dessus impact et inversement
                    for(int x = 0; x < imagesDifferent.size(); x++) {
                        ImagePlus i2 = imagesDifferent.get(x);
                        resizeImages(i1, i2);
                        int sad = DuplicateDetection.getDistWithSAD(i1, i2);
                        if(differentStats.containsKey(sad)) {
                            differentStats.put(sad, differentStats.get(sad) + 1);
                        }
                        else {
                            differentStats.put(sad, 1);
                        }

                    }

                }
            }

        }
        LOGGER.info("------------DIFFERENT------------");
        differentStats.entrySet().stream().sorted((e,e2)->Integer.compare(e2.getValue(), e.getValue())).forEach(e->LOGGER.info("Key : {}, Value : {}", e.getKey(), e.getValue()));differentStats.entrySet().stream().sorted((e,e2)->Integer.compare(e2.getValue(), e.getValue())).forEach(e->LOGGER.info("Key : {}, Value : {}", e.getKey(), e.getValue()));
        LOGGER.info("------------DUPLICATE------------");
        duplicateStats.entrySet().stream().sorted((e,e2)->Integer.compare(e2.getValue(), e.getValue())).forEach(e->LOGGER.info("Key : {}, Value : {}", e.getKey(), e.getValue()));differentStats.entrySet().stream().sorted((e,e2)->Integer.compare(e2.getValue(), e.getValue())).forEach(e->LOGGER.info("Key : {}, Value : {}", e.getKey(), e.getValue()));
        LOGGER.info("Duplicate : {}, Different : {}", duplicateNumber, differentNumber);

        /*File file = new File(PATH);

        for(int i = 0 ; i < 30; i++) {
            File f = new File(PATH, "" + i);
            f.mkdir();
            LOGGER.info(PATH + "\\" + i);
            File same = new File(PATH + "\\" +  i, "duplicate");
            same.mkdir();
            LOGGER.info(PATH + "\\" + i + "\\duplicate");
            File different = new File(PATH + "\\" + i, "different");
            different.mkdir();
            LOGGER.info(PATH + "\\" + i + "\\different");
        }*/



    }

    public static void resizeImages(ImagePlus i1, ImagePlus i2) {
        ImageConverter ic1 = new ImageConverter(i1);
        ImageConverter ic2 = new ImageConverter(i2);

        ic1.convertToGray8();
        ic2.convertToGray8();

        ImageProcessor ip1 = i1.getProcessor();
        ImageProcessor ip2 = i2.getProcessor();

        // DETECTION METHOD à voir pour changer
        ip1.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip2.setInterpolationMethod(ImageProcessor.BILINEAR);

        // On resize sur la plus petite image car il faut mieux perdre de l'information que d'en créer
        // Voir pour ne pas redimensioner l'image original car ça impact les résultats
        if(i1.getHeight() < i2.getHeight())
            i2.setProcessor(ip2.resize(i1.getWidth(), i1.getHeight()));
        else
            i1.setProcessor(ip1.resize(i2.getWidth(), i2.getHeight()));
    }
}
