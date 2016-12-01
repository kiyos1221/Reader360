package com.a360ground.epubreader360.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by mahavir on 3/31/16.
 */
public class ExtendedWebview extends WebView {


    public static interface ScrollListener {
        public void onScrollChange(int percent);
    }

    private ScrollListener mScrollListener;

    public ExtendedWebview(Context context) {
        super(context);
    }

    public ExtendedWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExtendedWebview(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return this.dummyActionMode();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final boolean[] mMoveOccured = new boolean[1];
        final float[] mDownPosX = new float[1];
        final float[] mDownPosY = new float[1];
        // Log.d("dispatchTouchEvent","dispatch touch event");
        final float MOVE_THRESHOLD_DP = 20 * getResources().getDisplayMetrics().density;
        final int action = event.getAction();
        int positionScroll = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMoveOccured[0] = false;
                mDownPosX[0] = event.getX();
                mDownPosY[0] = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (!mMoveOccured[0]) {
                }

               /* boolean isHorizontal = true;
                if (isHorizontal) {
                   *//* DisplayMetrics dm = new DisplayMetrics();
                    ((Activity)this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);*//*
                    ScreenUtils screen = new ScreenUtils(getContext());

                    int w = screen.getWidth();
                    int contentWidth = w - 15;
                    int deviceWidth = this.getWidth();
                    int totalPages = contentWidth / deviceWidth + 1;
                    int curentPage = (totalPages - (totalPages - positionScroll)) / this.getWidth();

                    int position = curentPage * deviceWidth;

                    int scrollX = this.getScrollX();

                    if (scrollX > position + deviceWidth / 4) { // right

                        position += deviceWidth;

                    } else if (scrollX < position - deviceWidth / 4) { // left

                        position -= deviceWidth;
                    }

                    positionScroll = position;

                   *//* ObjectAnimator anim = ObjectAnimator.ofInt(this,
                            "scrollX", getScrollX(), position);
                    anim.setDuration(600);
                    anim.start();

                    ObjectAnimator animY = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), 0);
                    animY.setDuration(300);
                    animY.start();

                }*/

                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mDownPosX[0]) > MOVE_THRESHOLD_DP
                        || Math.abs(event.getY() - mDownPosY[0]) > MOVE_THRESHOLD_DP) {
                    mMoveOccured[0] = true;
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.dummyActionMode();
    }

    public ActionMode dummyActionMode() {
        return new ActionMode() {
            @Override
            public void setTitle(CharSequence title) {
            }

            @Override
            public void setTitle(int resId) {
            }

            @Override
            public void setSubtitle(CharSequence subtitle) {
            }

            @Override
            public void setSubtitle(int resId) {
            }

            @Override
            public void setCustomView(View view) {
            }

            @Override
            public void invalidate() {
            }

            @Override
            public void finish() {
            }

            @Override
            public Menu getMenu() {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public CharSequence getSubtitle() {
                return null;
            }

            @Override
            public View getCustomView() {
                return null;
            }

            @Override
            public MenuInflater getMenuInflater() {
                return null;
            }
        };
    }
}