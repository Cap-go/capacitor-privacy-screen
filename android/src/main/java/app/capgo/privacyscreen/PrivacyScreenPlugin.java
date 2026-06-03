package app.capgo.privacyscreen;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "PrivacyScreen")
public class PrivacyScreenPlugin extends Plugin {

    private PrivacyScreen implementation;

    @Override
    public void load() {
        implementation = new PrivacyScreen(getActivity());
        if (getConfig().getBoolean("enabled", false)) {
            implementation.enable();
        }
    }

    @PluginMethod
    public void enable(final PluginCall call) {
        implementation.enable();
        call.resolve(status());
    }

    @PluginMethod
    public void disable(final PluginCall call) {
        implementation.disable();
        call.resolve(status());
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

    private JSObject status() {
        final JSObject ret = new JSObject();
        ret.put("enabled", implementation != null && implementation.isEnabled());
        return ret;
    }
}
