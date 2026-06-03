# Example App for `@capgo/capacitor-privacy-screen`

This Vite project links directly to the local plugin source so you can validate privacy-screen state changes on web, iOS, and Android while developing.

## Getting started

```bash
npm install
npm run start
```

To test on native shells:

```bash
npx cap add ios
npx cap add android
npx cap sync
```

Native shells leave the privacy screen disabled unless you enable it in Capacitor config or with JavaScript. Use the example buttons to enable it, then confirm screenshots and app switcher previews are hidden.
