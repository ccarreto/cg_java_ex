package chap07.ex7_1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib3d.shapes.Axes;
import cglib3d.shapes.FloorLamp;
import cglib3d.shapes.Floor;
import cglib3d.shapes.Table;

// This class implements the simple scene of the exercise for chapter 7.
// It uses the classes Axes, Floor, Table and FloorLamp from the CGLib3D.
public class Ex7_1 extends Frame {

	// Global bounds
	BoundingSphere bounds = new BoundingSphere();

	public static void main(String[] args) {
		Frame frame = new Ex7_1();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Simple 3D Scene");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex7_1() {
		// Create a Canvas3D
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		// Add the canvas to the frame
		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		// Create the scene graph
		BranchGroup bg = createSceneGraph();
		bg.compile();

		// Create the root of the scene graph with a standard nominal view
		SimpleUniverse su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();

		// Add the scene graph to the root
		su.addBranchGraph(bg);

		// Add a OrbitBehavior to control the view with the mouse
		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		// Add a listener to end program when window is closed
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		this.addWindowListener(wListener);
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		// Axes
		root.addChild(new Axes(new Color3f(Color.RED), 3, 0.5f));

		// === Floor ===
		root.addChild(new Floor(10, -1, 1, new Color3f(Color.DARK_GRAY), new Color3f(Color.WHITE), true));

		// === Animation ===
		// Uses a RotationInterpolator behavior to control a TransformGroup that
		// rotates the objects around the Y axis.
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.addChild(spin);
		Alpha alpha = new Alpha(-1, 5000);
		RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
		rotator.setSchedulingBounds(bounds);
		spin.addChild(rotator);

		// === Table ===
		Appearance app = new Appearance();
		app.setMaterial(new Material());
		Table table = new Table(app);

		Transform3D tr = new Transform3D();
		tr.setScale(0.5f);
		tr.setTranslation(new Vector3f(0.5f, 0f, 0f));
		TransformGroup tg = new TransformGroup(tr);
		tg.addChild(table);
		// root.addChild(tg);
		spin.addChild(tg);

		// === Lamp ===
		FloorLamp floorLamp = new FloorLamp(app);
		tr = new Transform3D();
		tr.setScale(0.5f);
		tr.setTranslation(new Vector3f(-0.3f, 0f, 0f));
		tg = new TransformGroup(tr);
		tg.addChild(floorLamp);
		//root.addChild(tg);
		spin.addChild(tg);

		// === Background ===
		Background background = new Background(new Color3f(Color.LIGHT_GRAY));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		// === Lights ===
		AmbientLight aLight = new AmbientLight(true, new Color3f(Color.WHITE));
		aLight.setInfluencingBounds(bounds);
		root.addChild(aLight);

		PointLight pLight = new PointLight(new Color3f(Color.YELLOW), new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);

		return root;
	}
}
