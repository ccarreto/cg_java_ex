package chap11.ex_lod;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;


public class Ex_LOD extends Frame {
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

	public static void main(String[] args) {
		Frame frame = new Ex_LOD();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Test LOD");
		frame.pack();
		frame.setVisible(true);
	}

	BufferedImage[] images = new BufferedImage[2];
	TextArea ta = null;
	public Ex_LOD() {
		// create canvas
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);
		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);
		
	    ta = new TextArea("",3,30,TextArea.SCROLLBARS_NONE);
	    ta.setText("Level 1");
	    ta.setEditable(false);
	    add(ta, BorderLayout.SOUTH);
		
		BranchGroup bg = createSceneGraph();
		bg.compile();
		SimpleUniverse su = new SimpleUniverse(cv);
		ViewingPlatform viewingPlatform = su.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();
		
		// orbit behavior to zoom and rotate the view
		OrbitBehavior orbit = new OrbitBehavior(cv,
				OrbitBehavior.REVERSE_ZOOM | OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.DISABLE_TRANSLATE);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);
		su.addBranchGraph(bg);

		// Listener to end program when window is closed
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		addWindowListener(wListener);
	}

	public BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		
		// === Switch with 3 branches (3 levels of detail) ===
		Switch sw = new Switch(0);
		sw.setCapability(Switch.ALLOW_SWITCH_READ);
		sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
		root.addChild(sw);

		// Level 1
		loadImages(2);
		Appearance ap = createAppearance(0);
		sw.addChild(new Sphere(0.4f, Primitive.GENERATE_TEXTURE_COORDS, 40, ap));
		// Level 2
		ap = createAppearance(1);
		sw.addChild(new Sphere(0.4f, Primitive.GENERATE_TEXTURE_COORDS, 20, ap));
		// Level 3
		ap = new Appearance();
		ap.setColoringAttributes(new ColoringAttributes(0f, 0f, 0.5f, ColoringAttributes.FASTEST));
		sw.addChild(new Sphere(0.4f, Sphere.GENERATE_NORMALS, 5, ap));

		// === DistanceLOD behavior ===
		float[] distances = new float[2];
		distances[0] = 5.0f;
		distances[1] = 10.0f;

		DistanceLOD lod = new DistanceLOD(distances);
		lod.setSchedulingBounds(bounds);
		lod.addSwitch(sw);
		root.addChild(lod);

		// === LevelMonitor behavior ===
		LevelMonitor lm = new LevelMonitor(sw, ta);
		lm.setSchedulingBounds(bounds);
		root.addChild(lm);
				
		// === Background ===
		Background background = new Background(1.0f, 1.0f, 1.0f);
		background.setApplicationBounds(bounds);
		root.addChild(background);
		return root;
	}

	void loadImages(int n) {
		URL filename = getClass().getClassLoader().getResource("images/earth.jpg");
		try {
			images[0] = ImageIO.read(filename);
			AffineTransform xform = AffineTransform.getScaleInstance(0.5, 0.5);
			AffineTransformOp scaleOp = new AffineTransformOp(xform, null);
			for (int i = 1; i < n; i++) {
				images[i] = scaleOp.filter(images[i - 1], null);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	Appearance createAppearance(int i) {
		Appearance appear = new Appearance();
		ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, images[i]);
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		texture.setEnable(true);
		texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
		texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
		appear.setTexture(texture);
		return appear;
	}
}

class LevelMonitor extends Behavior {

	Switch sw = null; 
	TextArea ta = null;
	WakeupOnElapsedFrames w = new WakeupOnElapsedFrames(0);
	private int lastValue = -1;
	
	public LevelMonitor(Switch sw, TextArea ta) {
		this.sw = sw;
		this.ta = ta;
	}
	
	@Override
	public void initialize() {

		wakeupOn(w);
	}

	@Override
	public void processStimulus(Enumeration criteria) {
		int atual = sw.getWhichChild();

        if (atual != lastValue) {
            lastValue = atual;
            ta.setText("Level: " + atual);
        }
		
		wakeupOn(w);
	}

}