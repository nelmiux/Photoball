package team6.photoball;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rosar on 4/4/2016.
 */
public class SimulationClass extends View {

    public final ArrayList balls = new ArrayList();

    AnimatorSet animation = null;

    public SimulationClass(Context context) {

        super(context);

        // Animate background color

        // Note that setting the background color will automatically invalidate the

        // view, so that the animated color, and the bouncing balls, get redisplayed on

        // every frame of the animation.

        this.start();
    }

    public void start() {

        ShapeHolder newBall = addBall(100, 100);

        // Bouncing animation with squash and stretch

        float startY = newBall.getY();

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        float endY = height - 50f;

        float h = (float)height;

        Random r = new Random();
        int e = r.nextInt(height);

        float eventY = e;

        int duration = (int)(500 * ((h - eventY)/h));

        ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);

        bounceAnim.setDuration(duration);

        bounceAnim.setInterpolator(new AccelerateInterpolator());

        ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),

                newBall.getX() + 50f);

        squashAnim1.setDuration(duration/4);

        squashAnim1.setRepeatCount(squashAnim1.INFINITE);

        squashAnim1.setRepeatMode(squashAnim1.REVERSE);

        squashAnim1.setInterpolator(new DecelerateInterpolator());

        ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),

                newBall.getWidth() + 50);

        squashAnim2.setDuration(duration/4);

        squashAnim2.setRepeatCount(squashAnim2.INFINITE);

        squashAnim2.setRepeatMode(squashAnim2.REVERSE);

        squashAnim2.setInterpolator(new DecelerateInterpolator());

        ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,

                endY + 25f);

        stretchAnim1.setDuration(duration/4);

        stretchAnim1.setRepeatCount(stretchAnim1.INFINITE);

        stretchAnim1.setInterpolator(new DecelerateInterpolator());

        stretchAnim1.setRepeatMode(stretchAnim1.REVERSE);

        ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",

                newBall.getHeight(), newBall.getHeight() - 25);

        stretchAnim2.setDuration(duration/4);

        stretchAnim2.setRepeatCount(stretchAnim2.INFINITE);

        stretchAnim2.setInterpolator(new DecelerateInterpolator());

        stretchAnim2.setRepeatMode(stretchAnim2.REVERSE);

        ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,

                startY);

        bounceBackAnim.setDuration(duration);

        bounceBackAnim.setInterpolator(new DecelerateInterpolator());

        // Sequence the down/squash&stretch/up animations

        AnimatorSet bouncer = new AnimatorSet();

        bouncer.play(bounceAnim).before(squashAnim1);

        bouncer.play(squashAnim1).with(squashAnim2);

        bouncer.play(squashAnim1).with(stretchAnim1);

        bouncer.play(squashAnim1).with(stretchAnim2);

        bouncer.play(bounceBackAnim).after(stretchAnim2);

        // Fading animation - remove the ball when the animation is done

        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);

        fadeAnim.setDuration(250);

        fadeAnim.addListener(new AnimatorListenerAdapter() {

                /*@Override

                public void onAnimationEnd(Animator animation) {

                    balls.remove(((ObjectAnimator)animation).getTarget());

                }*/

        });

        // Sequence the two animations to play one after the other

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(bouncer).before(fadeAnim);

        // Start the animation

        animatorSet.start();

    }

    private ShapeHolder addBall(float x, float y) {

        OvalShape circle = new OvalShape();

        circle.resize(50f, 50f);

        ShapeDrawable drawable = new ShapeDrawable(circle);

        ShapeHolder shapeHolder = new ShapeHolder(drawable);

        shapeHolder.setX(x - 25f);

        shapeHolder.setY(y - 25f);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        int ball_color = prefs.getInt("ball_preference_key", 0);

        int red = 0x000000ff & (ball_color >> 16);//(int)(Math.random() * 255);

        int green = 0x000000ff & (ball_color >> 8);//(int)(Math.random() * 255);

        int blue = 0x000000ff & ball_color;//(int)(Math.random() * 255);

        int color = 0xff000000 | red << 16 | green << 8 | blue;

        Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);

        int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;

        RadialGradient gradient = new RadialGradient(37.5f, 12.5f,

                50f, color, darkColor, Shader.TileMode.CLAMP);

        paint.setShader(gradient);

        shapeHolder.setPaint(paint);

        balls.add(shapeHolder);

        return shapeHolder;
    }

    @Override

    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < balls.size(); ++i) {

            ShapeHolder shapeHolder = (ShapeHolder) balls.get(i);

            canvas.save();

            canvas.translate(shapeHolder.getX(), shapeHolder.getY());

            shapeHolder.getShape().draw(canvas);

            canvas.restore();

        }

    }

}
