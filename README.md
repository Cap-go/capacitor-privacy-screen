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
bun add @capgo/capacitor-privacy-screen
bunx cap sync
```

## Usage

```ts
import { PrivacyScreen } from '@capgo/capacitor-privacy-screen';

await PrivacyScreen.disable();

// Perform a flow where screenshots or previews are acceptable.

await PrivacyScreen.enable();
```

The plugin enables protection automatically when the native plugin loads, so most apps do not need to call anything on startup.

## Behavior

- Android uses `WindowManager.LayoutParams.FLAG_SECURE`, which hides app content from screenshots, screen recording, and the recent apps preview.
- iOS adds a temporary overlay while the app resigns active so the app switcher snapshot does not expose your content.
- Web keeps an in-memory enabled flag for API parity, but browsers cannot enforce native privacy-screen behavior.

## API

<docgen-index>

* [`enable()`](#enable)
* [`disable()`](#disable)
* [`isEnabled()`](#isenabled)
* [`getPluginVersion()`](#getpluginversion)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

Capacitor API for protecting app content from the app switcher preview.

### enable()

```typescript
enable() => Promise<PrivacyScreenStatus>
```

Enables the privacy screen.

On Android this sets `FLAG_SECURE`, which also blocks screenshots and screen recording.
On iOS this restores the app-switcher overlay that hides your app while it is backgrounded.

**Returns:** <code>Promise&lt;<a href="#privacyscreenstatus">PrivacyScreenStatus</a>&gt;</code>

--------------------


### disable()

```typescript
disable() => Promise<PrivacyScreenStatus>
```

Disables the privacy screen.

Use this only when you explicitly want the current screen to remain visible in system previews.

**Returns:** <code>Promise&lt;<a href="#privacyscreenstatus">PrivacyScreenStatus</a>&gt;</code>

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
