import { CapacitorUpdater } from '@capgo/capacitor-updater';
import { Capacitor } from '@capacitor/core';
import './style.css';
import { PrivacyScreen } from '@capgo/capacitor-privacy-screen';

const output = document.getElementById('plugin-output');
const statusBadge = document.getElementById('status-badge');
const enableButton = document.getElementById('enable-protection');
const disableButton = document.getElementById('disable-protection');
const refreshButton = document.getElementById('refresh-status');
const versionButton = document.getElementById('get-version');

const setOutput = (value) => {
  output.textContent = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
};

const setStatus = (enabled) => {
  statusBadge.textContent = enabled ? 'Enabled' : 'Disabled';
  statusBadge.dataset.enabled = String(enabled);
};

const refreshStatus = async () => {
  try {
    const result = await PrivacyScreen.isEnabled();
    setStatus(result.enabled);
    setOutput(result);
  } catch (error) {
    setOutput(`Error: ${error?.message ?? error}`);
  }
};

enableButton.addEventListener('click', async () => {
  try {
    const result = await PrivacyScreen.enable();
    setStatus(result.enabled);
    setOutput(result);
  } catch (error) {
    setOutput(`Error: ${error?.message ?? error}`);
  }
});

disableButton.addEventListener('click', async () => {
  try {
    const result = await PrivacyScreen.disable();
    setStatus(result.enabled);
    setOutput(result);
  } catch (error) {
    setOutput(`Error: ${error?.message ?? error}`);
  }
});

refreshButton.addEventListener('click', refreshStatus);

versionButton.addEventListener('click', async () => {
  try {
    const result = await PrivacyScreen.getPluginVersion();
    setOutput(result);
  } catch (error) {
    setOutput(`Error: ${error?.message ?? error}`);
  }
});

refreshStatus();

if (Capacitor.isNativePlatform()) {
  CapacitorUpdater.notifyAppReady().catch((error) => {
    console.error('Capgo notifyAppReady failed', error);
  });
}
