package team6.photoball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;


public class SimulationClass extends View {

    private Ball ball;
    private Box box;

    //    http://stackoverflow.com/questions/7266836/get-associated-image-drawable-in-imageview-android
//    http://stackoverflow.com/questions/9632114/how-to-find-pixels-color-in-particular-coordinate-in-images
    private ImageView img;

    // Constructor
    public SimulationClass(Context context, float currentX, float currentY) {
        super(context);

        box = new Box();  // ARGB
        int ball_color = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("ball_preference_key",0xff006600);
        ball = new Ball(ball_color, currentX, currentY);
        ball.setRadius(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("size_preference_key", 60));
        ball.setSpeed((float)PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("speed_preference_key",35));

        // To enable keypad
        this.setFocusable(true);
        this.requestFocus();
        // To enable touch mode
        this.setFocusableInTouchMode(true);

    }

    // Called back to draw the view. Also called after invalidate().
    @Override
    synchronized protected void onDraw(Canvas canvas) {
        if(MainActivity.mImageView != null && MainActivity.mBitmap != null && !MainActivity.mBitmap.isRecycled()) {
            // Draw the components
            box.draw(canvas);
            ball.draw(canvas);
            // Update the position of the ball, including collision detection and reaction.
            ball.moveWithCollisionDetection(box);

            //Delay
            /*try {
                Thread.sleep(30);
            } catch (InterruptedException e) {}*/
        }
        invalidate();
    }

    // Called back when the view is first created or its size changes.
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        // Set the movement bounds for the ball
        box.set(0, 0, w, h);
    }

    public void setPosition (float currentX, float currentY) {
        ball.setPosition(currentX, currentY);
    }
}
