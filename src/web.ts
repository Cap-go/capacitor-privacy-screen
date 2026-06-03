import { WebPlugin } from '@capacitor/core';

import type { PluginVersionResult, PrivacyScreenPlugin, PrivacyScreenStatus } from './definitions';

export class PrivacyScreenWeb extends WebPlugin implements PrivacyScreenPlugin {
  private enabled = false;

  async enable(): Promise<PrivacyScreenStatus> {
    this.enabled = true;
    return { enabled: this.enabled };
  }

  async disable(): Promise<PrivacyScreenStatus> {
    this.enabled = false;
    return { enabled: this.enabled };
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
