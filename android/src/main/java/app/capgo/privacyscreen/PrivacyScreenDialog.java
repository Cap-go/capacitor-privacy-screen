package app.capgo.privacyscreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

@SuppressLint("DiscouragedApi")
public class PrivacyScreenDialog extends Dialog {

    private static final int DIM_OVERLAY_COLOR = Color.argb(230, 0, 0, 0);
    private boolean lostFocusOnce;

    public PrivacyScreenDialog(final Activity activity, final boolean dimBackground) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);

        final Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (dimBackground || !setSplashContent(activity)) {
            setDimContent();
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        dismiss();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            lostFocusOnce = true;
            return;
        }

        if (lostFocusOnce) {
            lostFocusOnce = false;
            hide();
            new Handler(Looper.getMainLooper()).post(this::dismiss);
        }
    }

    private boolean setSplashContent(final Activity activity) {
        final int resourceId = activity.getResources().getIdentifier("splash", "drawable", activity.getPackageName());
        if (resourceId == 0) {
            return false;
        }

        final FrameLayout contentView = fullScreenContentView();
        contentView.setBackgroundResource(resourceId);
        setContentView(contentView);
        return true;
    }

    private void setDimContent() {
        final View contentView = fullScreenContentView();
        contentView.setBackgroundColor(DIM_OVERLAY_COLOR);
        setContentView(contentView);
    }

    private FrameLayout fullScreenContentView() {
        final FrameLayout contentView = new FrameLayout(getContext());
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return contentView;
    }
}
