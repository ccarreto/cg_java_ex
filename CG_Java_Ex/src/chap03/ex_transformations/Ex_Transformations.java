package chap03.ex_transformations;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cglib2d.utilities.Utils;

public class Ex_Transformations extends JFrame {

	public static void main(String[] args) {
		JFrame frame = new Ex_Transformations();
		frame.setTitle("Test Transformations");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new MyPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}

class MyPanel extends JPanel {
	public MyPanel() {
		setPreferredSize(new Dimension(800, 800));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// ===== Object positioning with object transform (AffineTransform) =====
		Utils.drawAxis(g2, Color.BLUE, 200, 5);

		Shape a1 = Utils.getArrow(-100, -100, 200, 200);
		Shape a2 = Utils.getArrow(-50, -50, 100, 100);
		// g2.setColor(Color.RED);
		// g2.fill(a);

		// Position 1
		AffineTransform at = new AffineTransform();
		at.translate(400, 400);
		at.rotate(Math.toRadians(45));
		Shape a1_1 = at.createTransformedShape(a1);

		Utils.drawAxis(g2, Color.BLUE, 200, 5); // Object transformation doesn't change the CS
		g2.setColor(Color.RED);
		g2.fill(a1_1);

		// Position 2
		// at.setToIdentity(); // resets the transformation
		// at.translate(200, 600);
		at.setToTranslation(200, 600); // setTo... resets the transformation
		at.rotate(Math.toRadians(90));

		Shape a1_2 = at.createTransformedShape(a1);
		g2.setColor(Color.RED);
		g2.fill(a1_2);

		// ===== Example with Texture Paint =====
		Shape a3 = Utils.getArrow(-100, -100, 200, 200);
		Rectangle2D anchor = new Rectangle2D.Double(-100, -100, 200, 200);
		TexturePaint tp = new TexturePaint(Utils.getImage(this, "images/Smile.jpg"), anchor);
		g2.setPaint(tp);

		// at.translate(200, 200);
		// at.rotate(Math.toRadians(45));
		// a3 = at.createTransformedShape(a3);

		g2.translate(200, 200);
		g2.rotate(Math.toRadians(45));
		g2.fill(a3);

		g2.setTransform(new AffineTransform());

		// ===== Object positioning with coordinate system transform =====
		// Position 1
		// g2.rotate(Math.toRadians(45));
		AffineTransform myGC = g2.getTransform(); // Save the actual graphics context

		g2.translate(400, 400);
		g2.rotate(Math.toRadians(180));

		Utils.drawAxis(g2, Color.BLUE, 200, 5);
		g2.setColor(Color.GREEN);
		g2.fill(a2);

		// Position 2
		g2.setTransform(myGC); // Restore an old graphics context
		g2.translate(600, 150);
		g2.rotate(Math.toRadians(-90));
		Utils.drawAxis(g2, Color.BLUE, 200, 5);
		g2.setColor(Color.GREEN);
		g2.fill(a2);

	}
}
