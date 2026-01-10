package chap05.ex5_1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib2d.utilities.Utils;

public class Ex5_1 extends Frame implements MouseListener {
	BoundingSphere bounds = new BoundingSphere();

	Background background = null;
	ImageComponent2D image = null;
	int count = 0;
	Color3f[] colors = new Color3f[3];

	public static void main(String[] args) {
		Frame frame = new Ex5_1();
		frame.setPreferredSize(new Dimension(640, 480));
		frame.setTitle("Ex5_1");
		frame.pack();
		frame.setVisible(true);
	}

	// The Frame class doesn't have a
	// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// A possible solution is to override the processWindowEvent method.
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}

	public Ex5_1() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		cv.addMouseListener(this);

// Alternative way to program the mouseClicked event, without implementing the MouseListener in the class.  
//		cv.addMouseListener(new MouseAdapter() { // change background color and image on mouse click
//			public void mouseClicked(MouseEvent ev) {
//				count = (count + 1) % 3;
//				System.out.println(count);
//				background.setColor(colors[count]);
//			}
//		});

		BranchGroup bg = createSceneGraph();
		bg.compile();

		SimpleUniverse su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(bg);

		// OrbitBehavior to control the view with the mouse
		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();


		// === Object node ===
		ColorCube cc = new ColorCube(0.4);
		// root.addChild(cc);

		// === Transformation node ===
		Transform3D tr = new Transform3D();
		tr.setScale(0.5);
		tr.setRotation(new AxisAngle4d(1, 1, 1, Math.PI / 4));
		TransformGroup tg = new TransformGroup(tr);
		root.addChild(tg);
		tg.addChild(cc);

		// === Background node ===
		background = new Background(new Color3f(Color.BLACK)); // new Color3f(Color.RED));
		background.setApplicationBounds(bounds);
		// Must configure permissions to be possible to change the background node at
		// run time
		background.setCapability(Background.ALLOW_COLOR_WRITE);
		background.setCapability(Background.ALLOW_IMAGE_WRITE);

		root.addChild(background);
		// Colors for the background
		colors[0] = new Color3f(Color.RED);
		colors[1] = new Color3f(Color.GREEN);
		colors[2] = new Color3f(Color.BLUE);

		// Image of the background
		BufferedImage bi = Utils.getImage(this, "images/bg.jpg");
		image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bi);

		return root;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Switch between colors and image
		count = (count + 1) % 4;
		System.out.println(count);
		if (count < 3) {
			background.setImage(null);
			background.setColor(colors[count]);
		} else
			background.setImage(image);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
