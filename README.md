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

## Available Steps

| Step | Description |
|------|-------------|
| `detectEnvironment()` | Sets env.PIPELINE_ENV from branch (build/dev/staging/prod) |
| `buildAndPushDockerImage(imageName)` | Builds and pushes Docker image to Docker Hub |
| `approveProdDeploy()` | Pauses for manual approval (for prod deployments) |

## Pushing Updates

From the project root (final_project):

```bash
cd jenkins-shared-library
git add .
git commit -m "Update shared library"
git push origin main
```
