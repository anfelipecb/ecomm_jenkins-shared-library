# Jenkins Shared Library for E-Commerce Pipeline

Reusable pipeline steps for the e-commerce DevOps project.

## Structure

```
jenkins-shared-library/
├── vars/
│   ├── detectEnvironment.groovy   # Detects build/dev/staging/prod from branch
│   ├── buildAndPushDockerImage.groovy  # Build and push to Docker Hub
│   └── approveProdDeploy.groovy   # Manual approval for production
└── README.md
```

## Setup

### 1. Create the repo on GitHub

1. Go to https://github.com/new
2. Repository name: `jenkins-shared-library`
3. Add a description (optional)
4. Create repository (don't initialize with README - we already have one)

### 2. Push this folder to GitHub

From the project root (final_project):

```bash
cd jenkins-shared-library
git init
git add .
git commit -m "Initial shared library"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/ecomm_jenkins-shared-library.git
git push -u origin main
```

Replace `YOUR_USERNAME` with your GitHub username (e.g. `anfelipecb`).

**Yes, you need to create the repo on GitHub first** – Jenkins fetches the library from the Git URL.

### 3. Configure Jenkins

1. **Manage Jenkins** → **Configure System**
2. Scroll to **Global Pipeline Libraries**
3. Click **Add**
4. **Name:** `ecomm-shared-lib`
5. **Default version:** `main`
6. **Load implicitly:** unchecked (we use @Library in Jenkinsfiles)
7. **Retrieval method:** Modern SCM
8. **Source Code Management:** Git
9. **Project repository:** `https://github.com/YOUR_USERNAME/jenkins-shared-library`
10. **Credentials:** Add your GitHub credentials if the repo is private
11. **Save**

### 4. Use in Jenkinsfiles

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
