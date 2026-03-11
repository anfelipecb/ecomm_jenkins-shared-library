# Jenkins Shared Library for E-Commerce Pipeline

Reusable pipeline steps for the e-commerce DevOps project.

## Structure

```
jenkins-shared-library/
├── vars/
│   ├── detectEnvironment.groovy      # Detects build/dev/staging/prod from branch
│   ├── buildAndPushDockerImage.groovy # Build and push to Docker Hub
│   └── approveProdDeploy.groovy      # Manual approval for production
├── .gitignore
└── README.md
```

## Configure Jenkins

1. **Manage Jenkins** → **System** (Configure System)
2. Scroll to **Global Trusted Pipeline Libraries**
3. Click **Add**
4. **Name:** `ecomm-shared-lib`
5. **Default version:** `main`
6. **Load implicitly:** unchecked (we use @Library in Jenkinsfiles)
7. **Retrieval method:** Modern SCM
8. **Source Code Management:** Git
9. **Project repository:** `https://github.com/anfelipecb/ecomm_jenkins-shared-library`
10. **Credentials:** Add your GitHub credentials if the repo is private
11. **Save**

## Use in Jenkinsfiles

Add at the top of your Jenkinsfile:

```groovy
@Library('ecomm-shared-lib') _

pipeline {
    ...
}
```

Then call the steps:

```groovy
detectEnvironment()
buildAndPushDockerImage('ecomm-product-service')
approveProdDeploy()  // when PIPELINE_ENV == 'prod'
```

## Image tagging strategy

- **Tag format:** `{BUILD_NUMBER}-git-{shortCommit}-v{version}` (e.g. `2-git-a8a21b0-v1.0.0`).
- **Version:** From `package.json` (set `env.APP_VERSION` before calling) or optional 3rd parameter; default `0.0.0`.
- **Full image reference:** After push, `env.FULL_IMAGE` is set (e.g. `afcamachob/ecomm-product-service:2-git-a8a21b0-v1.0.0`) for use in Deploy / Kubernetes.

## Available Steps

| Step | Description |
|------|-------------|
| `detectEnvironment()` | Sets env.PIPELINE_ENV from branch (build/dev/staging/prod) |
| `buildAndPushDockerImage(imageName, credentialsId?, version?)` | Builds and pushes Docker image; sets env.IMAGE_TAG and env.FULL_IMAGE |
| `approveProdDeploy()` | Pauses for manual approval (for prod deployments) |

## Pushing Updates

From the project root (final_project):

```bash
cd jenkins-shared-library
git add .
git commit -m "Update shared library"
git push origin main
```
