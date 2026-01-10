package chap04.ex_image_pro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class Ex_Image_Pro extends JFrame {

	ImagePanel imageSrc, imageDst; // Panels to display the image source and the image destiny.
	JFileChooser fc = new JFileChooser();

	public static void main(String[] args) {
		JFrame frame = new Ex_Image_Pro();
		frame.setTitle("ImagePro 2025");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setVisible(true);
	}

	public Ex_Image_Pro() {
		// Since this application has a more complex interface, we build it in the class
		// constructor to ensure that all objects are created at the right time.

		// ===== Add panels =====
		imageSrc = new ImagePanel();
		imageDst = new ImagePanel();

		// Main layout of the application.
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		// Auxiliary panel to organize the image panels.
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(imageSrc);
		panel.add(imageDst);

		cp.add(panel, BorderLayout.CENTER);

		// ===== Add menus =====
		// Add a menu bar
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);

		// Add File menu
		JMenu menu = new JMenu("File");
		JMenuItem mi = new JMenuItem("Open image...");
		mi.addActionListener(e -> openImage());
		menu.add(mi);
		mi = new JMenuItem("Save image...");
		mi.addActionListener(e -> saveImage());
		menu.add(mi);
		mi = new JMenuItem("Copy image");
		menu.add(mi);
		mi.addActionListener(e -> copyImage());
		menu.addSeparator();
		mi = new JMenuItem("Exit");
		mi.addActionListener(e -> exit());
		menu.add(mi);
		mb.add(menu);

		// Add Process menu
		menu = new JMenu("Process");
		mi = new JMenuItem("Gray scale");
		mi.addActionListener(e -> grayScale1());
		menu.add(mi);
		mi = new JMenuItem("Gray scale 2");
		mi.addActionListener(e -> grayScale2());
		menu.add(mi);
		mi = new JMenuItem("Rescale");
		mi.addActionListener(e -> rescale());
		menu.add(mi);
		mi = new JMenuItem("Rotate");
		mi.addActionListener(e -> rotate());
		menu.add(mi);
		mi = new JMenuItem("Rotate 2");
		mi.addActionListener(e -> rotate2());
		menu.add(mi);
		mi = new JMenuItem("Smooth");
		mi.addActionListener(e -> smooth());
		menu.add(mi);
		mi = new JMenuItem("Sharpen");
		mi.addActionListener(e -> sharpen());
		menu.add(mi);
		mi = new JMenuItem("Edge");
		mi.addActionListener(e -> edge());
		menu.add(mi);
		mi = new JMenuItem("Binarization");
		mi.addActionListener(e -> binarization());
		menu.add(mi);
		mb.add(menu);

		// ===== Add Toolbar =====
		JToolBar toolBar = new JToolBar();

		// Open button
		ImageIcon openIcon = new ImageIcon(getClass().getResource("/icons/open.png"));
		JButton openBtn = new JButton(openIcon);
		openBtn.setToolTipText("Open image");
		openBtn.addActionListener(e -> openImage());
		// openBtn.addActionListener(e -> menuItem.doClick()); // if menuItem is an
		// object.

		// Save button
		ImageIcon saveIcon = new ImageIcon(getClass().getResource("/icons/save.png"));
		JButton saveBtn = new JButton(saveIcon);
		saveBtn.setToolTipText("Save image");
		saveBtn.addActionListener(e -> saveImage());

		// Add buttons to Toolbar
		toolBar.add(openBtn);
		toolBar.add(saveBtn);
		cp.add(toolBar, BorderLayout.NORTH);

		// Set home folder (example: User Documents)
		// fc.setCurrentDirectory(new File(System.getProperty("user.home") +
		// File.separator + "Images"));
	}

	// ===== File menu item functions =====
	private void saveImage() {
		int retval = fc.showSaveDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIO.write(imageDst.getImage(), "png", fc.getSelectedFile());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void openImage() {
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				BufferedImage bi = ImageIO.read(fc.getSelectedFile());
				imageSrc.setImage(bi);
				pack();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void copyImage() {
		imageSrc.setImage(imageDst.getImage());
		pack();
	}

	private void exit() {
		System.exit(0);
	}

	// ===== Process menu item functions =====
	private void grayScale1() {
		BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		BufferedImage bi = op.filter(imageSrc.getImage(), null);
		imageDst.setImage(bi);
		pack();
	}

	private void grayScale2() {
		imageDst.setImage(RGBToGray(imageSrc.getImage()));
		pack();
	}

	private void rescale() {
		// Apples a scale factor (1.5) and an offset (10) to the colors.
		// color = color x scale + offset
		BufferedImageOp op = new RescaleOp(1.5f, 10, null);
		BufferedImage bi = op.filter(imageSrc.getImage(), null);
		imageDst.setImage(bi);
		pack();
	}

	private void rotate() {
		// This version maintains the original size of the image therefore part of the
		// image is cut after rotation
		AffineTransform at = new AffineTransform();
		at.setToRotation(Math.toRadians(45), imageSrc.getWidth() / 2.0, imageSrc.getHeight() / 2.0);
		BufferedImageOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage bi = op.filter(imageSrc.getImage(), null);
		imageDst.setImage(bi);
		pack();
	}

	private void rotate2() {
		imageDst.setImage(rotateImage(imageSrc.getImage(), 30));
		pack();
	}

	private void edge() {
		float[] data = { 0f, -1f, 0f, -1f, 4f, -1f, 0f, -1f, 0f };
		imageDst.setImage(applyConvolve(3, data, imageSrc.getImage()));
		pack();
	}

	private void smooth() {
		float[] data = { 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f };
		imageDst.setImage(applyConvolve(3, data, imageSrc.getImage()));
		pack();
	}

	private void sharpen() {
		float[] data = { 0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f };
		imageDst.setImage(applyConvolve(3, data, imageSrc.getImage()));
		pack();
	}

	private void binarization() {
		imageDst.setImage(binarization(imageSrc.getImage(), 150));
		pack();
	}

	// ===== Generic implementations of some algorithms =====
	private BufferedImage applyConvolve(int s, float[] data, BufferedImage imgIn) {
		// Convolution operation
		BufferedImageOp op = null;
		Kernel ker = new Kernel(s, s, data);
		op = new ConvolveOp(ker);
		return (op.filter(imgIn, null));
	}

	private BufferedImage RGBToGray(BufferedImage imgIn) {
		// Converting pixel color to gray scale by scanning the entire image and
		// processing each pixel.

		BufferedImage imgOut = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());

		// WritableRaster object to access pixel data efficiently
		WritableRaster rasterImgIn = imgIn.getRaster();
		WritableRaster rasterImgOut = imgOut.getRaster();

		// Array to save pixel data
		int[] rgba = new int[4];

		// Loops to process all the image pixels
		for (int x = 0; x < imgIn.getWidth(); x++) {
			for (int y = 0; y < imgIn.getHeight(); y++) {
				rasterImgIn.getPixel(x, y, rgba); // Read the RGB color of pixel (x, y)

				int gray = (int) ((rgba[0] + rgba[1] + rgba[2]) / 3f); // Convert the color to a grayscale by
																		// calculating the mean of the RGB values
				rgba[0] = rgba[1] = rgba[2] = gray; // Create a RGB color equal to the grayscale

				rasterImgOut.setPixel(x, y, rgba); // Set the pixel (x, y) of the imgOut with the new RGB color
			}
		}
		return imgOut;
	}

	private BufferedImage binarization(BufferedImage imgIn, int t) {
		// This function performs binarization of an input image (imgIn) using a
		// threshold value t. It converts the image to gray scale by averaging the RGB
		// values of each pixel, then sets the pixel to either black (0) or white (255)
		// based on whether the grays value value is greater than the threshold. The
		// resulting binary image is stored in imgOut and returned.

		BufferedImage imgOut = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());

		WritableRaster rasterImgIn = imgIn.getRaster();
		WritableRaster rasterImgOut = imgOut.getRaster();

		int[] rgba = new int[4];

		for (int x = 0; x < imgIn.getWidth(); x++) {
			for (int y = 0; y < imgIn.getHeight(); y++) {
				rasterImgIn.getPixel(x, y, rgba);

				int gray = (int) ((rgba[0] + rgba[1] + rgba[2]) / 3f);

				rgba[0] = rgba[1] = rgba[2] = gray;

				if (rgba[0] > t) {
					rgba[0] = rgba[1] = rgba[2] = 255;
				} else {
					rgba[0] = rgba[1] = rgba[2] = 0;
				}

				rasterImgOut.setPixel(x, y, rgba);
			}
		}
		return imgOut;
	}

	public static BufferedImage rotateImage(BufferedImage image, double angle) {
		// This function implements an algorithm to rotate an image that takes in to
		// account the new size of the image after the rotation.

		// Define the transformation to rotate the image around its center.
		AffineTransform rotAt = new AffineTransform();
		rotAt.rotate(Math.toRadians(angle), image.getWidth() / 2.0, image.getHeight() / 2.0);

		// Apply the rotation to the corners of the image to determine the new
		// dimension.
		Point2D[] corners = new Point2D[4];

		corners[0] = rotAt.transform(new Point2D.Double(0.0, 0.0), null);
		corners[1] = rotAt.transform(new Point2D.Double(image.getWidth(), 0.0), null);
		corners[2] = rotAt.transform(new Point2D.Double(0.0, image.getHeight()), null);
		corners[3] = rotAt.transform(new Point2D.Double(image.getWidth(), image.getHeight()), null);

		// Get the translation needed to translate the image after rotation in
		// order avoid the cut of the corners of the image. The translation in X and in
		// Y corresponds respectively to the maximum X and Y negative coordinate of the
		// corners.
		double tX = 0;
		double tY = 0;
		for (int i = 0; i < 4; i++) {
			if (corners[i].getX() < 0 && corners[i].getX() < tX)
				tX = corners[i].getX();
			if (corners[i].getY() < 0 && corners[i].getY() < tY)
				tY = corners[i].getY();
		}

		// Create the translation and concatenate (combine) it with the
		// rotation, but pre-concatenating it because rotation must be applied first.
		AffineTransform traAt = new AffineTransform();
		traAt.translate(-tX, -tY);
		rotAt.preConcatenate(traAt);

		// Apply the transformation and return the resulting image.
		return new AffineTransformOp(rotAt, AffineTransformOp.TYPE_BILINEAR).filter(image, null);
	}

	BufferedImage histogramStretch(BufferedImage imgIn) {
		// This function implements an algorithm to improve the contrast of an image
		// by using the histogram stretch technique.
		BufferedImage imgOut = new BufferedImage(imgIn.getWidth(), imgIn.getHeight(), imgIn.getType());

		WritableRaster rasterImgIn = imgIn.getRaster();
		WritableRaster rasterImgOut = imgOut.getRaster();

		int[] h = getHistogram(imgIn);

		int a = 0;
		float b = 255;
		int c = 0;
		int d = 0;
		for (int i = 0; i < 255; i++)
			if (h[i] != 0) {
				c = i;
				break;
			}

		for (int i = 255; i >= 0; i--)
			if (h[i] != 0) {
				d = i;
				break;
			}

		int rgba[] = new int[4];
		for (int x = 0; x < imgIn.getWidth(); x++) {
			for (int y = 0; y < imgIn.getHeight(); y++) {
				rasterImgIn.getPixel(x, y, rgba);
				int gray2 = (int) ((rgba[0] - c) * ((b - a) / (d - c)) + a);
				rgba[0] = rgba[1] = rgba[2] = gray2;
				// rgba[3] = 255;

				rasterImgOut.setPixel(x, y, rgba);
			}
		}
		return imgOut;
	}

	int[] getHistogram(BufferedImage imgIn) {
		int[] h = new int[256];
		WritableRaster rasterImgIn = imgIn.getRaster();

		int rgba[] = new int[4];
		for (int x = 0; x < imgIn.getWidth(); x++) {
			for (int y = 0; y < imgIn.getHeight(); y++) {
				rasterImgIn.getPixel(x, y, rgba);
				h[rgba[0]]++;
			}
		}
		return h;
	}
}

class ImagePanel extends JPanel {
	// This class is used to display an image and has methods to set and get the
	// image.
	BufferedImage image = null;

	public ImagePanel() {
		setPreferredSize(new Dimension(256, 256));
		setBackground(Color.WHITE);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (image != null)
			g2.drawImage(image, 0, 0, this);

		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	public void setImage(BufferedImage bi) {
		image = bi;
		setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
		invalidate();
		repaint();
	}

	public BufferedImage getImage() {
		return image;
	}
}