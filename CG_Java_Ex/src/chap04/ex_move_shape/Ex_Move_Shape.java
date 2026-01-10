package chap04.ex_move_shape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cglib2d.shapes.Arrow;
import cglib2d.shapes.Heart;

// This application exemplifies the implementation of mouse interaction to move and 
// detect collisions between shapes. Select the object with the left mouse button 
// to move it and with the right mouse button to rotate it.
public class Ex_Move_Shape extends JFrame {

	public static void main(String[] args) {
		JFrame frame = new Ex_Move_Shape();
		frame.setTitle("Move Shape");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new MyPanel();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}

class MyPanel extends JPanel implements MouseListener, MouseMotionListener {
	// MyPanel needs to implement MouseListner and MouseMotionListner to detect
	// mouse events and implement mouse interaction.
	Shape heart = new Heart(400 - 100, 400 - 100, 200, 200);
	Shape arrow;
	int xPos = 100;
	int yPos = 100;

	AffineTransform at = new AffineTransform();

	boolean selectedB1 = false;
	boolean selectedB3 = false;

	boolean collision = false;

	// Translation
	int firstX = 0;
	int firstY = 0;
	int deltaX = 0;
	int deltaY = 0;

	// Rotation
	int oldY = 0;
	int newY = 0;
	double angle = 0;

	public MyPanel() {
		setPreferredSize(new Dimension(800, 800));

		// Add the mouse listeners implemented in the class itself.
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		// ===== Antialiasing =====
		// Set rendering hints to activate antialiasing to reduce the "stairs effect"at
		// the edge of the objects.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		// RenderingHints.VALUE_INTERPOLATION_BICUBIC)

		// ===== Drag the arrow object =====
		// The arrow object starts always centered at the origin, and then is
		// transformed to the actual location
		// This facilitates the implementation of the interaction algorithms.
		//
		arrow = new Arrow(-50, -25, 200, 100);
		at.setToTranslation(xPos, yPos);
		at.rotate(Math.toRadians(angle));
		arrow = at.createTransformedShape(arrow);
		// System.out.println(angle);

		// ===== Collision detection between arrow and heart =====
		// Version 1 - based on the intersection of the rectangular bounds of the
		// shapes.
		if (arrow.intersects(heart.getBounds()))
			collision = true;
		else
			collision = false;

		// Version 2 - Based on the verification if a point is contained in the interior
		// of a shape.

		// The point is the center of the heart
		// Point2D p = new Point2D.Double(heart.getBounds().getCenterX(),
		// heart.getBounds().getCenterY());
		// if(arrow.contains(p)) collision = true;
		// else collision = false;

		// The point is the tip of the arrow
		// Point2D p = new Point2D.Double(arrow.getBounds().getX() +
		// arrow.getBounds().getWidth(),
		// arrow.getBounds().getCenterY());
		// if (heart.contains(p))
		// collision = true;
		// else
		// collision = false;

		// ===== Draw objects =====
		if (!collision) {
			g2.setColor(Color.BLUE);
			g2.fill(heart);
			g2.setColor(Color.GREEN);
			g2.fill(arrow);
		} else {
			g2.setColor(Color.RED);
			g2.fill(heart);
			g2.fill(arrow);
		}

		// ===== Draw objects bounds for reference =====
		g2.setColor(Color.RED);
		g2.draw(arrow.getBounds());
		g2.draw(heart.getBounds());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selectedB1) {
			// Determines the displacement of the mouse.
			deltaX = e.getX() - firstX;
			deltaY = e.getY() - firstY;

			// Add the displacement of the mouse to update the position of the object.
			xPos += deltaX;
			yPos += deltaY;

			// Update the actual position of the mouse.
			firstX += deltaX;
			firstY += deltaY;

			// Redraw the scene.
			repaint();
		}

		if (selectedB3) {
			// Determines the displacement of the mouse in the Y direction.
			newY = e.getY();

			// Use displacement to update the angle of rotation.
			if (newY > oldY)
				angle += 5;
			else if (newY < oldY)
				angle -= 5;

			// Update the actual Y position of the mouse.
			oldY = newY;

			// Redraw the scene.
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Verify if the object is being selected with the left button of the mouse to
		// initiate the translation.
		if (e.getButton() == MouseEvent.BUTTON1)
			if (arrow.contains(e.getPoint())) {
				selectedB1 = true;
				firstX = e.getX();
				firstY = e.getY();
			} else
				selectedB1 = false;

		// Verify if the object is being selected with the left button of the mouse to
		// initiate the rotation.
		if (e.getButton() == MouseEvent.BUTTON3)
			if (arrow.contains(e.getPoint())) {
				selectedB3 = true;
				oldY = e.getY();
			} else
				selectedB3 = false;

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		selectedB1 = false;
		selectedB3 = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
