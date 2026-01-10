package chap11.ex11_1;

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
import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotPosPathInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib3d.appearance.MyMaterials;
import cglib3d.shapes.Axes;
import cglib3d.shapes.Floor;
import cglib3d.shapes.ImagePanel;
import cglib3d.shapes.SimpleObj;
import cglib3d.shapes.Spot2;
import cglib3d.shapes.ViewPosition;


public class Ex11_1 extends Frame {

	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4.0);

	public static void main(String[] args) {
		Frame frame = new Ex11_1();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Ex11");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex11_1() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		SimpleUniverse su = new SimpleUniverse(cv, 2);

		// Position of the view
		Transform3D viewTr = new Transform3D();
		viewTr.lookAt(new Point3d(-2.5, 2.5, 2.5), new Point3d(0.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));

		// First Person view
		// viewTr.lookAt(new Point3d(0.0, 0.50, 0.55), new Point3d(0.0, 0.4, 0.0), new
		// Vector3d(0.0, 1.0, 0.0));

		viewTr.invert();
		su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTr);

		// Scene graph
		BranchGroup bg = createSceneGraph(su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0));
		bg.compile();
		su.addBranchGraph(bg); // Add the content branch to the simple universe

		// Add a OrbitBehavior to control the view with the mouse
		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		// Listener to end program when window is closed
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		addWindowListener(wListener);
	}

	private BranchGroup createSceneGraph(TransformGroup viewTg) {
		BranchGroup root = new BranchGroup();

		OrderedGroup og = new OrderedGroup();
		
		// == Floor ==
		og.addChild(new Floor(21, -1.5f, 1.5f, new Color3f(Color.LIGHT_GRAY), new Color3f(Color.GRAY), true));

		// == Axes ==
	    og.addChild(new Axes(new Color3f(Color.RED), 3, 1f));

		// == Object ==
		Appearance myObjApp = new Appearance();
		myObjApp.setMaterial(new MyMaterials(MyMaterials.BRONZE));
		SimpleObj myObj = new SimpleObj(0.1f, myObjApp);
		// root.addChild(myObj);

		// TransformGroup to move the object
		TransformGroup moveTg = new TransformGroup();
		moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		og.addChild(moveTg);
		moveTg.addChild(myObj);

		// Use viewTg to implement a First Person view (the camera moves with the object)
		// viewTg.addChild(myObj);

		// == Interpolator to move the object along the path ==
		RotPosPathInterpolator interpolator = createInterpolator(moveTg, true, root);

		// Use viewTg to implement a First Person view (the camera moves with the object)
		// RotPosPathInterpolator interpolator = new RotPosPathInterpolator(alpha,
		// viewTg, tr, knots, quats, positions);

		interpolator.setSchedulingBounds(bounds);
		moveTg.addChild(interpolator);

		// == Billboard ==
		TransformGroup bbTg = new TransformGroup();
		bbTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		og.addChild(bbTg);
		Billboard bb = new Billboard(bbTg, Billboard.ROTATE_ABOUT_POINT, new Point3f(0f, 0f, 1f));
		// Billboard bb = new Billboard(bbTg, Billboard.ROTATE_ABOUT_AXIS, new
		// Vector3f(0, 1, 0));
		// bb.setAlignmentAxis(0, 1, 0);

		bb.setSchedulingBounds(bounds);
		bbTg.addChild(bb);

		Transform3D tr = new Transform3D();
		tr.setTranslation(new Vector3f(0f, 0.5f, 1f));
		TransformGroup tg = new TransformGroup(tr);
		ImagePanel imagePanel = new ImagePanel(this, "images/T-Rex.png", 1f);

		tg.addChild(imagePanel);
		// root.addChild(tg);
		bbTg.addChild(tg);
		// tg.addChild(bbTg);

		// == Background ==
		Background background = new Background(new Color3f(Color.DARK_GRAY));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		// == Lights ==
		AmbientLight ablight = new AmbientLight(true, new Color3f(Color.GREEN));
		ablight.setInfluencingBounds(bounds);
		root.addChild(ablight);

		PointLight ptlight = new PointLight(new Color3f(Color.GREEN), new Point3f(2f, 2f, 2f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		root.addChild(ptlight);

		root.addChild(og);
		return root;
	}

	private RotPosPathInterpolator createInterpolator(TransformGroup moveTg, boolean showWayoints, BranchGroup root) {
		// => Array of positions that define the path
		// The extra positions with the same coordinates are for the rotation
		// interpolations for the object to change direction
		Point3f[] positions = new Point3f[9];

		positions[0] = new Point3f(-1f, 0f, 1f);
		if (showWayoints)
			root.addChild(new Spot2(positions[0], new Point3f(0, 0.05f, 0), new Color3f(Color.RED), 20));
		 if (showWayoints)
		root.addChild(new ViewPosition(positions[0], new Color3f(Color.RED), 20));

		positions[1] = new Point3f(-1f, 0f, -1f);
		positions[2] = new Point3f(-1f, 0f, -1f); // Same position, for rotation in place.
		if (showWayoints)
			root.addChild(new ViewPosition(positions[1], new Color3f(Color.RED), 20));

		positions[3] = new Point3f(1f, 0f, -1f);
		positions[4] = new Point3f(1f, 0f, -1f);
		if (showWayoints)
			root.addChild(new ViewPosition(positions[3], new Color3f(Color.RED), 20));

		positions[5] = new Point3f(1f, 0f, 1f);
		positions[6] = new Point3f(1f, 0f, 1f);
		if (showWayoints)
			root.addChild(new ViewPosition(positions[5], new Color3f(Color.RED), 20));

		positions[7] = new Point3f(-1f, 0f, 1f);
		positions[8] = new Point3f(-1f, 0f, 1f);
		if (showWayoints)
			root.addChild(new ViewPosition(positions[7], new Color3f(Color.RED), 20));

		// => Array of quaternions that define the orientation between positions
		// To be easer, and not to have to work with quaternions directly, we create the
		// orientation for each position as a AxisAngle4f object and convert it to a
		// quaternion.
		Quat4f[] quats = new Quat4f[9];
		for (int i = 0; i < quats.length; i++)
			quats[i] = new Quat4f();

		Quat4f q = new Quat4f();
		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(0)));
		quats[0].add(q);

		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(0)));
		quats[1].add(q);

		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-90)));
		quats[2].add(q);

		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-90)));
		quats[3].add(q);
		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-180)));
		quats[4].add(q);

		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-180)));
		quats[5].add(q);
		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-270)));
		quats[6].add(q);

		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(-270)));
		quats[7].add(q);
		q.set(new AxisAngle4f(0f, 1f, 0f, (float) Math.toRadians(0)));
		quats[8].add(q);

		// => Array of knot values that define the times of the animation
		float[] knots = new float[9];

		// Version with same time interval for all positions
		float a = 1f / (knots.length - 1); // 1/8
		for (int i = 0; i < knots.length; i++)
			knots[i] = i * a;

		// Version with custom time intervals
		knots[0] = 0f;
		knots[1] = 0.1f;
		knots[2] = 0.15f;  // Duration between position 1 and position 2 is (0.15 - 0.1) = 0.05 (5%)
		knots[3] = 0.6f;
		knots[4] = 0.7f;
		knots[5] = 0.8f;
		knots[6] = 0.9f; // Duration between position 5 and position 6 is (0.9 - 0.8) = 0.1 (10%)
		knots[7] = 0.95f;
		knots[8] = 1f;

		// This version of Alpha starts the timer too early, and when
		// the renderer starts rendering the scene, the interpolator
		// has already moved the object along the path
		// Alpha alpha = new Alpha(-1, 10000);

		// This version of Alpha, with phaseDelayDuration, waits 2.5 seconds
		// to start the alpha and consequently the interpolator, giving time
		// so that when the renderer starts to render the scene, the object
		// starts at the initial position
		Alpha alpha = new Alpha(-1, 0, 2500, 10000, 0, 0);

		// This version of Alpha has increasing and decreasing phases that
		// inverts the animation during the decreasing phase. 
		// Alpha alpha = new Alpha(-1, Alpha.INCREASING_ENABLE |
		// Alpha.DECREASING_ENABLE, 0,0, 5000,0,0,5000,0,0);

		Transform3D tr = new Transform3D();
		//tr.rotX(Math.toRadians(90));

		return (new RotPosPathInterpolator(alpha, moveTg, tr, knots, quats, positions));
	}
}
