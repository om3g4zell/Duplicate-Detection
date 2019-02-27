package fr.babuchon.duplicate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ImageDataTest {

    private ImageData id2;
    private ImageData id1;

    @BeforeEach
    void setUp() {
        try {
            id2 = new ImageData(new File(getClass().getResource("/Tele_loisir_3.jpg").toURI()));
            id1 = new ImageData(new File(getClass().getResource("/Le_Figaro_2575626.jpg").toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getWidth() {
        assertNotNull(id1);
        assertNotNull(id2);

        assertEquals(1302, id1.getWidth());
        assertEquals(1280, id2.getWidth());
    }

    @Test
    void getHeight() {
        assertNotNull(id1);
        assertNotNull(id2);

        assertEquals(720, id2.getHeight());
        assertEquals(1772, id1.getHeight());
    }

    @Test
    void getCopyright() {
        assertNotNull(id1);
        assertNotNull(id2);

        assertEquals("", id1.getCopyright());
        assertEquals("Universal Pictures", id2.getCopyright());
    }

}