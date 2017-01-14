package org.rr.mobi4java;

import static org.junit.Assert.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.io.IOUtils;

public class MobiTestUtils {
	
	static void verifyRecordIndices(MobiContentHeader mobiHeader, List<MobiContent> mobiContents) {
		assertEquals(MobiContent.CONTENT_TYPE.FCIS, mobiContents.get(mobiHeader.getFcisRecordIndex()).getType());
		assertEquals(MobiContent.CONTENT_TYPE.FLIS, mobiContents.get(mobiHeader.getFlisRecordIndex()).getType());

		if (mobiHeader.getSrcsRecordIndex() != -1) {
			assertEquals(MobiContent.CONTENT_TYPE.SRCS, mobiContents.get(mobiHeader.getSrcsRecordIndex()).getType());
		}
	}

	static MobiDocument createReader(byte[] mobiData) throws IOException {
		return readDoc(mobiData);
	}

	static byte[] getResourceData(String resource) throws IOException {
		return IOUtils.toByteArray(MobiTestUtils.class.getResourceAsStream(resource));
	}

	static MobiDocument readDoc(byte[] newMobiData) throws IOException {
		return new MobiReader().read(new ByteArrayInputStream(newMobiData));
	}

	static byte[] writeDoc(MobiDocument doc) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new MobiWriter(doc).write(out);
		return out.toByteArray();
	}
	
	static MobiDocument reReadDocument(MobiDocument doc) throws IOException {
		byte[] newMobiData = writeDoc(doc);
		MobiDocument newDoc = readDoc(newMobiData);
		return newDoc;
	}
	
	static byte[] createJpegCover(int width, int height) throws IOException {
		BufferedImage bufferedImage = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawLine(0, 0, width, height);
		g.drawLine(width, 0, 0, height);
		g.dispose();
		
		return getImageBytes(bufferedImage, "image/jpeg");
	}
	
	private static byte[] getImageBytes(final BufferedImage image, String mime) throws IOException {
		ImageWriter writer = null;
        Iterator<ImageWriter> imageWritersByFormatName = ImageIO.getImageWritersByMIMEType(mime);
        while(imageWritersByFormatName.hasNext()) {
        	ImageWriter next = imageWritersByFormatName.next();
        	if(writer == null && next.getClass().getName().equals("com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriter")) {
        		writer = next;
        		break;
        	} else {
        		writer = next;
        	}
        }

        if(writer != null) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			MemoryCacheImageOutputStream mem = new MemoryCacheImageOutputStream(output);
			writer.setOutput(mem);
			try {
				writer.write(image);
			} finally {
				try {mem.flush();} catch (Exception e) {}
				try {mem.close();} catch (Exception e) {}
				try {writer.setOutput(null);} catch(Exception e) {}
				try {writer.dispose();} catch(Exception e) {}
			}
			return output.toByteArray();
        }
        return null;
	}

}
