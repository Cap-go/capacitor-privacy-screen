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
 * Result returned when privacy protection is toggled.
 */
export interface PrivacyScreenActionResult extends PrivacyScreenStatus {
  /**
   * Whether the native operation completed.
   */
  success: boolean;
}

/**
 * Native startup and runtime configuration for privacy protection.
 */
export interface PrivacyScreenConfig {
  /**
   * Enable privacy protection automatically when the native plugin loads.
   *
   * This option is read from Capacitor config only.
   *
   * @default false
   */
  enabled?: boolean;

  /**
   * Android-only behavior.
   */
  android?: {
    /**
     * Controls how the app appears when the app switcher privacy screen is displayed.
     *
     * When `true`, Android shows a dim overlay. When `false`, Android shows the
     * splash drawable when available and falls back to dimming.
     *
     * @default false
     */
    dimBackground?: boolean;

    /**
     * Deprecated upstream compatibility option.
     *
     * `FLAG_SECURE` is always applied while the privacy screen is enabled. To allow
     * screenshots for a screen or flow, call `disable()` before it and `enable()` after it.
     *
     * @deprecated This option is ignored and will be removed in a future major version.
     */
    preventScreenshots?: boolean;

    /**
     * Controls how Android appears when the activity is hidden, such as while a
     * system biometric prompt is displayed.
     *
     * - `none`: no temporary overlay
     * - `dim`: dim overlay
     * - `splash`: splash drawable when available, otherwise dim overlay
     *
     * @default 'none'
     */
    privacyModeOnActivityHidden?: 'none' | 'dim' | 'splash';
  };

  /**
   * iOS-only behavior.
   */
  ios?: {
    /**
     * Controls how iOS obscures the app switcher snapshot.
     *
     * `light` and `dark` use native blur effects. `none` uses the launch screen
     * when available and otherwise falls back to a system background.
     *
     * @default 'none'
     */
    blurEffect?: 'none' | 'light' | 'dark';
  };
}

/**
 * Capacitor API for protecting app content from screenshots and app-switcher previews.
 */
export interface PrivacyScreenPlugin {
  /**
   * Enables privacy screen protection.
   *
   * On Android this sets `FLAG_SECURE`, which also blocks screenshots and screen recording.
   * On iOS this adds an overlay while the app is backgrounded so app content
   * does not appear in the app-switcher snapshot. iOS cannot prevent
   * user-initiated screenshots or screen recording.
   *
   * @param config Optional platform-specific behavior.
   */
  enable(config?: PrivacyScreenConfig): Promise<PrivacyScreenActionResult>;

  /**
   * Disables privacy screen protection.
   *
   * Use this only when you explicitly want the current screen to remain visible in system previews.
   */
  disable(): Promise<PrivacyScreenActionResult>;

  /**
   * Returns the current enabled state.
   */
  isEnabled(): Promise<PrivacyScreenStatus>;

  /**
   * Returns the native implementation version marker.
   */
  getPluginVersion(): Promise<PluginVersionResult>;
}
