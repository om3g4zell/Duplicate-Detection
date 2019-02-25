import fr.babuchon.duplicate.model.ImageData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class AppTest {

    @Test
    public void TestMedatada() {

        assertThrows(FileNotFoundException.class, () -> new ImageData(new File("Test invalide file")));
        ImageData id2 = null;
        ImageData id1 = null;
        try {
            id2 = new ImageData(new File("testsRes/Tele loisir_3.jpg"));
            id1 = new ImageData(new File("testsRes/Le Figaro_2575626.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        // On test d'abord si c'est pas null
        assertNotNull(id1);
        assertNotNull(id2);

        // On vérifie les copyrights
        assertEquals("", id1.getCopyright());
        assertEquals("Universal Pictures", id2.getCopyright());

        // On vérifie les dimensions
        assertEquals(1302, id1.getWidth());
        assertEquals(1772, id1.getHeight());

        assertEquals(1280, id2.getWidth());
        assertEquals(720, id2.getHeight());
    }
}
