import XCTest
import UIKit
@testable import PrivacyScreenPlugin

class PrivacyScreenPluginTests: XCTestCase {
    func testStartDoesNotEnableByDefault() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.start {
            requestedWindow = true
            return nil
        }

        XCTAssertFalse(implementation.isEnabled)
        XCTAssertFalse(implementation.isScreenshotPreventionActive)
        XCTAssertFalse(requestedWindow)
    }

    func testEnableDisableState() {
        let implementation = PrivacyScreen()
        implementation.setEnabled(true)
        XCTAssertTrue(implementation.isEnabled)

        implementation.setEnabled(false)
        XCTAssertFalse(implementation.isEnabled)
    }

    func testEnableRequestsWindowForCaptureProtection() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.setApplicationStateProvider {
            .active
        }
        implementation.start {
            requestedWindow = true
            return nil
        }

        implementation.setEnabled(true)

        XCTAssertTrue(requestedWindow)
    }

    func testDisableDoesNotRequestWindowWhenNotEnabled() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.start {
            requestedWindow = true
            return nil
        }

        implementation.setEnabled(false)

        XCTAssertFalse(implementation.isScreenshotPreventionActive)
        XCTAssertFalse(requestedWindow)
    }

    func testEnableWhileInactiveRequestsWindowForOverlay() {
        let implementation = PrivacyScreen()
        var requestedWindow = false
        implementation.setApplicationStateProvider {
            .inactive
        }
        implementation.start {
            requestedWindow = true
            return nil
        }

        implementation.setEnabled(true)

        XCTAssertTrue(requestedWindow)
    }
}
