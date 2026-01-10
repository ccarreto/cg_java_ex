package chap06.ex6_1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib3d.shapes.Axes;
import cglib3d.shapes.Cone;
import cglib3d.shapes.Cone4;
import cglib3d.shapes.Cone2;
import cglib3d.shapes.Floor;

public class TestCone extends Frame {
	BoundingSphere bounds = new BoundingSphere();

	public static void main(String[] args) {
		Frame frame = new TestCone();
		frame.setTitle("Cone ");
		frame.setPreferredSize(new Dimension(640, 480));
		frame.pack();
		frame.setVisible(true);
	}

	public TestCone() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		BranchGroup bg = createSceneGraph();
		bg.compile();

		SimpleUniverse su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(bg);

		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		WindowListener wl = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};

		addWindowListener(wl);
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		// === Axes ===
		Shape3D axes = new Axes(new Color3f(Color.RED), 5, 0.6f);
		root.addChild(axes);

		// === Floor ===
		Shape3D floor = new Floor(10, -1f, 1f, new Color3f(Color.GRAY), new Color3f(Color.LIGHT_GRAY), false);
		root.addChild(floor);

		// === Cone ===
		Appearance app = new Appearance();

		// Version 1 - Appearance configuration to draw only the edges of the polygon mesh
		
		// app.setColoringAttributes(new ColoringAttributes(new Color3f(Color.BLUE),
		// ColoringAttributes.SHADE_FLAT)); // Appearance based on color
		// app.setPolygonAttributes(new
		// PolygonAttributes(PolygonAttributes.POLYGON_LINE,
		// PolygonAttributes.CULL_NONE, 0)); // Draw only the edges
		// app.setLineAttributes(new LineAttributes(2, LineAttributes.PATTERN_SOLID,
		// true)); // Configure the line edges

		// Version 2 - Appearance configuration to draw the polygon mesh (default).
		// If appearance is based on material, the geometry must have
		// normals (therefore use version Cone2), and the scene must have light.
		app.setMaterial(new Material());

		// Cone cone = new Cone(12, 0.8f, 0.3f, app);
		// root.addChild(cone);
		Cone2 cone2 = new Cone2(8, 0.8f, 0.3f, app);
		//root.addChild(cone2);

		// === Rotation behavior ===
		// Example of an animation behavior. In this case the RotationInterpolator
		// that implements a rotation around the Y axis-
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.addChild(spin);
		
		// Additional a transformation to reduce the object size.
		//Transform3D tr = new Transform3D();
		//tr.setScale(0.5);
		//TransformGroup tg = new TransformGroup(tr);
		//spin.addChild(tg);
		//tg.addChild(cone2);

		spin.addChild(cone2);
		Alpha alpha = new Alpha(-1, 4000);
		RotationInterpolator rotator = new RotationInterpolator(alpha, spin);
		BoundingSphere bounds = new BoundingSphere();
		rotator.setSchedulingBounds(bounds);
		spin.addChild(rotator);

		// === Light ===
		// Add light if appearance is based on material
		PointLight pLight = new PointLight(new Color3f(Color.WHITE), new Point3f(1f, 1f, 1f),
				new Point3f(1f, 0.1f, 0f));
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);

		return root;
	}
}
