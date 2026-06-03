/**
 * Plugin version payload.
 */
export interface PluginVersionResult {
  /**
   * Version identifier returned by the platform implementation.
   */
  version: string;
}

/**
 * Current privacy screen state.
 */
export interface PrivacyScreenStatus {
  /**
   * Whether privacy protection is currently enabled.
   */
  enabled: boolean;
}

/**
 * Native startup configuration for privacy protection.
 */
export interface PrivacyScreenConfig {
  /**
   * Enable privacy protection automatically when the native plugin loads.
   *
   * @default false
   */
  enabled?: boolean;
}

/**
 * Capacitor API for protecting app content from screenshots and app-switcher previews.
 */
export interface PrivacyScreenPlugin {
  /**
   * Enables the privacy screen.
   *
   * On Android this sets `FLAG_SECURE`, which also blocks screenshots and screen recording.
   * On iOS this hides app content from screenshots and restores the app-switcher overlay while backgrounded.
   */
  enable(): Promise<PrivacyScreenStatus>;

  /**
   * Disables the privacy screen.
   *
   * Use this only when you explicitly want the current screen to remain visible in system previews.
   */
  disable(): Promise<PrivacyScreenStatus>;

  /**
   * Returns the current enabled state.
   */
  isEnabled(): Promise<PrivacyScreenStatus>;

  /**
   * Returns the native implementation version marker.
   */
  getPluginVersion(): Promise<PluginVersionResult>;
}
