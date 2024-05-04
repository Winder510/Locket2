package com.example.myapplication.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.viewpager2.widget.ViewPager2;

public class NestedScrollableHost extends FrameLayout {
    private ViewPager2 viewPager2;
    private float initialX;
    private float initialY;
    private boolean isScrollable = true;

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    public NestedScrollableHost(Context context) {
        super(context);
    }

    public NestedScrollableHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedScrollableHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setViewPager2(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int currentItem = viewPager2.getCurrentItem();
        if ((currentItem == 0 && isScrollable) || (currentItem == 0 && event.getAction() == MotionEvent.ACTION_MOVE && event.getY() - initialY < 0)) {
            // Cho phép sự kiện lướt lên để quay lại camera fragment
            if (event.getAction() == MotionEvent.ACTION_MOVE && event.getY() - initialY > 0) {
                viewPager2.requestDisallowInterceptTouchEvent(true);
                return false;
            }
            return super.onInterceptTouchEvent(event);
        }
        handleInterceptTouchEvent(event);

        return super.onInterceptTouchEvent(event);
    }


    private void handleInterceptTouchEvent(MotionEvent event) {
        if (viewPager2 == null) return;

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = x;
                initialY = y;
                if(viewPager2.getCurrentItem()!=0 ){
                    viewPager2.requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - initialX;
                float dy = y - initialY;

                if (viewPager2.getCurrentItem()!=0 &&Math.abs(dx) > Math.abs(dy)) {
                    viewPager2.requestDisallowInterceptTouchEvent(false);
                } else {
                    viewPager2.requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                break;
        }
    }
}
