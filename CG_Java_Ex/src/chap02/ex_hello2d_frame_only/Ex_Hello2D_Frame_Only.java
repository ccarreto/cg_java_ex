// This is a new version of the example "Hello2"D of Chapter 2 of the book, 
// but without the structure the allows the run of the application as an applet 
// and as an windows application. The examples of the book use that structure, 
// but because applets are now obsolete, in the examples we create from scratch, 
// we will not use applets when creating the application window. 

package chap02.ex_hello2d_frame_only;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

public class Ex_Hello2D_Frame_Only extends JFrame {
  public static void main(String s[]) {
    JFrame frame = new Ex_Hello2D_Frame_Only();
    frame.setTitle("Hello 2D Frame Only");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JPanel panel = new Hello2DPanel();
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}

class Hello2DPanel extends JPanel {
  public Hello2DPanel() {
    setPreferredSize(new Dimension(640, 480));
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    Ellipse2D e = new Ellipse2D.Double(-100, -50, 200, 100);
    AffineTransform tr = new AffineTransform();
    tr.rotate(Math.PI / 6.0);
    Shape shape = tr.createTransformedShape(e);
    g2.translate(300,200);
    g2.scale(2,2);
    g2.setColor(Color.blue);
    g2.draw(shape);
    g2.drawString("Hello 2D", 0, 0);
  }
}