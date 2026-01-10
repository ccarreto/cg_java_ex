package chap09.ex9_1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.Primitive;

import cglib3d.appearance.MyMaterials;
import cglib3d.appearance.TextureAppearance;
import cglib3d.shapes.Axes;
import cglib3d.shapes.FloorLamp2;
import cglib3d.shapes.FloorLamp;
import cglib3d.shapes.Floor;
import cglib3d.shapes.Spot2;
import cglib3d.shapes.Table2;
import cglib3d.shapes.Table;
import cglib3d.shapes.ViewPosition;

public class Ex9_1 extends Frame implements MouseListener {

	// Global bounds
	BoundingSphere bounds = new BoundingSphere();
	PickCanvas pc = null; // PickCanvas to perform picking
	PointLight pLight = null; // Point light of the scene

	public static void main(String[] args) {
		Frame frame = new Ex9_1();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Simple 3D Scene");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex9_1() {
		// === Create a Canvas3D ===
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv1 = new Canvas3D(gc);
		cv1.addMouseListener(this); // Add a mouse listener to the canvas cv1 to get the mouse events.

		// Add the canvas to the frame.
		setLayout(new GridLayout(1, 2));
		add(cv1, BorderLayout.CENTER);

		// === Create a SimpleUniverse ===
		SimpleUniverse su = new SimpleUniverse(cv1, 2); // View has 2 TG.

		// === View Configuration ===
		TransformGroup tg = su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(1);
		Transform3D tx = new Transform3D();
		tx.lookAt(new Point3d(2, 1, 2), new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
		// tx.lookAt(new Point3d(0,2,0), new Point3d(0,0,0), new Vector3d(1,0,0)); //
		// *** must change Vector3d
		tx.invert();
		tg.setTransform(tx);

		View view = su.getViewer().getView();
		// view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		// view.setFieldOfView(Math.PI/3);

		// === Create the content branch (scene graph) ===
		BranchGroup bg = createSceneGraph(su.getViewingPlatform().getMultiTransformGroup().getTransformGroup(0));
		bg.compile();

		// Add the scene graph to the su.
		su.addBranchGraph(bg);

		// === Create a PickCanvas for the view canvas ===
		pc = new PickCanvas(cv1, bg);
		pc.setMode(PickTool.GEOMETRY);

		// === Add a OrbitBehavior to control the view with the mouse ===
		OrbitBehavior orbit = new OrbitBehavior(cv1);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		// === Add a listener to end program when window is closed ===
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		this.addWindowListener(wListener);
	}

	
	private BranchGroup createView(Canvas3D cv, Point3d eye, Point3d center, Vector3d vup) {
		// This function creates a complete view branch for the scene graph.
		View view = new View();
		view.setProjectionPolicy(View.PARALLEL_PROJECTION);
		ViewPlatform vp = new ViewPlatform();
		view.addCanvas3D(cv);
		view.attachViewPlatform(vp);
		view.setPhysicalBody(new PhysicalBody());
		view.setPhysicalEnvironment(new PhysicalEnvironment());
		Transform3D trans = new Transform3D();
		trans.lookAt(eye, center, vup);
		trans.invert();
		TransformGroup tg = new TransformGroup(trans);
		tg.addChild(vp);
		BranchGroup bgView = new BranchGroup();
		bgView.addChild(tg);
		return bgView;
	}

	private BranchGroup createSceneGraph(TransformGroup viewTG) {
		// Receives a secondary view transform group to control the camera with an animation.
		BranchGroup root = new BranchGroup();

		// === Axes ===
		root.addChild(new Axes(new Color3f(Color.RED), 3, 0.5f));

		// === Floor ===
		root.addChild(new Floor(10, -1, 1, new Color3f(Color.DARK_GRAY), new Color3f(Color.WHITE), true));

		// === Table ===
		//Appearance app = new Appearance();
		//app.setMaterial(new Material());
		//Table_2025 table = new Table_2025(app);
		
		// The TextureAppearance class facilitates the creation of an texture type appearance.
		TextureAppearance topApp = new TextureAppearance(this, "images/wood2.jpg", new Material(), true, false);

		// The MyMaterial class facilitates the material configuration.
		Appearance brassApp = new Appearance();
		MyMaterials brass = new MyMaterials(MyMaterials.BRASS);
		brassApp.setMaterial(brass);
		Table2 table = new Table2(topApp, brassApp);
	
		Transform3D tr = new Transform3D();
		tr.setScale(0.5f);
		tr.setTranslation(new Vector3f(0.5f, 0f, 0f));
		TransformGroup tg = new TransformGroup(tr);
		tg.setUserData("tableTG");
		tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg.addChild(table);
		root.addChild(tg);
		
		// === Lamp ===
		TextureAppearance lampshadeApp = new TextureAppearance(this, "images/lampshade-texture.jpg", new Material(), true, true);
		Appearance bronzeApp = new Appearance();
		MyMaterials bronze = new MyMaterials(MyMaterials.BRONZE);
		bronzeApp.setMaterial(bronze);
		
		//FloorLamp_2025 floorLamp = new FloorLamp_2025(bronzeApp);
		FloorLamp2 floorLamp = new FloorLamp2(lampshadeApp, bronzeApp);
		
		tr = new Transform3D();
		tr.setScale(0.5f);
		tr.setTranslation(new Vector3f(-0.3f, 0f, 0f));
		tg = new TransformGroup(tr);
		tg.addChild(floorLamp);
		root.addChild(tg);
		
		// === Background ===
		Background background = new Background(new Color3f(Color.LIGHT_GRAY));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		// === Lights ===
		AmbientLight aLight = new AmbientLight(true, new Color3f(Color.WHITE));
		aLight.setInfluencingBounds(bounds);
		root.addChild(aLight);

		pLight = new PointLight(new Color3f(Color.YELLOW), new Point3f(1f, 1f, 1f), new Point3f(1f, 0f, 0f));
		pLight.setCapability(PointLight.ALLOW_STATE_READ);
		pLight.setCapability(PointLight.ALLOW_STATE_WRITE);
		pLight.setInfluencingBounds(bounds);
		root.addChild(pLight);
		Point3f pLightPos = new Point3f();
		pLight.getPosition(pLightPos);
		root.addChild(new ViewPosition(pLightPos, new Color3f(Color.YELLOW), 20));

		SpotLight sLight = new SpotLight(new Color3f(Color.BLUE), new Point3f(0.5f, 1f, 0f), new Point3f(1f, 0f, 0f),
				new Vector3f(0f, -1f, 0f), (float) (Math.PI / 6.0), 0f);
		sLight.setInfluencingBounds(bounds);
		root.addChild(sLight);
		Point3f sLightPos = new Point3f();
		sLight.getPosition(sLightPos);
		root.addChild(new ViewPosition(sLightPos, new Color3f(Color.BLUE), 20));

		// == Animation of the view (camera) ===
		Alpha alpha = new Alpha(-1, 10000);
		RotationInterpolator viewRotator = new RotationInterpolator(alpha, viewTG);
		viewRotator.setSchedulingBounds(bounds);
		//root.addChild(viewRotator);

		return root;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		pc.setShapeLocation(e);
		PickResult result = pc.pickClosest();

		if (result != null) {
			// Verify if it is the Lampshade.
			Shape3D shape = (Shape3D) result.getNode(PickResult.SHAPE3D);
			if (shape != null) {
				String shapeName = (String) shape.getUserData();
				System.out.println(shapeName);
				if (shapeName == "myLampshade") {
					if (pLight.getEnable())
						pLight.setEnable(false);
					else
						pLight.setEnable(true);
					return;
				}
			}

			// Verify it its is the table.
			Primitive obj = (Primitive) result.getNode(PickResult.PRIMITIVE);
			if (obj != null) {
				String objName = (String) obj.getUserData();
				System.out.println(objName);
				TransformGroup nodeTG = (TransformGroup) result.getNode(PickResult.TRANSFORM_GROUP);
				System.out.println((String) nodeTG.getUserData());

				if (objName == "myTable") {
					// Get the actual geometric transformation of the nodeTG that is the parent of
					// the table.

					Transform3D tr = new Transform3D();
					nodeTG.getTransform(tr);

					// Create a rotation and add it to the actual geometric transformation tr
					Transform3D rot = new Transform3D();
					rot.rotY(Math.PI / 8);
					tr.mul(rot);

					// Set the geometric transformation of the nodeTG with the new tr
					nodeTG.setTransform(tr);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

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
