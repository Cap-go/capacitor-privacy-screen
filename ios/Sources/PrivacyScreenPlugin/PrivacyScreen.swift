import Foundation
import UIKit

@objc public class PrivacyScreen: NSObject {
    private var observers: [NSObjectProtocol] = []
    private var windowProvider: (() -> UIWindow?)?
    private weak var overlayView: UIView?
    private(set) var isEnabled = false
    private var blurEffectStyle: UIBlurEffect.Style?
    // Hidden secure text field used as a protected rendering host.
    // With `isSecureTextEntry = true`, iOS marks this layer subtree as capture-protected.
    // We temporarily re-parent the window layer under that subtree to blank screenshots.
    private var screenshotPreventionTextField: UITextField?
    private weak var screenshotProtectedWindow: UIWindow?

    deinit {
        stop()
    }

    @objc public func start(windowProvider: @escaping () -> UIWindow?) {
        self.windowProvider = windowProvider

        guard observers.isEmpty else {
            if isEnabled {
                enableScreenshotPrevention()
            }
            return
        }

        let center = NotificationCenter.default
        observers = [
            center.addObserver(
                forName: UIApplication.willResignActiveNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                self?.showOverlayIfNeeded()
            },
            center.addObserver(
                forName: UIApplication.didBecomeActiveNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                self?.hideOverlay()
            }
        ]

        if isEnabled {
            enableScreenshotPrevention()
        }
    }

    @objc public func setBlurEffect(_ blurEffect: String?) {
        switch blurEffect {
        case "light":
            blurEffectStyle = .light
        case "dark":
            blurEffectStyle = .dark
        default:
            blurEffectStyle = nil
        }

        if overlayView != nil {
            hideOverlay()
            showOverlayIfNeeded()
        }
    }

    @objc public func stop() {
        let center = NotificationCenter.default
        observers.forEach(center.removeObserver)
        observers.removeAll()
        windowProvider = nil
        hideOverlay()
        disableScreenshotPrevention()
    }

    @objc public func setEnabled(_ enabled: Bool) {
        isEnabled = enabled
        if enabled {
            enableScreenshotPrevention()
        } else {
            hideOverlay()
            disableScreenshotPrevention()
        }
    }

    private func enableScreenshotPrevention() {
        guard screenshotPreventionTextField == nil,
              let window = currentWindow(),
              let screenLayer = window.layer.superlayer else { return }

        let textField = UITextField()
        textField.isSecureTextEntry = true

        screenLayer.addSublayer(textField.layer)

        let secureSublayer: CALayer?
        if #available(iOS 17.0, *) {
            secureSublayer = textField.layer.sublayers?.last ?? textField.layer.sublayers?.first
        } else {
            secureSublayer = textField.layer.sublayers?.first ?? textField.layer.sublayers?.last
        }

        guard let secureSublayer else {
            textField.layer.removeFromSuperlayer()
            return
        }

        secureSublayer.addSublayer(window.layer)
        screenshotPreventionTextField = textField
        screenshotProtectedWindow = window
    }

    private func disableScreenshotPrevention() {
        guard let textField = screenshotPreventionTextField else { return }
        defer {
            screenshotPreventionTextField = nil
            screenshotProtectedWindow = nil
        }

        if let screenLayer = textField.layer.superlayer,
           let window = screenshotProtectedWindow {
            screenLayer.addSublayer(window.layer)
        }
        textField.layer.removeFromSuperlayer()
    }

    private func showOverlayIfNeeded() {
        guard isEnabled, overlayView == nil, let window = currentWindow() else {
            return
        }

        let overlay = makeOverlayView(frame: window.bounds)
        overlay.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        window.addSubview(overlay)
        overlayView = overlay
    }

    private func hideOverlay() {
        overlayView?.removeFromSuperview()
        overlayView = nil
    }

    private func currentWindow() -> UIWindow? {
        if let window = windowProvider?() {
            return window
        }

        return UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first(where: \.isKeyWindow)
    }

    private func makeOverlayView(frame: CGRect) -> UIView {
        if let blurEffectStyle = blurEffectStyle {
            let blurView = UIVisualEffectView(effect: UIBlurEffect(style: blurEffectStyle))
            blurView.frame = frame
            return blurView
        }

        if let storyboardView = UIStoryboard(name: "LaunchScreen", bundle: nil).instantiateInitialViewController()?.view {
            storyboardView.frame = frame
            storyboardView.backgroundColor = .systemBackground
            return storyboardView
        }

        let fallbackView = UIView(frame: frame)
        fallbackView.backgroundColor = .systemBackground
        return fallbackView
    }
}
