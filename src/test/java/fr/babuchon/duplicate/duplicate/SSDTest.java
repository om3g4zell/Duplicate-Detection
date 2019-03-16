package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SSDTest {

    private ImagePlus different1;
    private ImagePlus different2;

    private ImagePlus duplicate1;
    private ImagePlus duplicate2;

    private ImagePlus i1Resized;
    private ImagePlus i2Resized;

    private DuplicateMethod method;

    @BeforeEach
    void setUp() {
        different1 = new ImagePlus(getClass().getResource("/Le_Figaro_2575626.jpg").getFile());
        different2 = new ImagePlus(getClass().getResource("/Tele_loisir_3.jpg").getFile());

        duplicate1 = new ImagePlus(getClass().getResource("/CeSoirTv_7.jpg").getFile());
        duplicate2 = new ImagePlus(getClass().getResource("/Le_Figaro_7341195.jpg").getFile());


        method = new SSD();
    }

    @Test
    void getDist() {

        assertNotNull(different1);
        assertNotNull(different2);

        assertNotNull(duplicate1);
        assertNotNull(duplicate2);

        assertNotNull(method);

        // -------- Test with cropped false --------
        ImagePlus computed[] = method.resizeImages(different1, different2, false);
        i1Resized = computed[0];
        i2Resized = computed[1];

        assertTrue(method.getDist(i1Resized, i2Resized, true) <= 1);
        assertTrue(method.getDist(i1Resized, i2Resized, true) >= 0);

        computed = method.resizeImages(duplicate1, duplicate2, false);
        i1Resized = computed[0];
        i2Resized = computed[1];

        assertTrue(method.getDist(i1Resized, i2Resized, true) <= 1);
        assertTrue(method.getDist(i1Resized, i2Resized, true) >= 0);

        // -------- Test with cropped false and different --------
        computed = method.resizeImages(different1, different2, true);
        i1Resized = computed[0];
        i2Resized = computed[1];

        assertTrue(method.getDist(i1Resized, i2Resized, true) <= 1);
        assertTrue(method.getDist(i1Resized, i2Resized, true) >= 0);

        computed = method.resizeImages(duplicate1, duplicate2, true);
        i1Resized = computed[0];
        i2Resized = computed[1];

        assertTrue(method.getDist(i1Resized, i2Resized, true) <= 1);
        assertTrue(method.getDist(i1Resized, i2Resized, true) >= 0);

    }
}