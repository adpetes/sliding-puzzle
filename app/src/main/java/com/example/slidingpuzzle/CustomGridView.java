package com.example.slidingpuzzle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

public class CustomGridView extends GridView {
    private GestureDetector gestureDetector;

    public CustomGridView(Context context) {
        super(context);
        setupGestureDetect(context);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupGestureDetect(context);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupGestureDetect(context);
    }

    private void setupGestureDetect(final Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }
            /*
            We obtain the specific button pressed in the gridview as well as the swiping direction.
            We also ensure the swipe was significant enough in a single direction.
             */
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
                if (GameActivity.solveMode) return false;
                int position = CustomGridView.this.pointToPosition(Math.round(motionEvent.getX()), Math.round(motionEvent.getY()));
                if (position == -1){ // Prevent swapping empty square
                    return false;
                }
                String direction = "";

                if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > 100) {
                    if (Math.abs(motionEvent.getX() - motionEvent1.getX()) > 100 || Math.abs(velocityY) < 50)
                        return false;

                    if (motionEvent.getY() - motionEvent1.getY() > 100)
                        direction = "UP";
                    else
                        direction = "DOWN";
                }
                else {
                    if (Math.abs(velocityX) < 50)
                        return false;

                    if (motionEvent.getX() - motionEvent1.getX() > 100)
                        direction = "LEFT";
                    else
                        direction = "RIGHT";
                }

                GameActivity.updateBoard(context, direction, position);
                return super.onFling(motionEvent, motionEvent1, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev);
    }
}
