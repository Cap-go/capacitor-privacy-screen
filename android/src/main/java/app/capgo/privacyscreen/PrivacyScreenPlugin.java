package app.capgo.privacyscreen;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import org.json.JSONObject;

@CapacitorPlugin(name = "PrivacyScreen")
public class PrivacyScreenPlugin extends Plugin {

    private static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS_XIAOMI = "fs_gesture";

    private PrivacyScreen implementation;
    private boolean receiverRegistered;

    private final BroadcastReceiver recentAppsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (!ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                return;
            }

            final String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (
                implementation != null &&
                (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason) || SYSTEM_DIALOG_REASON_RECENT_APPS_XIAOMI.equals(reason))
            ) {
                implementation.onRecentAppsTriggered(true);
            }
        }
    };

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void load() {
        implementation = new PrivacyScreen(getActivity());

        // ACTION_CLOSE_SYSTEM_DIALOGS is restricted to system apps on Android 12+.
        // FLAG_SECURE remains active; this receiver only improves legacy recent-apps overlay timing.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            getContext().registerReceiver(recentAppsReceiver, new IntentFilter(ACTION_CLOSE_SYSTEM_DIALOGS));
            receiverRegistered = true;
        }

        if (getConfig().getBoolean("enabled", false)) {
            final JSONObject androidConfig = getConfig().getObject("android");
            implementation.enable(getDimBackground(androidConfig), getPrivacyModeOnActivityHidden(androidConfig));
        }
    }

    @PluginMethod
    public void enable(final PluginCall call) {
        final JSONObject androidConfig = call.getObject("android");
        implementation.enable(getDimBackground(androidConfig), getPrivacyModeOnActivityHidden(androidConfig));
        call.resolve(actionResult(true));
    }

    @PluginMethod
    public void disable(final PluginCall call) {
        implementation.disable();
        call.resolve(actionResult(true));
    }

    @PluginMethod
    public void isEnabled(final PluginCall call) {
        call.resolve(status());
    }

    @PluginMethod
    public void getPluginVersion(final PluginCall call) {
        final JSObject ret = new JSObject();
        ret.put("version", "android");
        call.resolve(ret);
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        if (implementation != null) {
            implementation.handleOnPause();
        }
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        if (implementation != null) {
            implementation.handleOnResume();
        }
    }

    @Override
    protected void handleOnDestroy() {
        if (implementation != null) {
            implementation.destroy();
        }

        if (receiverRegistered) {
            try {
                getContext().unregisterReceiver(recentAppsReceiver);
            } catch (final IllegalArgumentException ignored) {
                // Receiver was already unregistered by Android during teardown.
            }
            receiverRegistered = false;
        }

        super.handleOnDestroy();
    }

    private JSObject actionResult(final boolean success) {
        final JSObject ret = status();
        ret.put("success", success);
        return ret;
    }

    private JSObject status() {
        final JSObject ret = new JSObject();
        ret.put("enabled", implementation != null && implementation.isEnabled());
        return ret;
    }

    private PrivacyScreen.PrivacyMode parsePrivacyMode(final String value) {
        if ("dim".equals(value)) {
            return PrivacyScreen.PrivacyMode.DIM;
        }
        if ("splash".equals(value)) {
            return PrivacyScreen.PrivacyMode.SPLASH;
        }
        return PrivacyScreen.PrivacyMode.NONE;
    }

    private boolean getDimBackground(final JSONObject androidConfig) {
        return androidConfig != null && androidConfig.optBoolean("dimBackground", false);
    }

    private PrivacyScreen.PrivacyMode getPrivacyModeOnActivityHidden(final JSONObject androidConfig) {
        return parsePrivacyMode(androidConfig != null ? androidConfig.optString("privacyModeOnActivityHidden", "none") : "none");
    }
}
