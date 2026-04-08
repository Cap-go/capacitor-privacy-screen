import Foundation
import UIKit

@objc public class PrivacyScreen: NSObject {
    private var observers: [NSObjectProtocol] = []
    private var windowProvider: (() -> UIWindow?)?
    private weak var overlayView: UIView?
    private(set) var isEnabled = false
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
            setEnabled(true)
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

        setEnabled(true)
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
        if let storyboardView = UIStoryboard(name: "LaunchScreen", bundle: nil).instantiateInitialViewController()?.view {
            storyboardView.frame = frame
            storyboardView.backgroundColor = .systemBackground
            return storyboardView
        }

        let blurView = UIVisualEffectView(effect: UIBlurEffect(style: .systemUltraThinMaterial))
        blurView.frame = frame

        let shield = UILabel(frame: .zero)
        shield.translatesAutoresizingMaskIntoConstraints = false
        shield.text = "Protected"
        shield.font = UIFont.systemFont(ofSize: 24, weight: .semibold)
        shield.textColor = .secondaryLabel

        blurView.contentView.addSubview(shield)
        NSLayoutConstraint.activate([
            shield.centerXAnchor.constraint(equalTo: blurView.contentView.centerXAnchor),
            shield.centerYAnchor.constraint(equalTo: blurView.contentView.centerYAnchor)
        ])

        return blurView
    }
}
