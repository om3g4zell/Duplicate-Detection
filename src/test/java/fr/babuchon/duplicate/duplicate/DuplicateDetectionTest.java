package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateDetectionTest {

    private ImagePlus i1;
    private ImagePlus i2;

    @BeforeEach
    void setUp() {
        // ImagePlus and GetResource is incompatible with space in the name
        i1 = new ImagePlus(getClass().getResource("/Le_Figaro_2575626.jpg").getFile());
        i2 = new ImagePlus(getClass().getResource("/Tele_loisir_3.jpg").getFile());
    }

    @Test
    void getDist() {
        assertNotNull(i1);
        assertNotNull(i2);

        //assertThrows(IllegalArgumentException.class, () -> DuplicateDetection.getDist(i1, i2, true,"ncc"));
    }
}