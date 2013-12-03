//TODO update the game to have a maze background, and some way to detect where walls all for collision detection.
//TODO Change the spaceship (sprite 2) to be something more suitable to a navigate a maze.
//TODO Sprite2's location seems to be the top left of it's bounding rectangle instead of the center.
package com.TeamAmazing.drawing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.TeamAmazing.game.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameBoard extends View {

	private Paint p;
	private List<Point> starField = null;
	private int starAlpha = 80;
	private int starFade = 2;
	private Rect sprite2Bounds = new Rect(0, 0, 0, 0);
	private Point sprite2;
	// TODO make velocity and friction values private and use getter and setter
	// methods.
	public float sprite2XVelocity = 0;
	public float sprite2YVelocity = 0;
	public float xFriction = 0;
	public float yFriction = 0;
	private final int maxSpeed = 15;
	private Bitmap bm2 = null;
	// Collision flag and point
	private boolean collisionDetected = false;
	private Point lastCollision = new Point(-1, -1);
	// acceleration flag
	private boolean isAccelerating = false;
	private int xTouch;
	private int yTouch;

	private static final int NUM_OF_STARS = 25;

	// Allow our controller to get and set the sprite positions

	// sprite 2 setter
	synchronized public void setSprite2(int x, int y) {
		sprite2 = new Point(x, y);
	}

	// sprite 2 getter
	synchronized public int getSprite2X() {
		return sprite2.x;
	}

	synchronized public int getSprite2Y() {
		return sprite2.y;
	}

	synchronized public void resetStarField() {
		starField = null;
	}

	// expose sprite bounds to controller
	synchronized public int getSprite2Width() {
		return sprite2Bounds.width();
	}

	synchronized public int getSprite2Height() {
		return sprite2Bounds.height();
	}

	// return the point of the last collision
	synchronized public Point getLastCollision() {
		return lastCollision;
	}

	// return the collision flag
	synchronized public boolean wasCollisionDetected() {
		return collisionDetected;
	}

	public GameBoard(Context context, AttributeSet aSet) {
		super(context, aSet);
		p = new Paint();
		// load our bitmap and set the bounds for the controller
		sprite2 = new Point(-1, -1);
		// Define a matrix so we can rotate the asteroid
		bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.ufo);
		sprite2Bounds = new Rect(0, 0, bm2.getWidth(), bm2.getHeight());
	}

	synchronized private void initializeStars(int maxX, int maxY) {
		starField = new ArrayList<Point>();
		for (int i = 0; i < NUM_OF_STARS; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			starField.add(new Point(x, y));
		}
		collisionDetected = false;
	}

	// TODO This usually works, but not quite always, perhaps there is an issue
	// in the bitmaps.
	private boolean checkForCollision() {
//		if (sprite1.x < 0 && sprite2.x < 0 && sprite1.y < 0 && sprite2.y < 0)
//			return false;
//		Rect r1 = new Rect(sprite1.x, sprite1.y, sprite1.x
//				+ sprite1Bounds.width(), sprite1.y + sprite1Bounds.height());
//		Rect r2 = new Rect(sprite2.x - sprite2Bounds.width() / 2, sprite2.y
//				- sprite2Bounds.height() / 2, sprite2.x + sprite2Bounds.width()
//				/ 2, sprite2.y + sprite2Bounds.height() / 2);
//		Rect r3 = new Rect(r1);
//		if (r1.intersect(r2)) {
//			for (int i = r1.left; i < r1.right; i++) {
//				for (int j = r1.top; j < r1.bottom; j++) {
//					// TODO why do we have r3? Why not just use r1?
//					if (bm1.getPixel(i - r3.left, j - r3.top) != Color.TRANSPARENT) {
//						if (bm2.getPixel(i - r2.left, j - r2.top) != Color.TRANSPARENT) {
//							lastCollision = new Point(sprite2.x
//									- sprite2Bounds.width() / 2 + i - r2.left,
//									sprite2.y - sprite2Bounds.height() / 2 + j
//											- r2.top);
//							return true;
//						}
//					}
//				}
//			}
//		}
//		lastCollision = new Point(-1, -1);
		return false;
	}

	@Override
	synchronized public void onDraw(Canvas canvas) {

		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);

		if (starField == null) {
			initializeStars(canvas.getWidth(), canvas.getHeight());
		}

		p.setColor(Color.CYAN);
		p.setAlpha(starAlpha += starFade);
		if (starAlpha >= 252 || starAlpha <= 80)
			starFade = starFade * -1;
		p.setStrokeWidth(5);
		for (int i = 0; i < NUM_OF_STARS; i++) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}

		// TODO why do we check if (sprite2.x >= 0)?
		if (sprite2.x >= 0) {
			// Draws the bitmap, with sprite2.x,y as the center
			canvas.drawBitmap(bm2, sprite2.x - sprite2Bounds.width() / 2,
					sprite2.y - sprite2Bounds.height() / 2, null);
		}
		collisionDetected = checkForCollision();
		if (collisionDetected) {
			p.setColor(Color.RED);
			p.setAlpha(255);
			p.setStrokeWidth(5);
			canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
					lastCollision.x + 5, lastCollision.y + 5, p);
			canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
					lastCollision.x - 5, lastCollision.y + 5, p);
		}
	}

	// Method for getting touch state--requires android 2.1 or greater
	// The touch event only triggers if a down event happens inside this view,
	// however move events and up events can happen outside the view
	@Override
	synchronized public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xTouch = Math.round(ev.getX());
			yTouch = Math.round(ev.getY());
			isAccelerating = true;
			break;
		case MotionEvent.ACTION_UP:
			isAccelerating = false;
			break;
		case MotionEvent.ACTION_MOVE:
			xTouch = Math.round(ev.getX());
			yTouch = Math.round(ev.getY());
			isAccelerating = true;
			break;
		}
		return true;
	}

	public void updateVelocity() {
		if (isAccelerating) {
			xFriction = 0;
			yFriction = 0;
			final float dimFactor = .49f;
			final float touchFactor = .25f;
			sprite2XVelocity = touchFactor
					* (xTouch - sprite2.x + Math.round(dimFactor
							* sprite2XVelocity));
			sprite2YVelocity = touchFactor
					* (yTouch - sprite2.y + Math.round(dimFactor
							* sprite2YVelocity));
			// Enforce max speed;
			int accSpeed = (int) Math.round(Math.sqrt(Math.pow(
					sprite2XVelocity, 2) + Math.pow(sprite2YVelocity, 2)));
			if (accSpeed > maxSpeed + 1) {
				sprite2XVelocity = sprite2XVelocity * maxSpeed / accSpeed;
				// TODO interestingly sprite2Velocity.y *= maxSpeed / accSpeed;
				// doesn't
				// work. Why?
				sprite2YVelocity = sprite2YVelocity * maxSpeed / accSpeed;
			}
		} else {
			// Decrease speed with friction.
			final float friction = .05f;
			float speed = (float) Math.sqrt(Math.pow(sprite2XVelocity, 2)
					+ Math.pow(sprite2YVelocity, 2));
			if ((Math.abs(sprite2XVelocity) + Math.abs(sprite2YVelocity)) > 0) {
				xFriction = speed
						* friction
						* -1
						* sprite2XVelocity
						/ (Math.abs(sprite2XVelocity) + Math
								.abs(sprite2YVelocity));
				yFriction = speed
						* friction
						* -1
						* sprite2YVelocity
						/ (Math.abs(sprite2XVelocity) + Math
								.abs(sprite2YVelocity));
			}
			sprite2XVelocity = sprite2XVelocity + xFriction;
			sprite2YVelocity = sprite2YVelocity + yFriction;
		}
	}

	public void resetSprite2Velocity() {
		sprite2XVelocity = 0;
		sprite2YVelocity = 0;
		xFriction = 0;
		yFriction = 0;
	}

	// TODO Better boundary checking
	// What this does: update the position, then check if we are outside
	// the boundary,
	// if we are: reverse direction.
	// This sort of boundary checking has major issues, because the velocity
	// gets overwritten by the next call. So we can head outside the
	// boundary with two successive impulses.
	// Something about how I changed update velocity broke this.
	public void updatePosition() {
		// TODO do I need this.getWidth() and this.getSprite2Width() instead?
		int sprite2MaxX = getWidth() - getSprite2Width();
		int sprite2MaxY = getHeight() - getSprite2Height();
		sprite2.x = Math.round(sprite2.x + sprite2XVelocity);
		if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
			sprite2XVelocity *= -1;
		}
		sprite2.y = Math.round(sprite2.y + sprite2YVelocity);
		if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
			sprite2YVelocity *= -1;
		}
		setSprite2(sprite2.x, sprite2.y);
	}
}
