import Foundation
import UIKit

@objc public class PrivacyScreen: NSObject {
    private var observers: [NSObjectProtocol] = []
    private var windowProvider: (() -> UIWindow?)?
    private var applicationStateProvider: () -> UIApplication.State = { UIApplication.shared.applicationState }
    private weak var overlayView: UIView?
    private(set) var isEnabled = false
    private var blurEffectStyle: UIBlurEffect.Style?

    deinit {
        stop()
    }

    @objc public func start(windowProvider: @escaping () -> UIWindow?) {
        self.windowProvider = windowProvider

        guard observers.isEmpty else {
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
    }

    @objc public func setBlurEffect(_ blurEffect: String?) {
        switch blurEffect {
        case "light":
            blurEffectStyle = .light
        case "dark":
            blurEffectStyle = .dark
        case "none":
            blurEffectStyle = nil
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
    }

    @objc public func setEnabled(_ enabled: Bool) {
        isEnabled = enabled
        if enabled {
            showOverlayIfAppIsInactive()
        } else {
            hideOverlay()
        }
    }

    func setApplicationStateProvider(_ provider: @escaping () -> UIApplication.State) {
        applicationStateProvider = provider
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

    private func showOverlayIfAppIsInactive() {
        guard applicationStateProvider() != .active else {
            return
        }

        showOverlayIfNeeded()
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
