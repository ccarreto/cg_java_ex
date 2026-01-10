package chap12.ex_sound3d;

import com.sun.j3d.utils.universe.SimpleUniverse;

import cglib2d.utilities.Utils;
import cglib3d.appearance.TextureAppearance;
import cglib3d.shapes.Axes;
import cglib3d.shapes.Floor;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.*;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Ex_Sound3D extends Frame implements KeyListener {
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 4.0);
	TransformGroup moveTg = null;

	private Canvas3D cv;
	private Canvas3D offScreenCanvas;
	private View view;

	public static void main(String[] args) {
		Frame frame = new Ex_Sound3D();
		frame.setPreferredSize(new Dimension(400, 400));
		frame.setTitle("Sound3D");
		frame.pack();
		frame.setVisible(true);
	}

	public Ex_Sound3D() {
		// Create canvas
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		cv = new Canvas3D(gc);

		cv.addKeyListener(this);

		setLayout(new BorderLayout());
		add(cv, BorderLayout.CENTER);

		// Create simple universe
		SimpleUniverse su = new SimpleUniverse(cv);

		// Position of the view
		Transform3D viewTr = new Transform3D();
		viewTr.lookAt(new Point3d(-2.5, 2.5, 2.5), new Point3d(0.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));
		viewTr.invert();
		su.getViewingPlatform().getViewPlatformTransform().setTransform(viewTr);

		// Create audio device
		AudioDevice audioDev = new JavaSoundMixer(su.getViewer().getPhysicalEnvironment());
		audioDev.initialize();

		// Create scene graph
		BranchGroup bg = createSceneGraph();
		bg.compile();
		su.addBranchGraph(bg);

		// Add a OrbitBehavior to control the view with the mouse
		OrbitBehavior orbit = new OrbitBehavior(cv);
		orbit.setSchedulingBounds(bounds);
		su.getViewingPlatform().setViewPlatformBehavior(orbit);

		// Create listener to end program when window is closed
		WindowListener wListener = new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		};
		addWindowListener(wListener);

		// Button
		Button button = new Button("Save image");
		button.addActionListener(e -> buttonPressed());
		add(button, BorderLayout.SOUTH);

		createOffScreenCanvas(su, gc);
	}

	void createOffScreenCanvas(SimpleUniverse su, GraphicsConfiguration gc) {
		// Get the actual view.
		view = su.getViewer().getView();

		// Create an off screen canvas similar to the one in use.
		offScreenCanvas = new Canvas3D(gc, true);
		Screen3D onScreen = cv.getScreen3D();
		Screen3D offScreen = offScreenCanvas.getScreen3D();
		Dimension dim = onScreen.getSize();
		offScreen.setSize(dim);
		offScreen.setPhysicalScreenWidth(onScreen.getPhysicalScreenWidth());
		offScreen.setPhysicalScreenHeight(onScreen.getPhysicalScreenHeight());

		// Position the off screen canvas at the same screen position.
		Point loc = cv.getLocationOnScreen();
		offScreenCanvas.setOffScreenLocation(loc);
	}

	private void buttonPressed() {
		BufferedImage bi = captureImage();
		saveImage(bi);
	}

	public BufferedImage captureImage() {
		// Retrieve the current dimensions of the on-screen Canvas3D. The off-screen
		// image will be rendered with the same resolution.
		Dimension dim = cv.getSize();

		// Temporarily stop the View and attach an off-screen Canvas3D. This allows
		// rendering the scene into an image buffer instead of directly onto the screen.
		view.stopView();
		view.addCanvas3D(offScreenCanvas);

		// Create a BufferedImage that will store the rendered frame. The image
		// dimensions match the Canvas3D dimensions.
		BufferedImage bImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);

		// Create an ImageComponent2D that wraps the BufferedImage. This component acts
		// as the off-screen rendering target.

		ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage);

		// Assign the ImageComponent2D as the off-screen buffer. The next rendered frame
		// will be drawn into this image instead of the visible canvas.
		offScreenCanvas.setOffScreenBuffer(buffer);

		// Restart the View and explicitly trigger off-screen rendering. The rendering
		// pipeline draws one frame into the off-screen buffer.
		view.startView();
		offScreenCanvas.renderOffScreenBuffer();
		offScreenCanvas.waitForOffScreenRendering();

		// Retrieve the rendered image from the off-screen buffer.
		bImage = offScreenCanvas.getOffScreenBuffer().getImage();

		// Detach the off-screen Canvas3D and restore the original View configuration.
		view.removeCanvas3D(offScreenCanvas);

		// Return the captured frame as a BufferedImage.
		return bImage;
	}

	public void saveImage(BufferedImage bImage) {
		// Save image to file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagens JPEG", "jpeg");
		fileChooser.setFileFilter(filter);

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// File file = new File(fileChooser.getSelectedFile().toString() + ".jpeg");
			try {
				ImageIO.write(bImage, "jpeg", file);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();

		// === Floor ===
		root.addChild(new Floor(21, -1.5f, 1.5f, new Color3f(Color.LIGHT_GRAY), new Color3f(Color.GRAY), false));

		// === Axes ===
		root.addChild(new Axes(new Color3f(Color.RED), 3, 1f));

		// === Obj ===
		BranchGroup obj = loadOBJ("models/bird/bird_13.obj", "models/bird/bird_13.jpg");
		// root.addChild(obj);

		// === TransformGroup to move the obj with keyboard ===
		Transform3D tr = new Transform3D();
		tr.setRotation(new AxisAngle4d(1, 0, 0, Math.toRadians(-90)));
		tr.setScale(0.1);
		TransformGroup tg = new TransformGroup(tr);
		tg.addChild(obj);

		moveTg = new TransformGroup();
		moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		moveTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		root.addChild(moveTg);
		moveTg.addChild(tg);

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

		// === Sound ===
		// URL url = this.getClass().getClassLoader().getResource("images/river.wav");
		URL url = this.getClass().getClassLoader().getResource("models/bird/bird.wav");
		MediaContainer mc = new MediaContainer(url);
		BackgroundSound bSound = new BackgroundSound(mc, 0.01f);
		bSound.setSoundData(mc);
		bSound.setLoop(Sound.INFINITE_LOOPS);
		bSound.setSchedulingBounds(bounds);

		bSound.setInitialGain(1.0f);
		bSound.setEnable(true);
		root.addChild(bSound);

		// PointSound doesn't work with modern Java versions.
		PointSound pSound = new PointSound();
		url = this.getClass().getClassLoader().getResource("models/bird/bird.wav");
		mc = new MediaContainer(url);
		pSound.setSoundData(mc);
		pSound.setLoop(Sound.INFINITE_LOOPS);
		pSound.setInitialGain(1f);
		float[] distances = { 1f, 2f };
		float[] gains = { 1f, 0.1f, 0.01f };
		pSound.setDistanceGain(distances, gains);
		pSound.setSchedulingBounds(bounds);
		pSound.setEnable(true);
		// moveTg.addChild(pSound);

		// === Background ===
		ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGB,
				Utils.getImage(this, "images/clouds.jpg"));
		Background background = new Background(image);
		background.setApplicationBounds(bounds);
		root.addChild(background);

		return root;
	}

	protected BranchGroup loadOBJ(String objFileName, String textureFileName) {
		ObjectFile file = new ObjectFile();
		Scene scene = null;

		// Load the .obj file to a Scene object.
		URL url = this.getClass().getClassLoader().getResource(objFileName);
		try {
			scene = file.load(url);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Create texture appearance based on the object's texture file.
		TextureAppearance app = new TextureAppearance(this, textureFileName, new Material(), true, false);

		// Convert from obj to Shape3D.
		((Shape3D) scene.getSceneGroup().getChild(0)).setAppearance(app);

		return scene.getSceneGroup();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();

		if (key == 'q') { // rotate left
			doRotation(Math.toRadians(-1));
		}

		if (key == 'a') { // rotate right
			doRotation(Math.toRadians(1));
		}

		if (key == 'p') { // forward
			doTranslation(new Vector3f(0.1f, 0f, 0f));
		}

		if (key == 'l') { // backward
			doTranslation(new Vector3f(-0.1f, 0f, 0f));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	private void doRotation(double t) {
		// Standard code to add a transformation to the actual transformation 
		// of a TransformGroup.

		// Get old transformation of the TransformGroup.
		Transform3D oldTr = new Transform3D();
		moveTg.getTransform(oldTr);

		// Create the new transformation to add.
		Transform3D newTr = new Transform3D();
		newTr.rotY(t);

		// Add the new transformation by multiplying the transformations.
		oldTr.mul(newTr);

		// Set the new transformation.
		moveTg.setTransform(oldTr);
	}

	private void doTranslation(Vector3f v) {
		Transform3D oldTr = new Transform3D();
		moveTg.getTransform(oldTr);

		Transform3D newTr = new Transform3D();
		newTr.setTranslation(v);

		oldTr.mul(newTr);

		moveTg.setTransform(oldTr);
	}
}
