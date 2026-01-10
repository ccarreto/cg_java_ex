package chap10.ex10_1_fps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib3d.appearance.MyMaterials;
import cglib3d.appearance.TextureAppearance;
import cglib3d.behaviors.KeyControl;
import cglib3d.shapes.Floor;
import cglib3d.shapes.MyCone;
import cglib3d.shapes.SimpleObj;

public class Ex10_FPS_2025 extends Frame {

	// === This version demonstrates the technique FPS (First PErson Shooter) where the view (camera) is moved with the object. ===
	
	BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 2); // Bounds of the scene
	SimpleObj simpleObj = null;

	public static void main(String[] args) {
		Frame frame = new Ex10_FPS_2025();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Ex10 FPS");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex10_FPS_2025() {
		// === Create Canvas3D ===
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);

		// Add canvas to the frame
		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		// === Create SimpleUniverse  ===
		// Create the a simple universe with 2 TransformGroup nodes in the visualization branch
		SimpleUniverse su = new SimpleUniverse(cv, 2);

		// Create a personalized point of view
		// Position the camera just above the back of the object
		Transform3D viewTr = new Transform3D();
		viewTr.lookAt(new Point3d(0.0, 0.55, 0.5), new Point3d(0.0, 0.5, 0.0), new Vector3d(0.0, 1.0, 0.0));
		viewTr.invert();
		su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTr);

		// Pass the first TransformGroup node of the view branch to be controlled by the interaction behavior
		BranchGroup bg = createSceneGraph(su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0));
		bg.compile();
		su.addBranchGraph(bg); // Add the content branch to the simple universe

		// === Add a OrbitBehavior to control the view with the mouse ===
		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		// === Add listener to end program when window is closed ===
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		addWindowListener(wListener);
	}

	private BranchGroup createSceneGraph(TransformGroup viewTg) {
		BranchGroup root = new BranchGroup();

		// === Floor ===
		root.addChild(new Floor(21, -1.5f, 1.5f, new Color3f(Color.WHITE), new Color3f(Color.DARK_GRAY), true));

		// === Object to move ===
		Appearance objApp = new Appearance();
		objApp.setMaterial(new MyMaterials(MyMaterials.BRONZE));
		simpleObj = new SimpleObj(0.2f, objApp);
		//root.addChild(simpleObj);
		
		// === TransformGroup to move the object ===
		// Not used. The object os moved my the TrhsnformGroup of the view.
		//TransformGroup moveTg = new TransformGroup();
		//moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		//moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//root.addChild(moveTg);
		
		// The TransformGroup of the viewBranch (viewTg) is also the parent of the object to move
		viewTg.addChild(simpleObj);
		
		// === Obstacles ===
		// Wall
		TextureAppearance wallApp = new TextureAppearance(this, "images/brick_wall_texture.jpg", new Material(), true, false);
		Box wall = new Box(1.0f, 0.5f + 0.001f, 0.01f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS,
				wallApp);
		Transform3D tr = new Transform3D();
		tr.setRotation(new AxisAngle4d(0, 1, 0, Math.toRadians(-25)));
		tr.setTranslation(new Vector3f(0f, 0.5f, -0.7f));
		TransformGroup tg = new TransformGroup(tr);
		tg.addChild(wall);
		root.addChild(tg);

		// Pyramid
		TextureAppearance coneApp = new TextureAppearance(this, "images/brick_wall_texture.jpg", new Material(), true, true);
		MyCone cone = new MyCone(4, 0.5f, 0.3f, coneApp);
		tr = new Transform3D();
		tr.setTranslation(new Vector3f(-0.7f, 0f, 0.7f));
		tg = new TransformGroup(tr);
		tg.addChild(cone);
		root.addChild(tg);
		
		// === Behavior to move the object ===
		// The behavior controls the viewTg
		KeyControl kc = new KeyControl(viewTg, simpleObj);
		kc.setSchedulingBounds(bounds);
		root.addChild(kc);
				
		// === Background ===
		Background background = new Background(new Color3f(Color.LIGHT_GRAY));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		// === Lights ===
		AmbientLight ablight = new AmbientLight(true, new Color3f(Color.WHITE));
		ablight.setInfluencingBounds(bounds);
		root.addChild(ablight);

		PointLight ptlight = new PointLight(new Color3f(Color.WHITE), new Point3f(0f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		root.addChild(ptlight);

		return root;
	}
}
