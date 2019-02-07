package fr.babuchon.duplicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PATH = "D:\\Images\\PRD\\Doublon_exact_scale";
    public static void main(String[] args) {

        DuplicateDetection dd = new DuplicateDetection();

        File folder = new File(PATH);
        File files[] = folder.listFiles();

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
            for(File f : different[0].listFiles()) {
                differentNumber++;
                LOGGER.debug(f.getName());
            }
            for(File f : duplicate[0].listFiles()) {
                LOGGER.debug(f.getName());
                duplicateNumber++;
            }
        }

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
}
