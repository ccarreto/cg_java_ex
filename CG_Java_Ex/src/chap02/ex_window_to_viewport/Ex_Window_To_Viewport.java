package chap02.ex_window_to_viewport;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.*;

import cglib2d.utilities.Utils;
import cglib2d.utilities.WtoV;

/**
 * 
 * Demonstration of the Window to Viewport mapping.
 *
 */

public class Ex_Window_To_Viewport extends JFrame {

	public static void main(String[] args) {
		JFrame frame = new Ex_Window_To_Viewport();
		frame.setTitle("Mapping Window-Viewport");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new MyPanel();
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
	}
}

class MyPanel extends JPanel {

	/*
	float[][] data = { { 6824, 7479, 122 }, { 1358, 7594, 612 }, { 2625, 1990, 880 }, { 6088, 7404, 266 },
			{ 6809, 2419, 358 }, { 8790, 2522, 755 }, { 5435, 4974, 45 }, { 7332, 8604, 812 }, { 5741, 6806, 590 },
			{ 0, 0, 900 }, { 7336, 6944, 783 } };
    */
	  
	float[][] data = new float[11][3];
	

	// Limits of the Window.
	int XWmin = -1000; 
	int YWmin = -1000; 
	int XWmax = 10000; 
	int YWmax = 10000;

	// The data values (3rd column) are represented by circles.
	// It is considered that the maximum possible value in coordinates of the
	// universe is 900 and that the maximum possible value for the radius of
	// the circle in coordinates of the device is 15.
	// Thus, a given value is represented by a circle of radius = value * 20/1000
	int maxValue = 900;
	float radiusScale = 15f / maxValue;

	// Limits of the Viewport.
	int XDmin = 100;
	int YDmin = 100;
	int XDmax = 300;
	int YDmax = 300;

	 /*
	 // If the viewport coordinates change, the mapping will adapt. In case the
	 // aspect ratio of the viewport is different from the window, there is no
	 // distortion in the mapping, because the mapping uses the smallest scale factor
	 // of the X and Y directions. 
	 int XDmin = 100; 
	 int YDmin = 150; 
	 int XDmax = 500;
	 int YDmax = 350;
	 */

	WtoV w2v = new WtoV(XWmin, YWmin, XWmax, YWmax, XDmin, YDmin, XDmax, YDmax);

	// float[][] data = new float[11][3];

	public MyPanel() {
		setPreferredSize(new Dimension(800, 400));

		// Class Utils and function readData() must exist in the CGLib2D project.
		// Project Chapter02 must be configured to use the classes of the CGLib2D project.
		// File data.txt must be inside the program itself (inside the folder src/data/).
		Utils.readData(this, data, "data/data.txt"); 
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.red);
		g2.drawRect(XDmin, YDmin, XDmax - XDmin - 1, YDmax - YDmin - 1);

		g2.setColor(Color.blue);

		for (int i = 0; i < data.length; i++) {
			// Mapping of the coordinates.
			float XW = data[i][0];
			float YW = data[i][1];
			if (XW >= XWmin && XW <= XWmax && YW >= YWmin && YW <= YWmax) {

				int XD = w2v.MapX(XW);
				int YD = w2v.MapY(YW);
				// To change the origin to the bottom left corner of the rectangle:
				// int YD = YDmin + YDmax - w2v.MapY(YW);

				// Draw the circle.
				// int r = 10;
				int r = (int) (radiusScale * (data[i][2]));
				g2.fillOval(XD - r, YD - r, 2 * r, 2 * r);

				System.out.println(XD + ", " + YD);
			}
		}
	}
}
