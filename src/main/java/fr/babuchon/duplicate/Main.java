package fr.babuchon.duplicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String PATH = "D:\\Images\\PRD\\Doublon_exact_scale";
    public static void main(String[] args) {
        
        System.out.println("Hello World");
        LOGGER.info("Test logger");

        File file = new File(PATH);

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
        }

    }
}
