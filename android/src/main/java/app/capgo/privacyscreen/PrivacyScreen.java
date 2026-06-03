package app.capgo.privacyscreen;

import android.app.Activity;
import android.os.Build;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

public class PrivacyScreen {

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

    public PrivacyScreen(final Activity activity) {
        this.activity = activity;
    }

    public void enable() {
        enable(false, PrivacyMode.NONE);
    }

    public void enable(final boolean dimBackground, final PrivacyMode privacyModeOnActivityHidden) {
        enabled = true;
        this.dimBackground = dimBackground;
        this.privacyModeOnActivityHidden = privacyModeOnActivityHidden;
        runOnMainThread(() -> {
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

    public void onRecentAppsTriggered(final boolean isRecentAppsOpen) {
        if (enabled && isRecentAppsOpen) {
            showPrivacyDialog(dimBackground);
        } else {
            runOnMainThread(this::dismissPrivacyDialog);
        }
    }

    public void destroy() {
        runOnMainThread(this::dismissPrivacyDialog);
    }

    private void showPrivacyDialog(final boolean dim) {
        runOnMainThread(() -> {
            if (!isActivityUsable()) {
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
