package fr.babuchon.duplicate.duplicate;

import ij.ImagePlus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateMethodTest {

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


        method = new NCC();
    }

    @Test
    void resizeImages() {
        assertNotNull(different1);
        assertNotNull(different2);

        assertNotNull(duplicate1);
        assertNotNull(duplicate2);

        assertNotNull(method);

        // -------- Test with cropped false and different --------
        ImagePlus computed[] = method.resizeImages(different1, different2, false);
        i1Resized = computed[0];
        i2Resized = computed[1];

        // On test si l'image est bien en grayscale
        assertEquals(i1Resized.getType(), ImagePlus.GRAY8);
        assertEquals(i2Resized.getType(), ImagePlus.GRAY8);

        // On test si les tailles sont bien egales
        assertEquals(i1Resized.getWidth(), i2Resized.getWidth());
        assertEquals(i1Resized.getHeight(), i2Resized.getHeight());


        // -------- Test with cropped false and duplicate --------
        computed = method.resizeImages(different1, different2, true);
        i1Resized = computed[0];
        i2Resized = computed[1];

        // On test si l'image est bien en grayscale
        assertEquals(i1Resized.getType(), ImagePlus.GRAY8);
        assertEquals(i2Resized.getType(), ImagePlus.GRAY8);

        // On test si les tailles sont bien egales
        assertEquals(i1Resized.getWidth(), i2Resized.getWidth());
        assertEquals(i1Resized.getHeight(), i2Resized.getHeight());

        assertEquals(i1Resized.getHeight(), i2Resized.getWidth());

        int height = i1Resized.getHeight();

        assertTrue(height == 2 || height == 4 || height == 8 || height == 16 || height == 32 || height == 64 || height == 128 || height == 256);

        // -------- Test with cropped true and different --------
        computed = method.resizeImages(duplicate1, duplicate2, false);
        i1Resized = computed[0];
        i2Resized = computed[1];

        // On test si l'image est bien en grayscale
        assertEquals(i1Resized.getType(), ImagePlus.GRAY8);
        assertEquals(i2Resized.getType(), ImagePlus.GRAY8);

        // On test si les tailles sont bien egales
        assertEquals(i1Resized.getWidth(), i2Resized.getWidth());
        assertEquals(i1Resized.getHeight(), i2Resized.getHeight());

        // -------- Test with cropped true and duplicate --------
        computed = method.resizeImages(duplicate1, duplicate2, true);
        i1Resized = computed[0];
        i2Resized = computed[1];

        // On test si l'image est bien en grayscale
        assertEquals(i1Resized.getType(), ImagePlus.GRAY8);
        assertEquals(i2Resized.getType(), ImagePlus.GRAY8);

        // On test si les tailles sont bien egales
        assertEquals(i1Resized.getWidth(), i2Resized.getWidth());
        assertEquals(i1Resized.getHeight(), i2Resized.getHeight());

        assertEquals(i1Resized.getHeight(), i2Resized.getWidth());

        height = i1Resized.getHeight();

        assertTrue(height == 2 || height == 4 || height == 8 || height == 16 || height == 32 || height == 64 || height == 128 || height == 256);

    }
}