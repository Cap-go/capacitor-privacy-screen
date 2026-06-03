import { WebPlugin } from '@capacitor/core';

import type {
  PluginVersionResult,
  PrivacyScreenActionResult,
  PrivacyScreenPlugin,
  PrivacyScreenStatus,
} from './definitions';

export class PrivacyScreenWeb extends WebPlugin implements PrivacyScreenPlugin {
  private enabled = false;

  async enable(): Promise<PrivacyScreenActionResult> {
    this.enabled = true;
    return { enabled: this.enabled, success: true };
  }

  async disable(): Promise<PrivacyScreenActionResult> {
    this.enabled = false;
    return { enabled: this.enabled, success: true };
  }

  async isEnabled(): Promise<PrivacyScreenStatus> {
    return { enabled: this.enabled };
  }

  async getPluginVersion(): Promise<PluginVersionResult> {
    return {
      version: 'web',
    };
  }
}
