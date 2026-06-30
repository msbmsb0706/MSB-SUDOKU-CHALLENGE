<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# MSB Sudoku Challenge — Run & Deploy

This repository contains the automated build system to compile and deploy your Google AI Studio app without needing a heavy local computer setup.

View this app layout in AI Studio: https://ai.studio/apps/326dfd68-d547-4db5-bad1-8bdd7301b174

---

## 🚀 Cloud Build & Deployment Workflow (Mobile-Friendly)

You do not need to install Android Studio or manage SDK tools locally. The entire compilation, code optimization, and app signing pipeline run entirely in the cloud via GitHub Actions.

### 📋 Prerequisites
Before triggering a build, ensure your secret API credentials are set up securely inside GitHub:
1. Go to your repository settings: **Settings ➔ Secrets and variables ➔ Actions**.
2. Click **New repository secret**.
3. Name the secret exactly: `GEMINI_API_KEY`
4. Paste your secret alphanumeric API token string from your Google AI Studio dashboard into the value box and save.

---

## 🛠️ How to Generate Your App Packages

1. Navigate to the **Actions** tab at the top of this GitHub repository.
2. Select **Build MSB Sudoku Deliverables** from the left sidebar.
3. Click the **Run workflow** dropdown on the right side and click the green confirmation button.
4. Once the build run finishes successfully (indicated by a green checkmark), scroll down to the **Artifacts** section.
5. Download the single **`MSB-SUDOKU-COMPLETE-PACKAGE`** zip folder.

---

## 📦 What's Inside the Downloaded Package?

When you extract the downloaded `.zip` archive on your device, you will find three essential standalone files side-by-side:

* **`app-release.apk`**: Your standalone executable app. Uninstall any old copies from your device first, then click this file to install and test the game directly on your phone.
* **`app-release.aab`**: The master Android App Bundle. This is the official file you upload straight to the **Google Play Console** to publish your game on the store.
* **`my-upload-key.jks`**: Your permanent digital signature keystore backup (secured with password: `android123`). **Download this file and save it securely on your Google Drive.** You will need this exact file to sign future updates for the Play Store.
