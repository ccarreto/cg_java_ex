package chap04.ex_simple_game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Ex_Simple_Game extends JFrame {

	public static void main(String[] args) {
		JFrame frame = new Ex_Simple_Game();
		frame.setTitle("Simple Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new GamePanel();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}

class GamePanel extends JPanel implements Runnable, KeyListener {

	// Scenario objects
	Shape obj1 = null;
	Shape obj2 = null;

	Shape obj3 = null;
	Shape obj4 = null;

	// Player v1
	Shape playerV1 = null;

	// Player V2
	int playerX = 50;
	int playerY = 300;
	int speed = 4;

	int playerS = 64; // Size of the sprites.
	String direction = "down"; // Initial direction of the player.
	int spriteNum = 1;
	int spriteCounter = 0;
	boolean newKey = false;
	BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;

	// Flags for the keys pressed
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean upPressed = false;
	boolean downPressed = false;

	// Auxiliary AffineTransform
	AffineTransform at = new AffineTransform();

	// Update variables
	int angle = 45;

	// Thread
	Thread thread = null;

	public GamePanel() {
		setPreferredSize(new Dimension(400, 400));
		setDoubleBuffered(true);

		addKeyListener(this);
		setFocusable(true);

		getPlayerImages();

		thread = new Thread(this);
		thread.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		drawScenario(g2);
		// drawPlayerV1(g2);
		drawPlayerV2(g2);
	}

	@Override
	public void run() {
		while (thread != null) {

			// Update
			scenarioUpdate();
			//playerUpdateV1();
			playerUpdateV2();

			// Draw
			repaint();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void drawScenario(Graphics2D g2) {
		// Initial objects
		obj1 = new Rectangle2D.Double(-100, -10, 200, 20);
		obj2 = new Rectangle2D.Double(-10, -100, 20, 200);
		at.setToTranslation(280, 280); // Using setTo because we want to replace and not accumulate the existing
										// transformation.
		at.rotate(Math.toRadians(angle)); // Rotation is the first transformation to be applied, when the object is
											// still centered at the origin.
		obj1 = at.createTransformedShape(obj1);
		obj2 = at.createTransformedShape(obj2);

		g2.setColor(Color.RED);
		g2.fill(obj1);
		g2.fill(obj2);

		// Other objects
		// Use different angle variables to control different rotation speeds and
		// directions
		obj3 = new Ellipse2D.Double(-50, -10, 100, 20);
		obj4 = new Rectangle2D.Double(-10, -10, 20, 20);

		at.setToTranslation(100, 100);
		at.rotate(Math.toRadians(angle));
		obj3 = at.createTransformedShape(obj3);

		at.setToTranslation(100, 100);
		at.rotate(Math.toRadians(-angle));
		at.translate(70, 0);
		obj4 = at.createTransformedShape(obj4);

		g2.setColor(Color.BLUE);
		g2.fill(obj3);
		g2.setColor(Color.RED);
		g2.fill(obj4);
	}

	private void scenarioUpdate() {
		angle = (angle + 2) % 360;
	}

	private void drawPlayerV1(Graphics2D g2) {
		// In this version, the player is implemented as a Shape.
		playerV1 = new Ellipse2D.Double(-25, -25, 50, 50);

		at.setToTranslation(playerX, playerY); // Using setTo because we want to replace and not accumulate the existing

		playerV1 = at.createTransformedShape(playerV1);

		g2.setColor(Color.GREEN);
		g2.fill(playerV1);
	}

	private void playerUpdateV1() {
		if (upPressed || downPressed || rightPressed || leftPressed) {
			if (upPressed) {
				playerY -= speed;
			} else if (downPressed) {
				playerY += speed;
			} else if (leftPressed) {
				playerX -= speed;
			} else if (rightPressed) {
				playerX += speed;
			}
		}
	}

	private void drawPlayerV2(Graphics2D g2) {
		// The player can move in four possible directions: up, down, left, and right.
		// Each direction has two different images associated with it, which, when
		// shown alternately, create a movement effect.

		BufferedImage img = null;

		switch (direction) {
		case "up":
			if (spriteNum == 1)
				img = up1;
			else if (spriteNum == 2)
				img = up2;
			break;
		case "down":
			if (spriteNum == 1)
				img = down1;
			else if (spriteNum == 2)
				img = down2;
			break;
		case "left":
			if (spriteNum == 1)
				img = left1;
			else if (spriteNum == 2)
				img = left2;
			break;
		case "right":
			if (spriteNum == 1)
				img = right1;
			else if (spriteNum == 2)
				img = right2;
			break;
		}

		g2.drawImage(img, playerX, playerY, playerS, playerS, this);
	}

	private void playerUpdateV2() {
		// If an arrow key was pressed, update the direction and position according to
		// the key.
		if (upPressed || downPressed || rightPressed || leftPressed) {
			if (upPressed) {
				playerY -= speed;
				direction = "up";
			} else if (downPressed) {
				playerY += speed;
				direction = "down";
			} else if (leftPressed) {
				playerX -= speed;
				direction = "left";
			} else if (rightPressed) {
				playerX += speed;
				direction = "right";
			}

			// Every X frames, switch between the two sprites in one direction to create the
			// effect of movement.
			// The value 5 (X), indicates that the sprite changes every 5 frames
			// Increase or decrease the value for a smaller or faster change
			spriteCounter++;
			if (spriteCounter > 5) {
				if (spriteNum == 1)
					spriteNum = 2;
				else if (spriteNum == 2)
					spriteNum = 1;
				spriteCounter = 0;
			}
		}

		checkPanelLimits();
		// checkCollisionV1();
		checkCollisionV2();
	}

	private void getPlayerImages() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void checkPanelLimits() {
		if (playerX < 0)
			playerX = 0;
		if (playerX + playerS > 400 - 1)
			playerX = 400 - 1 - playerS;
		if (playerY < 0)
			playerY = 0;
		if (playerY + playerS > 400 - 1)
			playerY = 400 - 1 - playerS;
	}

	private void checkCollisionV1() {
		// Check intersection between the obj area and the rectangular bounds of the player's sprite
		Rectangle playerBounds = new Rectangle(playerX, playerY, playerS, playerS);

		if (obj1 != null && obj2 != null)
			if (obj1.intersects(playerBounds) || obj2.intersects(playerBounds)) {
				playerX = 50;
				playerY = 300;
			}
	}

	private void checkCollisionV2() {
		// Check if obj contains the point that represents the center of the player's sprite
		Point2D.Double p = new Point2D.Double(playerX + playerS / 2, playerY + playerS / 2);

		if (obj1 != null && obj2 != null)
			if (obj1.contains(p) || obj2.contains(p)) {
				playerX = 50;
				playerY = 300;
			}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			leftPressed = true;
			break;
		case KeyEvent.VK_RIGHT:
			rightPressed = true;
			break;
		case KeyEvent.VK_UP:
			upPressed = true;
			break;
		case KeyEvent.VK_DOWN:
			downPressed = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		upPressed = downPressed = rightPressed = leftPressed = false;
	}
}
