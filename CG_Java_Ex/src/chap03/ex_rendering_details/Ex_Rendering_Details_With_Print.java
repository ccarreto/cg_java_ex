package chap03.ex_rendering_details;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cglib2d.utilities.Utils;

public class Ex_Rendering_Details_With_Print extends JFrame {

	// ===== 1º =====
	// Declare the panel and the PrinterJob objects
	MyPanel panel;
	PrinterJob pj;

	public static void main(String[] args) {
		JFrame frame = new Ex_Rendering_Details_With_Print();
		frame.setTitle("Rendering Details");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ===== 2º =====
		// Since the frame has a more elaborated interface with the setup of layouts,
		// the panel should be created in the constructor.
		// JPanel panel = new MyPanel();
		// frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
	}

	// ===== 3º =====
	// Create a constructor to setup the frame layout and add a panel and a button.
	public Ex_Rendering_Details_With_Print() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		// Add the button
		JButton button = new JButton("Print");
		cp.add(button, BorderLayout.SOUTH);
		// Add ActionListener to the button to execute a function when the button is
		// clicked.
		button.addActionListener(e -> buttonClick());

		// Add the panel.
		panel = new MyPanel();
		cp.add(panel, BorderLayout.CENTER);

		// ===== 4º =====
		// Create and configure the PrinterJob object
		pj = PrinterJob.getPrinterJob(); // Create a printer job
		pj.setPrintable(panel); // Set the object panel as the object of the class that implements the Printable
								// interface.
		// The class MyPanel is going to implement the Pritable interface. That is, is
		// going to implement the print method that
		// has the code to print the graphics.
	}

	private void buttonClick() {
		// ===== 9º =====
		// Shows the OS print dialog (user selects printer, page layout, etc.)
		// If the user confirms, print() is called.
		// At this point Java enters the printing loop and begins calling:
		// print(Graphics graphics, PageFormat pageFormat, int pageIndex)
		// for pageIndex = 0, 1, 2, ...
		
		if (pj.printDialog()) {
			try {
				pj.print();
			} catch (PrinterException ex) {
				ex.printStackTrace();
			}
		}
	}

}

// ===== 5º =====
// The class MyPanel implements the Printable interface
class MyPanel extends JPanel implements Printable {

	public MyPanel() {
		setPreferredSize(new Dimension(850, 850));
		setBackground(Color.LIGHT_GRAY);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// ===== 7º =====
		// Call the function to draw the graphics
		drawGraphics(g2);

	}

	// ===== 6º =====
	// Create a function to draw the graphics than can be called from the
	// paintComponentFunction or from the print function.
	private void drawGraphics(Graphics2D g2) {
		// ===== ClippingPath =====
		Shape a5 = Utils.getArrow(400 - 350, 400 - 350, 700, 700);
		g2.draw(a5);
		g2.setClip(a5);

		// ===== Color =====
		// Demonstration of different ways to create colors
		g2.setColor(Color.RED);
		g2.fillRect(50, 150, 200, 250);

		Shape a1 = Utils.getArrow(50, 150, 200, 250);
		// g2.setColor(Color.RED); // Color constant
		// g2.setColor(new Color(1f, 1f, 0f)); // Combination of primary colors (float 0
		// to 1)
		// g2.setColor(new Color(255, 255, 0)); // Combination of primary colors (int 0
		// to 255)
		// g2.setColor(new Color(100, 100, 100)); // If R = G = B -> intensity of gray
		// g2.setColor(new Color(0.4f, 0.6f, 0.3f, 0.3f)); // Combination of primary
		// colors with 30% transparent
		g2.setColor(new Color(255, 255, 255, 128)); // White, but 50% transparent
		g2.fill(a1);
		g2.setColor(Color.BLACK);
		g2.draw(a1);

		// ===== GradientPaint =====
		Shape a2 = Utils.getArrow(350, 50, 250, 350);
		// Create and activate a diagonal gradient
		GradientPaint gp = new GradientPaint(350, 50, Color.RED, 350 + 250, 50 + 350, Color.BLUE);
		g2.setPaint(gp);
		// Paint the a2 shape with the active gradient
		g2.fill(a2);
		// Draw the a2 shape contour in blue
		g2.setColor(Color.BLUE);
		g2.draw(a2);
		// Draw the line segment used to define the direction of the gradient
		g2.setColor(Color.WHITE);
		g2.drawLine(350, 50, 350 + 250, 50 + 350);

		// Example of a horizontal cyclic gradient
		Shape a3 = Utils.getArrow(150, 350, 350, 250);
		gp = new GradientPaint(150 + 50, 350 + 250 / 2, Color.RED, 150 + 100, 350 + 250 / 2, Color.BLUE, true);
		g2.setPaint(gp);
		g2.fill(a3);
		g2.setColor(Color.BLUE);
		g2.draw(a3);
		g2.setColor(Color.WHITE);
		g2.drawLine(150 + 50, 350 + 250 / 2, 150 + 100, 350 + 250 / 2);

		// ===== TexturePaint =====
		BufferedImage image = Utils.getImage(this, "images/Smile.jpg");

		Shape a4 = new Ellipse2D.Double(500, 500, 300, 300);

		// In the first TexturePaint the anchor rectangle has the same position and size
		// of the shape
		TexturePaint tp = new TexturePaint(image, new Rectangle2D.Float(500, 500, 300, 300));

		// In the second TexturePaint the anchor rectangle has its left upper corner at
		// the center of the shape
		// TexturePaint tp = new TexturePaint(image, new Rectangle2D.Float(500, 500,
		// 150, 150));

		// In the third TexturePaint the anchor rectangle has its left upper corner at
		// the origin of the coordinates space
		// TexturePaint tp = new TexturePaint(image, new Rectangle2D.Float(0, 0, 50,
		// 50));

		g2.setPaint(tp);
		g2.fill(a4);

		// Draw the anchor rectangle for reference
		g2.setColor(Color.BLACK);
		g2.draw(tp.getAnchorRect());

		// ===== Stroke =====
		// Stroke s = new BasicStroke(15);
		// Stroke s = new BasicStroke(25, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		// Stroke s = new BasicStroke(25, BasicStroke.CAP_SQUARE,
		// BasicStroke.JOIN_MITER, 2);

		// float[] dashArray = {20, 20};
		float[] dashArray = { 20, 5, 5, 5 };
		float dashPhase = 0;
		// float dashPhase = 20;
		Stroke s = new BasicStroke(10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dashArray, dashPhase);
		g2.setStroke(s);
		g2.setColor(Color.BLUE);
		g2.draw(a4);

		// ===== Font and Font Metrics =====
		// Utils.listSystemFonts();
		Font font = new Font("Serif", Font.PLAIN, 80);
		g2.setColor(Color.ORANGE);
		drawCenteredText(g2, font, "Computer Graphics", 400, 400);

		// ===== AlphaComposite =====
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g2.setComposite(ac);

		// image = getImage("images/earth.jpg");
		image = Utils.getImage(this, "images/earth.jpg");
		g2.drawImage(image, 400 - image.getWidth() / 2, 400 - image.getHeight() / 2, this);
	}

	// Suggestion: Add this function to the CGLib2D
	private void drawCenteredText(Graphics2D g2, Font font, String text, int xc, int yc) {
		g2.setFont(font);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D r = font.getStringBounds(text, frc);

		g2.drawString(text, (int) (xc - r.getWidth() / 2.0), (int) (yc + r.getHeight() / 2.0));
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

		// ===== 8º =====
		// The print method of the Printable interface is the core entry point of the 
		// printing process in Java. Whenever the printing system needs to render a 
		// page, this method is invoked, providing a graphics context (Graphics) 
		// where the page content should be drawn, a PageFormat object that defines
		// the page orientation and margins, and a page index (pageIndex) indicating 
		// which page is being requested. If the index corresponds to a valid page, 
		// the method must perform the necessary drawing operations and return 
		// PAGE_EXISTS, signaling to the printing system that the page should be
		// printed. Otherwise, it should return NO_SUCH_PAGE, indicating that no 
		// further pages are available. In this way, the print method gives the 
		// developer full control over what gets printed and how many pages are 
		// produced, rendering each page through custom drawing instructions before
		// it is sent to the printer.

		Graphics2D g2 = (Graphics2D) graphics;
		// Adjust the graphics size to fit the A4 sheet
		double scaleX = pageFormat.getImageableWidth() / getWidth();
		double scaleY = pageFormat.getImageableHeight() / getHeight();
				
		if (scaleX < scaleY)
			g2.scale(scaleX, scaleX);
		else
			g2.scale(scaleY, scaleY);
        
		// Manage the page index if multiple pages exist
		// In this case there is only one page
		switch (pageIndex) {
		case 0:
			drawGraphics(g2);
			break;
		// case 1:
		// Other possible page.
		default:
			// In this case if pageIndex > 0 return NO_SUCH_PAGE to end. 
			return NO_SUCH_PAGE;
		}
		// Validate page. In this case pageIndex 0
		return PAGE_EXISTS;
	}

}
