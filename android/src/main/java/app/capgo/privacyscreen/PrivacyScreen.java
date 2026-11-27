package app.capgo.privacyscreen;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.content.res.AppCompatResources;

public class PrivacyScreen {

    private static final long FOCUS_OVERLAY_HIDE_DELAY_MS = 50L;

    public enum PrivacyMode {
        NONE,
        DIM,
        SPLASH
    }

    private final Activity activity;
    private boolean enabled;
    private volatile boolean dimBackground;
    private volatile PrivacyMode privacyModeOnActivityHidden = PrivacyMode.NONE;
    private PrivacyScreenDialog dialog;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable hideFocusOverlayRunnable = this::hideFocusOverlay;
    private View focusOverlay;

    public PrivacyScreen(final Activity activity) {
        this.activity = activity;
    }

    public void enable() {
        enable(false, PrivacyMode.NONE);
    }

    public void enable(final boolean dimBackground, final PrivacyMode privacyModeOnActivityHidden) {
        enable(dimBackground, privacyModeOnActivityHidden, false);
    }

    public void enable(final boolean dimBackground, final PrivacyMode privacyModeOnActivityHidden, final boolean hideContentOnFocusLoss) {
        enabled = true;
        this.dimBackground = dimBackground;
        this.privacyModeOnActivityHidden = privacyModeOnActivityHidden;
        runOnMainThread(() -> {
            if (hideContentOnFocusLoss) {
                addFocusOverlay();
            } else {
                removeFocusOverlay();
            }

            final Window window = activity.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });
    }

    public void disable() {
        enabled = false;
        privacyModeOnActivityHidden = PrivacyMode.NONE;
        runOnMainThread(() -> {
            removeFocusOverlay();

            final Window window = activity.getWindow();
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
            dismissPrivacyDialog();
        });
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void handleOnPause() {
        if (!enabled) {
            return;
        }

        switch (privacyModeOnActivityHidden) {
            case DIM:
                showPrivacyDialog(true);
                break;
            case SPLASH:
                showPrivacyDialog(false);
                break;
            case NONE:
            default:
                break;
        }
    }

    public void handleOnResume() {
        onRecentAppsTriggered(false);
    }

    // Some launchers (e.g. Pixel) animate the live window surface during the Recents gesture,
    // which FLAG_SECURE does not hide. Focus loss is the earliest signal available, before onPause.
    public void handleWindowFocusChanged(final boolean hasFocus) {
        if (focusOverlay == null) {
            return;
        }

        mainHandler.removeCallbacks(hideFocusOverlayRunnable);
        if (hasFocus) {
            mainHandler.postDelayed(hideFocusOverlayRunnable, FOCUS_OVERLAY_HIDE_DELAY_MS);
        } else {
            focusOverlay.setAlpha(activity.isInMultiWindowMode() ? 0f : 1f);
        }
    }

    public void handleConfigurationChanged() {
        if (focusOverlay != null) {
            focusOverlay.setBackground(createFocusOverlayBackground());
            focusOverlay.setAlpha((activity.hasWindowFocus() || activity.isInMultiWindowMode()) ? 0f : 1f);
        }
    }

    public void onRecentAppsTriggered(final boolean isRecentAppsOpen) {
        if (enabled && isRecentAppsOpen) {
            showPrivacyDialog(dimBackground);
        } else {
            runOnMainThread(this::dismissPrivacyDialog);
        }
    }

    public void destroy() {
        runOnMainThread(() -> {
            removeFocusOverlay();
            dismissPrivacyDialog();
        });
    }

    private void showPrivacyDialog(final boolean dim) {
        runOnMainThread(() -> {
            if (!isActivityUsable()) {
                return;
            }

            // The focus overlay already hides the live window, so skip the DIM/SPLASH dialog
            // while it is visible. privacyModeOnActivityHidden stays in effect once the overlay is hidden.
            if (focusOverlay != null && focusOverlay.getAlpha() > 0f) {
                return;
            }

            if (dialog != null && (dialog.isShowing() || isDialogViewAttachedToWindowManager())) {
                return;
            }

            dismissPrivacyDialog();
            dialog = new PrivacyScreenDialog(activity, dim);

            try {
                dialog.show();
            } catch (final RuntimeException ignored) {
                dialog = null;
            }
        });
    }

    private void dismissPrivacyDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void addFocusOverlay() {
        if (focusOverlay != null) {
            return;
        }

        focusOverlay = new View(activity);
        focusOverlay.setBackground(createFocusOverlayBackground());
        // The overlay stays attached and only its alpha is toggled, so it is drawn in the very next frame on focus loss.
        focusOverlay.setAlpha((activity.hasWindowFocus() || activity.isInMultiWindowMode()) ? 0f : 1f);
        focusOverlay.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        activity.addContentView(
            focusOverlay,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

    private void hideFocusOverlay() {
        if (focusOverlay != null) {
            focusOverlay.setAlpha(0f);
        }
    }

    private void removeFocusOverlay() {
        mainHandler.removeCallbacks(hideFocusOverlayRunnable);
        if (focusOverlay == null) {
            return;
        }

        final ViewParent parent = focusOverlay.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(focusOverlay);
        }
        focusOverlay = null;
    }

    private Drawable createFocusOverlayBackground() {
        final Drawable opaqueBackground = new ColorDrawable(Color.BLACK);
        final TypedValue value = new TypedValue();
        if (!activity.getTheme().resolveAttribute(android.R.attr.windowBackground, value, true)) {
            return opaqueBackground;
        }

        Drawable themeBackground = null;
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            themeBackground = new ColorDrawable(value.data);
        } else if (value.resourceId != 0) {
            themeBackground = AppCompatResources.getDrawable(activity, value.resourceId);
        }

        return themeBackground != null ? new LayerDrawable(new Drawable[] { opaqueBackground, themeBackground }) : opaqueBackground;
    }

    private boolean isDialogViewAttachedToWindowManager() {
        return dialog != null && dialog.getWindow() != null && dialog.getWindow().getDecorView().getParent() != null;
    }

    private boolean isActivityUsable() {
        return !activity.isFinishing() && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed());
    }

    private void runOnMainThread(final Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            activity.runOnUiThread(action);
        }
    }
}
