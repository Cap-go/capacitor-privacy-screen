import Foundation
import Capacitor

@objc(PrivacyScreenPlugin)
public class PrivacyScreenPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "PrivacyScreenPlugin"
    public let jsName = "PrivacyScreen"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "enable", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disable", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isEnabled", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getPluginVersion", returnType: CAPPluginReturnPromise)
    ]

    private let implementation = PrivacyScreen()

    override public func load() {
        DispatchQueue.main.async {
            self.implementation.start { [weak self] in
                self?.bridge?.viewController?.view.window
            }
            if let iosConfig = self.getConfig().getObject("ios") {
                self.implementation.setBlurEffect(iosConfig["blurEffect"] as? String)
            }
            self.implementation.setEnabled(self.getConfig().getBoolean("enabled", false))
        }
    }

    @objc func enable(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            if let iosConfig = call.getObject("ios") {
                self.implementation.setBlurEffect(iosConfig["blurEffect"] as? String)
            }
            self.implementation.setEnabled(true)
            call.resolve([
                "enabled": self.implementation.isEnabled,
                "success": true
            ])
        }
    }

    @objc func disable(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.setEnabled(false)
            call.resolve([
                "enabled": self.implementation.isEnabled,
                "success": true
            ])
        }
    }

    @objc func isEnabled(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            call.resolve([
                "enabled": self.implementation.isEnabled
            ])
        }
    }

    @objc func getPluginVersion(_ call: CAPPluginCall) {
        call.resolve([
            "version": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "ios"
        ])
    }
}
