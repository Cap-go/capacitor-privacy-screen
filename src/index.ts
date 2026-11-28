import { registerPlugin } from '@capacitor/core';

import type { PrivacyScreenPlugin } from './definitions.js';

const PrivacyScreen = registerPlugin<PrivacyScreenPlugin>('PrivacyScreen', {
  web: () => import('./web.js').then((m) => new m.PrivacyScreenWeb()),
});

export * from './definitions.js';
export { PrivacyScreen };
