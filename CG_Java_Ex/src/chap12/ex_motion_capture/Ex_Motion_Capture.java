package chap12.ex_motion_capture;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib3d.shapes.MyCone;
import cglib3d.appearance.PNoiseTextureAppearence;
import cglib3d.appearance.PerlinNoise;
import cglib3d.appearance.TextureAppearance;
import cglib3d.shapes.Floor;
import cglib3d.shapes.Pyramid;

public class Ex_Motion_Capture extends Frame {
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4.0);

	float[][] markers = new float[1700][48]; // Coordinates of the markers of the skeleton over time.

	GeometryArray geom = null; // Geometry of the object.
	Updater updater = null; // Object to update the geometry.

	public static void main(String[] args) {
		Frame frame = new Ex_Motion_Capture();
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setTitle("Motion Capture");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex_Motion_Capture() {
		// Read data from CSV file
		readData(markers, "images/data.csv");

		// Create the object with the algorithm to update the geometry.
		updater = new Updater();

		// Create Canvas
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);
		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		// Create SimpleUniverse and initial position of the view.
		SimpleUniverse su = new SimpleUniverse(cv);
		Transform3D locator = new Transform3D();
		locator.lookAt(new Point3d(-4.5, 4.5, 4.5), new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
		locator.invert();
		su.getViewingPlatform().getViewPlatformTransform().setTransform(locator);

		// Create Scene Graph
		BranchGroup bg = createSceneGraph();
		bg.compile();
		su.addBranchGraph(bg);

		// Create Orbit Behavior
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

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		// === Floor ===
		root.addChild(new Floor(15, -2f, 2f, new Color3f(Color.LIGHT_GRAY), new Color3f(Color.GRAY), true));

		// === Object ===
		// Object creation
		Appearance app = new Appearance();
		app.setColoringAttributes(new ColoringAttributes(new Color3f(Color.RED), ColoringAttributes.FASTEST));
		app.setPointAttributes(new PointAttributes(10f, true));
		app.setLineAttributes(new LineAttributes(5f, LineAttributes.PATTERN_SOLID, true));

		// geom = getPointCloudGeometry();
		geom = getSkeletonGeometry();
		Shape3D obj = new Shape3D(geom, app);
		// root.addChild(obj);

		// Object positioning
		Transform3D tr = new Transform3D();
		tr.setScale(2);
		TransformGroup tg = new TransformGroup(tr);
		tg.addChild(obj);
		root.addChild(tg);

		// === Update Behavior ===
		UpdateBehavior ub = new UpdateBehavior();
		ub.setSchedulingBounds(bounds);
		root.addChild(ub);

		// === Object With 3D Texture ===
		//app = new TextureAppearance(this, "images/stone.jpg", new Material(), true, true);
		app = new PNoiseTextureAppearence(new PerlinNoise());
		Pyramid pyramid = new Pyramid(1.5f, 0.7f, app);
		//MyCone cone = new MyCone(4, 0.5f, 0.3f, app);
		// root.addChild(cone);
		tr = new Transform3D();
		tr.setTranslation(new Vector3f(-1f, 0f, 0f));
		tg = new TransformGroup(tr);
		tg.addChild(pyramid);
		//tg.addChild(cone);
		root.addChild(tg);

		// === Lights ===
		AmbientLight light = new AmbientLight(true, new Color3f(Color.WHITE));
		light.setInfluencingBounds(bounds);
		root.addChild(light);

		PointLight ptlight = new PointLight(new Color3f(Color.WHITE), new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		root.addChild(ptlight);

		ptlight = new PointLight(new Color3f(Color.WHITE), new Point3f(-3f, 3f, -3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bounds);
		root.addChild(ptlight);

		// === Background ===
		Background background = new Background(new Color3f(Color.DARK_GRAY));
		background.setApplicationBounds(bounds);
		root.addChild(background);

		return root;
	}

	private void readData(float markers[][], String file) {
		// Standard algorithm for reading a text data file, where each line contains
		// a set of values separated by a specific delimiter character.
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";"; // Character that separates the values

		try {
			URL url = getClass().getClassLoader().getResource(file);
			br = new BufferedReader(new InputStreamReader(url.openStream()));

			int m = 0;
			while ((line = br.readLine()) != null) { // Read a line from the file

				String[] data = line.split(cvsSplitBy); // Split the values of the line
				for (int i = 0; i < data.length; i++)
					markers[m][i] = Float.parseFloat(data[i]); // Convert values to float and save them
				m++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private GeometryArray getPointCloudGeometry() {
		// Creates the geometry of the object as a point cloud (only vertices).

		GeometryArray g = new PointArray(16, PointArray.COORDINATES | PointArray.BY_REFERENCE);
		g.setCapability(PointArray.ALLOW_REF_DATA_READ);
		g.setCapability(PointArray.ALLOW_REF_DATA_WRITE);

		float[] coords = new float[48];

		for (int i = 0; i < 48; i++)
			coords[i] = markers[0][i];

		g.setCoordRefFloat(coords);

		return g;
	}

	private GeometryArray getSkeletonGeometry() {
		// Creates the object's geometry as a skeletal representation, where vertices
		// are connected by edges to approximate the structure of the human body.

		IndexedLineArray g = new IndexedLineArray(16, IndexedLineArray.COORDINATES | IndexedLineArray.BY_REFERENCE, 30);
		g.setCapability(IndexedLineArray.ALLOW_REF_DATA_READ);
		g.setCapability(IndexedLineArray.ALLOW_REF_DATA_WRITE);

		float[] coords = new float[48];

		for (int i = 0; i < 48; i++)
			coords[i] = markers[0][i];

		g.setCoordRefFloat(coords);

		int indices[] = { 3, 0, 0, 14, 14, 11, // right arm
				3, 1, 1, 9, 9, 12, // left arm
				6, 10, 10, 2, 2, 5, // right leg
				6, 13, 13, 15, 15, 7, // left leg
				6, 3, // back
				4, 3, 4, 8 }; // head

		g.setCoordinateIndices(0, indices);

		return g;
	}

	class UpdateBehavior extends Behavior {
		/*
		 * Custom Java 3D Behavior responsible for triggering geometry updates. This
		 * class does NOT modify the geometry directly. Instead, it signals the Java 3D
		 * renderer when an update should occur by invoking updateData() through a
		 * GeometryUpdater object.
		 */

		@Override
		public void initialize() {
			wakeupOn(new WakeupOnElapsedFrames(0)); // Wakeup the behavior as soon as possible
		}

		@Override
		public void processStimulus(Enumeration criteria) {

			/*
			 * Request a geometry update by passing a GeometryUpdater object to the geometry
			 * node.
			 *
			 * The actual update algorithm is NOT executed here. Java 3D requires geometry
			 * modifications to be performed by the rendering engine at a safe point in the
			 * rendering pipeline. This method merely signals that an update should occur.
			 */
			geom.updateData(updater);

			wakeupOn(new WakeupOnElapsedFrames(0));
		}
	}

	class Updater implements GeometryUpdater {
		/*
		 * Implements the GeometryUpdater interface, which defines the updateData()
		 * method. This method contains the actual algorithm responsible for modifying
		 * the geometry's vertex data.
		 *
		 * This separation ensures that geometry updates comply with Java 3D's threading
		 * and rendering constraints.
		 */

		int m; // Index of the line of the markers array.

		public Updater() {
			m = 0;
		}

		@Override
		public void updateData(Geometry geometry) {

			/*
			 * Obtain a direct reference to the internal coordinate array of the
			 * GeometryArray. Using getCoordRefFloat() avoids unnecessary copying and allows
			 * efficient in-place updates.
			 */
			float[] coords = ((GeometryArray) geometry).getCoordRefFloat();

			/*
			 * Update the geometry's vertex coordinates using the next set of values stored
			 * in the markers array.
			 */
			for (int i = 0; i < 48; i++)
				coords[i] = markers[m][i];

			/*
			 * Advance to the next marker set, wrapping around when the end of the array is
			 * reached.
			 */
			m = (m + 1) % markers.length;
		}
	}
}
