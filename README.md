# Android Onfleet SDK example project

A minimal example app showing how to integrate the Onfleet Driver SDK.

## Setup

1. Copy the config template and fill in the values Onfleet gave you:
   ```bash
   cp app/src/main/assets/onfleet.properties.example app/src/main/assets/onfleet.properties
   ```
   - `applicationName` — your application name
   - `sdkApplicationId` — your Onfleet SDK key

2. (Push notifications) Replace the placeholder `app/google-services.json` with the
   `google-services.json` for the Firebase project you registered with Onfleet. The
   committed file is a non-functional placeholder so the project builds out of the box;
   push delivery requires your own Firebase config.

## Build

```bash
./gradlew assembleDebug
```

CI builds every pull request and every push to `master`.
