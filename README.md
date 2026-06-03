# @capgo/capacitor-privacy-screen
<a href="https://capgo.app/"><img src="https://capgo.app/readme-banner.svg?repo=Cap-go/capacitor-privacy-screen" alt="Capgo - Instant updates for Capacitor" /></a>

<div align="center">
  <h2><a href="https://capgo.app/?ref=plugin_privacy_screen"> ➡️ Get Instant updates for your App with Capgo</a></h2>
  <h2><a href="https://capgo.app/consulting/?ref=plugin_privacy_screen"> Missing a feature? We’ll build the plugin for you 💪</a></h2>
</div>

Protect sensitive app content from appearing in Android screenshots and iOS app-switcher previews.

Capgo's Privacy Screen plugin is a Capacitor port of [PrivacyScreenPlugin](https://github.com/martinkasa/PrivacyScreenPlugin) with a modern native implementation for Capacitor 8.

## Documentation

The most complete doc is available here: https://capgo.app/docs/plugins/privacy-screen/

## Compatibility

| Plugin version | Capacitor compatibility | Maintained |
| -------------- | ----------------------- | ---------- |
| v8.\*.\*       | v8.\*.\*                | ✅          |
| v7.\*.\*       | v7.\*.\*                | On demand   |
| v6.\*.\*       | v6.\*.\*                | ❌          |

> **Note:** The major version of this plugin follows the major version of Capacitor. Use the version that matches your Capacitor installation. Only the latest major version is actively maintained.

## Install

```bash
npm install @capgo/capacitor-privacy-screen
npx cap sync
```

## Configuration

Protection is disabled by default. Enable it on native startup with Capacitor config:

```ts
import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.example.app',
  appName: 'Example',
  webDir: 'dist',
  plugins: {
    PrivacyScreen: {
      enabled: true,
      android: {
        dimBackground: true,
        privacyModeOnActivityHidden: 'splash',
      },
      ios: {
        blurEffect: 'dark',
      },
    },
  },
};

export default config;
```

## Usage

```ts
import { PrivacyScreen } from '@capgo/capacitor-privacy-screen';

await PrivacyScreen.enable({
  android: {
    dimBackground: true,
    privacyModeOnActivityHidden: 'splash',
  },
  ios: {
    blurEffect: 'dark',
  },
});

const { enabled } = await PrivacyScreen.isEnabled();

await PrivacyScreen.disable();

await PrivacyScreen.enable();

// Perform a flow where screenshots or previews should be protected.

await PrivacyScreen.disable();
```

Use JavaScript calls when protection should only apply to specific screens or flows.

## Behavior

- Android uses `WindowManager.LayoutParams.FLAG_SECURE`, which hides app content from screenshots, screen recording, and the recent apps preview.
- Android can show a temporary dim or splash overlay for recent-apps and activity-hidden states.
- iOS adds a temporary overlay while the app resigns active so the app-switcher snapshot does not expose your content, with optional light or dark blur. iOS cannot prevent user-initiated screenshots or screen recording.
- Web keeps an in-memory enabled flag for API parity, but browsers cannot enforce native privacy-screen behavior.

## API

<docgen-index>

* [`enable(...)`](#enable)
* [`disable()`](#disable)
* [`isEnabled()`](#isenabled)
* [`getPluginVersion()`](#getpluginversion)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

Capacitor API for protecting app content from screenshots and app-switcher previews.

### enable(...)

```typescript
enable(config?: PrivacyScreenConfig | undefined) => Promise<PrivacyScreenActionResult>
```

Enables privacy screen protection.

On Android this sets `FLAG_SECURE`, which also blocks screenshots and screen recording.
On iOS this adds an overlay while the app is backgrounded so app content
does not appear in the app-switcher snapshot. iOS cannot prevent
user-initiated screenshots or screen recording.

| Param        | Type                                                                | Description                          |
| ------------ | ------------------------------------------------------------------- | ------------------------------------ |
| **`config`** | <code><a href="#privacyscreenconfig">PrivacyScreenConfig</a></code> | Optional platform-specific behavior. |

**Returns:** <code>Promise&lt;<a href="#privacyscreenactionresult">PrivacyScreenActionResult</a>&gt;</code>

--------------------


### disable()

```typescript
disable() => Promise<PrivacyScreenActionResult>
```

Disables privacy screen protection.

Use this only when you explicitly want the current screen to remain visible in system previews.

**Returns:** <code>Promise&lt;<a href="#privacyscreenactionresult">PrivacyScreenActionResult</a>&gt;</code>

--------------------


### isEnabled()

```typescript
isEnabled() => Promise<PrivacyScreenStatus>
```

Returns the current enabled state.

**Returns:** <code>Promise&lt;<a href="#privacyscreenstatus">PrivacyScreenStatus</a>&gt;</code>

--------------------


### getPluginVersion()

```typescript
getPluginVersion() => Promise<PluginVersionResult>
```

Returns the native implementation version marker.

**Returns:** <code>Promise&lt;<a href="#pluginversionresult">PluginVersionResult</a>&gt;</code>

--------------------


### Interfaces


#### PrivacyScreenActionResult

Result returned when privacy protection is toggled.

| Prop          | Type                 | Description                             |
| ------------- | -------------------- | --------------------------------------- |
| **`success`** | <code>boolean</code> | Whether the native operation completed. |


#### PrivacyScreenConfig

Native startup and runtime configuration for privacy protection.

| Prop          | Type                                                                                                                               | Description                                                                                                           | Default            |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **`enabled`** | <code>boolean</code>                                                                                                               | Enable privacy protection automatically when the native plugin loads. This option is read from Capacitor config only. | <code>false</code> |
| **`android`** | <code>{ dimBackground?: boolean; preventScreenshots?: boolean; privacyModeOnActivityHidden?: 'none' \| 'dim' \| 'splash'; }</code> | Android-only behavior.                                                                                                |                    |
| **`ios`**     | <code>{ blurEffect?: 'none' \| 'light' \| 'dark'; }</code>                                                                         | iOS-only behavior.                                                                                                    |                    |


#### PrivacyScreenStatus

Current privacy screen state.

| Prop          | Type                 | Description                                      |
| ------------- | -------------------- | ------------------------------------------------ |
| **`enabled`** | <code>boolean</code> | Whether privacy protection is currently enabled. |


#### PluginVersionResult

Plugin version payload.

| Prop          | Type                | Description                                                 |
| ------------- | ------------------- | ----------------------------------------------------------- |
| **`version`** | <code>string</code> | Version identifier returned by the platform implementation. |

</docgen-api>
