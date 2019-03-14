package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateMethodTest {

    private ImagePlus i1;
    private ImagePlus i2;

    @BeforeEach
    void setUp() {
        i1 = new ImagePlus(getClass().getResource("/Le_Figaro_2575626.jpg").getFile());
        i2 = new ImagePlus(getClass().getResource("/Tele_loisir_3.jpg").getFile());
    }

    @Test
    void resizeImages() {
    }
}