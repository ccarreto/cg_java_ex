package chap02.ex_custom_shapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ex_Custom_Shapes extends JFrame {

	public static void main(String[] args) {
		JFrame frame = new Ex_Custom_Shapes();
		frame.setTitle("Custom Shapes");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new MyPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}

class MyPanel extends JPanel {

	public MyPanel() {
		setPreferredSize(new Dimension(400, 400));
		setBackground(Color.LIGHT_GRAY);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// The function drawArrow creates and draws a custom shape using the General Path technique.
		// It's not the best method to create a custom shape, as it doesn't allow you to
		// transform the object before drawing it, unless you change the function or
		// add the necessary parameterization.
		drawArrow(g2, 100, 100, 200, 50, Color.RED, Color.BLUE);
		drawArrow(g2, 50, 50, 200, 300, Color.GREEN, Color.GREEN);

		// The function getArrow creates a custom shape using the General Path technique.
		// This method is better, as the function only creates the shape that can then
		// be transformed as we want.
		// We will also see a third method that consists of creating a class that implements 
		// the Shape class. This will allow our customized shape to have all the functionality 
		// that the Java 2D library shapes have and is therefore the best method to use.
		Shape arrow = getArrow(50, 150, 200, 250);

		// Apply a rotation to rotate the object around its center (center of the rectangle).
		// The center of rotation is easy to calculate based on the parameters that define the arrow (x, y, w, h).
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(90), 50 + 0.5 * 200, 150 + 0.5 * 250);
		arrow = at.createTransformedShape(arrow);

		g2.setColor(Color.WHITE);
		g2.fill(arrow);
		g2.setColor(Color.RED);
		g2.draw(arrow);

		// The function creates a custom shape using the Constructive Area Geometry technique.
		// In this case, the shape is being created so that it is positioned centered on the origin. 
		// It is usually easier to create it this way, and it is always possible to reposition it by 
		// applying a translation as shown below. 
		Shape s = getRoundShape(-100, -100, 200, 200);

		// Use a translation to reposition the object.
		at = new AffineTransform();
		at.translate(200, 200);
		s = at.createTransformedShape(s);

		g2.setColor(Color.RED);
		g2.fill(s);
	}

	void drawArrow(Graphics2D g2, int x, int y, int w, int h, Color c1, Color c2) {
		// This function uses the General Path technique to create and draw an arrow
		// shape.
		// The arrow shape is defined in terms of 4 parameters (like other shapes of
		// Java 2D):
		// x, y - coordinates of the upper left corner
		// w - with
		// h - height
		// The four parameters are used to define a set of control positions that are
		// used to specify the segments that construct the path of the custom shape.

		GeneralPath path = new GeneralPath();
		float x0 = x + w;
		float y0 = y + 0.5f * h;

		float x1 = x + 0.7f * w;
		float y1 = y + h;

		float x2 = x + 0.7f * w;
		float y2 = y + 0.7f * h;

		float x3 = x;
		float y3 = y + 0.7f * h;

		float x4 = x;
		float y4 = y + 0.3f * h;

		float x5 = x + 0.7f * w;
		float y5 = y + 0.3f * h;

		float x6 = x + 0.7f * w;
		float y6 = y;

		path.moveTo(x0, y0);
		path.lineTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.lineTo(x5, y5);
		path.lineTo(x6, y6);
		// path.lineTo(x0, y0);
		path.closePath();

		g2.setColor(c1);
		g2.fill(path);

		g2.setColor(c2);
		g2.draw(path);
	}

	public Shape getArrow(int x, int y, int w, int h) {
		// This function uses the same technique as the previous function to construct
		// the custom shape of an arrow, which is returned after being constructed.
		// The arrow shape is defined in terms of 4 parameters (like other shapes of
		// Java 2D):
		// x, y - coordinates of the upper left corner;
		// w - with;
		// h - height.

		GeneralPath path = new GeneralPath();
		float x0 = x + w;
		float y0 = y + 0.5f * h;

		float x1 = x + 0.7f * w;
		float y1 = y + h;

		float x2 = x + 0.7f * w;
		float y2 = y + 0.7f * h;

		float x3 = x;
		float y3 = y + 0.7f * h;

		float x4 = x;
		float y4 = y + 0.3f * h;

		float x5 = x + 0.7f * w;
		float y5 = y + 0.3f * h;

		float x6 = x + 0.7f * w;
		float y6 = y;

		path.moveTo(x0, y0);
		path.lineTo(x1, y1);
		path.lineTo(x2, y2);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.lineTo(x5, y5);
		path.lineTo(x6, y6);
		// path.lineTo(x0, y0);
		path.closePath();

		return path;
	}

	public Shape getRoundShape(int x, int y, int w, int h) {
		// This function exemplifies the implementation of the
		// Construction Area Geometry technique to create a custom
		// shape by subtracting the area of a rectangle from the area of an ellipse

		Shape s1 = new Ellipse2D.Double(x, y, w, h);

		// So that the rectangle is positioned in the center of the ellipse,
		// the coordinates (x,y) of its upper left corner are calculated based
		// on its width and height, and the coordinates of the ellipse.
		double xR = (x + 0.5 * w) - 20.0;
		double yR = (y + 0.5 * h) - 20.0;
		double wR = 40.0f;
		double hR = 40.0f;
		Shape s2 = new Rectangle2D.Double(xR, yR, wR, hR);

		// The Constructive Area Geometry technique is implemented by the Area class,
		// so Shape objects have to be converted to objects of this type before it is
		// possible to combine the areas.
		Area a1 = new Area(s1);
		Area a2 = new Area(s2);
		a1.subtract(a2);

		return a1;
	}
}
