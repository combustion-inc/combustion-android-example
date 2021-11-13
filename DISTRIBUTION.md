# Build Checklist
- From `develop` branch.
- Run `ruby perform_release.rb <new-tag>` with the version string (e.g. 0.1.2).  This creates the [release on GitHub](https://github.com/combustion-inc/combustion-android/releases).
- Push the new tag `origin`.  This builds the release and pushes it to the internal group on [Firebase App Distribution](https://console.firebase.google.com/project/combustion-apps/appdistribution/app/android:inc.combustion.engineering/releases) 
- Smoke test the app from Firebase.  If broken, fix it and repeat.
- Merge `develop` and tag to `master`.
- In Firebase, update the release notes and add the other beta testing groups.

# App Deployment and Distribution

The following tools are used for the android release and deployment process:

- [Github and the Github CLI](https://cli.github.com/)
- [Github Actions](https://docs.github.com/en/actions)
- [Firebase and the Firebase CLI](https://firebase.google.com/docs/cli)

# Installation

## Github CLI
- [Github CLI Installation instructions](https://github.com/cli/cli#installation)

To setup and authorize the Github CLI, run the following command:

```bash
> gh auth login # Follow the prompts
```

Some useful commands:
```bash
> gh repo list      # List all repositories
> gh release list   # List all releases for the current repo
> gh run list       # List currently running actions in the repo
> gh pr list        # List all pull requests for the current repo
```

## Firebase and Firebase CLI

Firebase provides a framework to distribute builds of your apps to sets of testers. Since we already integrated Firebase into our android app it is easy to leverage the app distribution capability.

- [Firebase Installation instructions](https://firebase.google.com/docs/android/setup)
- [Firebase CLI Installation instructions](https://firebase.google.com/docs/cli#install-firebase-cli)

To setup and authorize the Firebase CLI, run the following command:
```bash
> firebase login # Follow the prompts
```

Some useful commands:
```bash
> firebase projects:list # List all firebase projects, provides the Project ID 
> firebase apps:list --project --project 746307581755 # List all apps in the firebase project, this is how you can get the App ID
> firebase appdistribution:distribute --app <app_id> --groups <string> --release-notes <string> <release-binary-file> # upload a release binary to firebase app distribution, real App ID is required and binary file is required
```

### Firebase Tester Setup

Firebase provides instructions on how to accept and use the applications you are invited to test.
Invitations to participate in the app testing are sent via email. 

https://firebase.google.com/docs/app-distribution/android/set-up-for-testing?authuser=3


## Github Actions

Since our project is hosted on Github we can use the a Github Actions workflow to distribute our app to Firebase App Distribution. We define the workflow in a yaml file called [android.yml](.github/workflows/android.yml)

We use the following actions:
- [actions/checkout](https://github.com/actions/checkout)
- [actions/setup-java](https://github.com/actions/setup-java)
- [r0adkll/sign-android-release](https://github.com/r0adkll/sign-android-release)
- [metcalfc/changelog-generator@v3.0.0](https://github.com/marketplace/actions/generate-changelog-action)
- [wzieba/Firebase-Dsitribution-Github-Action](https://github.com/wzieba/Firebase-Distribution-Github-Action)

### Secrets

Github provides a way to store secrets in the repo. This is useful for storing API keys and other sensitive information. The secrets can be accessible in the workflow by using the `secrets` variable.

The SIGNING_KEY secret that is used to sign the built apk was generated using the following commands:

Print keystore info
```bash
> keytool -list -rfc -keystore ~/.android/debug.keystore
```

Convert PKCS12 to JKS
```bash
> keytool -importkeystore -srckeystore somekeystore.pkcs12 -destkeystore somenewkeystore.jks -deststoretype jks
`````

Convert to base64 encoded string
```bash
> openssl base64 < martin-android-debug.jks | tr -d '\n' | tee martin-android-debug.jks.base64.txt
```

The ***martin-android-debug.jks.base64.txt*** file contains the base64 encoded string that can be used in the workflow.


> **_NOTE:_** ***You must have admin access to the repo to add secrets to the repo.***

## Release and Deployment

### Automatic Deployment
The [perform_release.rb](engineering/perform_release.rb) script is used to automatically deploy the latest release to Github and Firebase.
To generate a release and deploy run the following command:

```bash
> ruby perform_release.rb <new-tag> # where new-tag is the semver tag to be released i.e. 1.2.3
```

> **_NOTE:_** You are required to provide a tag to the perform_release.rb script. Do ***NOT*** include 'v' in the version tag.


### Manual Deployment
Assuming you have the gh CLI setup and firebase CLI you can manually You can generate a release to Github and Firebase by running the following commands:
```bash
> gh release create <new-tag> --generate-notes --title "Combustion Android Engineering Diagnostics App" <path-to-apk> # where new-tag is the semver tag to be released i.e. 1.2.3 and path-to-apk is the path to the apk file
> firebase appdistribution:distribute --app <app_id> --groups <string> --release-notes <string> <release-binary-file> # upload a release binary to firebase app distribution, real App ID is required and a path to a binary file is required, groups can be a string of groups names "group1, group2, group3"
```

# Guides

| Reference | Description |
| -- | -- |
| [Firebase App Distribution](https://firebase.google.com/docs/app-distribution/android/set-up-for-testing?authuser=3) | Set up for being a tester! |
| [Generate Release/Debug Keystores](https://coderwall.com/p/r09hoq/android-generate-release-debug-keystores) | Generate keystores for signing your app |
