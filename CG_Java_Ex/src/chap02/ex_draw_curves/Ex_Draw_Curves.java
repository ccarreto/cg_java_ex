package chap02.ex_draw_curves;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ex_Draw_Curves extends JFrame {
	MyPanel panel = null;
	
	
	public static void main(String s[]) {
		JFrame frame = new Ex_Draw_Curves();
		frame.setTitle("Curves");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public Ex_Draw_Curves() {
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
	
		panel = new MyPanel();
		cp.setLayout(new BorderLayout());
		cp.add(BorderLayout.CENTER, panel);
		
		JPanel buttonsPanel = new JPanel();
		JButton b1 = new JButton("Circle 1");
		JButton b2 = new JButton("Circle 2");
		JButton b3 = new JButton("Curve");
		b1.addActionListener(e -> b1Pressed());
		b2.addActionListener(e -> b2Pressed());
		b3.addActionListener(e -> b3Pressed());
		buttonsPanel.add(b1);
		buttonsPanel.add(b2);
		buttonsPanel.add(b3);
		cp.add(BorderLayout.SOUTH, buttonsPanel);
	}
	
	private void b1Pressed() {
		panel.redraw(0);
	}
	private void b2Pressed() {
		panel.redraw(1);
	}
	private void b3Pressed() {
		panel.redraw(2);
	}
}

class MyPanel extends JPanel {

	int type = 0;
	
	public MyPanel() {
		setPreferredSize(new Dimension(400, 400));
		//setBackground(Color.LIGHT_GRAY);
		setBackground(Color.WHITE);
	}

	public void redraw(int x) {
		type = x;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.translate(200, 200);
		drawAxes(g2, Color.BLUE, 150);

		g2.setColor(Color.RED);
		
		switch (type) {
		case 0:
			drawCircle_1(g2);	
			break;
        case 1:
        	drawCircle_2(g2, 100, 80);
			break;
        case 2:
    		drawCurve(g2, 80, 0.0, 2*Math.PI);			
			break;
		}
	}

	private void drawAxes(Graphics2D g2, Color color, int size) {
	  	g2.setColor(color);
	  	g2.drawLine(-size, 0, size, 0);
	  	g2.drawLine(0, -size, 0, size);
	}
	
	private void drawCircle_1(Graphics2D g2) {
		// Draw of a circle using an equation y = f(x).
		// Considering the circle centered at the origin:
		// x^2 + y^2 = r^2 => y = +- sqrt(r^2 - x^2)
		// The use of this type of equation normally does't work because different values of x give the same y. 
		// This is because we are using a raster system with e finite resolution of pixels.  
		
		int x = 0;    // Coordinate X.
		int y = 0;    // Coordinate Y.
		int r = 100;  // Radius

		// The result is not good. The circle has gaps.  
		for (x = -r; x <= r; x++) {
			y = (int) Math.sqrt(r * r - x * x);
			g2.drawLine(x, y, x, y);
			
			y = (int) -Math.sqrt(r * r - x * x);
			g2.drawLine(x, y, x, y);
		}
	}

	private void drawCircle_2(Graphics2D g2, int r, int nPoints) {
		// This version uses the parametric equations of the circle.
		// Considering the circle centered at the origin:
		// x = r x cos(t)
		// y = r x sin(t)
		// 0 <= t <= 2 x Pi 
		// The curve is approximated with a set of points that are connected by small line segments.
	
		// Initial value of t.
		double t = 0;
		
		// First point for the initial value of t.
		int x1 = (int) (r * Math.cos(t));
		int y1 = (int) (r * Math.sin(t));
		int x2;
		int y2;

		// Increment of t.
		double dt = 2 * Math.PI / nPoints; // The greater the number of points, the greater the resolution of the curve.

		// Calculate the remaining points of the curve.
		for (int i = 1; i <= nPoints; i++) {
			// Next t.
			t = i * dt;
			
			// Next point of the curve.
			x2 = (int) (r * Math.cos(t));
			y2 = (int) (r * Math.sin(t));
			
			// Connect next point with last point.  
			g2.drawLine(x1, y1, x2, y2);
			
			// Next point becomes last point.
			x1 = x2;
			y1 = y2;
		}
	}

	private void drawCurve(Graphics2D g2, int nPoints, double tmin, double tmax) {
		// The code of function drawCircle2() can be generalized to draw any curve with known parametric equations.
		// To do that we add to the parameters, the minimum and maximum values of t and 
		// implement the parametric equations of a specific curve in tow external function f() and g().
		// Function f() and g() should implement the equations of the curve we want to draw. For example, these equations: 
		// x = f(t) = 100 x cos(3t)
		// y = g(t) = 100 x sin(2t)
		// 0 ≤ t ≤ 2π
		
		// Initial value of t.
        double t = tmin;
		
        // First point for the initial value of t.
		int x1 = f(t);
		int y1 = g(t);
		int x2;
		int y2;

		// Increment of t.
		double dt = (tmax - tmin) / nPoints; // The greater the number of points, the greater the resolution of the curve.

		// Calculate the remaining points of the curve.
		for (int i = 1; i <= nPoints; i++) {
			// Next t.
			t = i * dt;
			
			// Next point of the curve.
			x2 = f(t);
			y2 = g(t);
			
			// Connect next point with last point.  
			g2.drawLine(x1, y1, x2, y2);
			
			// Next point becomes last point.
			x1 = x2;
			y1 = y2;
		}
	}
	
	private int f(double t) {
		// Function that implements the parametric equation for x. 
		return ((int) (100 * Math.cos(3 * t)));
	}
	
	private int g(double t) {
		// Function that implements the parametric equation for y.
		return ((int) (100 * Math.sin(2 * t)));
	}
}