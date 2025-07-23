package me.wh4i3.turbine.utils;

import org.lwjgl.BufferUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.nio.ByteBuffer;

public class ImageUtils {
	public static ByteBuffer imageToBytes(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] data = new int[width * height];
		image.getRGB(0, 0, width, height, data, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = data[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
				buffer.put((byte) (pixel & 0xFF));        // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
			}
		}
		return buffer.flip();
	}
	public static BufferedImage convertToARGB(BufferedImage image)
	{
		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	public static BufferedImage createFlipped(BufferedImage image)
	{
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}

	public static BufferedImage createRotated(BufferedImage image)
	{
		AffineTransform at = AffineTransform.getRotateInstance(
				Math.PI, image.getWidth()/2, image.getHeight()/2.0);
		return createTransformed(image, at);
	}

	public static BufferedImage createTransformed(
			BufferedImage image, AffineTransform at)
	{
		BufferedImage newImage = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	public static BufferedImage createInverted(BufferedImage image)
	{
		if (image.getType() != BufferedImage.TYPE_INT_ARGB)
		{
			image = convertToARGB(image);
		}
		LookupTable lookup = new LookupTable(0, 4)
		{
			@Override
			public int[] lookupPixel(int[] src, int[] dest)
			{
				dest[0] = (int)(255-src[0]);
				dest[1] = (int)(255-src[1]);
				dest[2] = (int)(255-src[2]);
				return dest;
			}
		};
		LookupOp op = new LookupOp(lookup, new RenderingHints(null));
		return op.filter(image, null);
	}

	public static Component createComponent(
			String title, BufferedImage image)
	{
		JLabel label = new JLabel(new ImageIcon(image));
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.add(label);
		panel.setBorder(BorderFactory.createTitledBorder(title));
		return panel;
	}
}
