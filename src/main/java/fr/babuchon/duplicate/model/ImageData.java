package fr.babuchon.duplicate.model;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ImageData {

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageData.class);

    /**
     * The image file
     */
    private File imageFile;

    /**
     * The width of the image
     */
    private int width = -1;

    /**
     * The height of the image
     */
    private int height = -1;

    /**
     * The copyright of the image
     */
    private String copyright = "";

    /**
     * Constructor, init attributes and extract metadatas
     * @param file : The image file
     * @throws IOException
     */
    public ImageData(File file) throws IOException{
        this.imageFile = file;

        try {
            InputStream stream = new FileInputStream(file);
            FileType fileType = FileTypeDetector.detectFileType(new BufferedInputStream(stream));

            // Reading image dimension
            if (fileType == FileType.Unknown)
                return;
            Metadata metadata = ImageMetadataReader.readMetadata(file);

            JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            IptcDirectory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
            ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            // Reading resolution
            if(jpegDirectory != null) {
                this.width = jpegDirectory.getImageWidth();
                this.height = jpegDirectory.getImageHeight();
            }

            // Reading Image Copyright
            if(exifDirectory != null) {
                this.copyright = exifDirectory.getString(ExifIFD0Directory.TAG_COPYRIGHT);
                if (this.copyright == null) {
                    this.copyright = "";
                }
            }
            else if(iptcDirectory != null) {
                this.copyright = iptcDirectory.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE);
                if (this.copyright == null) {
                    this.copyright = "";
                }
            }
        } catch (MetadataException | ImageProcessingException e) {
            LOGGER.error("Erreur :", e);
        }
    }

    /**
     * Return the Width of the image
     * @return int : The Width of the image, -1 if can't extract it
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the Height of the image
     * @return int : The height of the image, -1 if can't extract it
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the copyright of the image
     * @return The copyright, "" if no copyright
     */
    public String getCopyright() {
        return copyright;
    }

}
