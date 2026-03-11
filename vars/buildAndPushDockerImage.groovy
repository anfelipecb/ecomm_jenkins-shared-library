/**
 * Builds and pushes Docker image to Docker Hub.
 * Tag format: ${BUILD_NUMBER}-git-${shortCommit}-v${version} (e.g. 2-git-a8a21b0-v1.0.0)
 * Sets env.IMAGE_TAG and env.FULL_IMAGE for use in Deploy / Kubernetes.
 *
 * @param imageName - name of the image (e.g. ecomm-product-service)
 * @param credentialsId - Jenkins credential ID for Docker Hub (default: docker-hub-credentials)
 * @param version - optional app version (default: env.APP_VERSION or '0.0.0')
 */
def call(String imageName, String credentialsId = 'docker-hub-credentials', String version = null) {
    def ver = version?.trim() ?: env.APP_VERSION?.trim() ?: '0.0.0'
    def gitCommit = env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'unknown'
    def imageTag = "${env.BUILD_NUMBER}-git-${gitCommit}-v${ver}"
    env.IMAGE_TAG = imageTag

    echo "Building Docker image ${imageName}:${imageTag}"
    sh "docker build -t ${imageName}:${imageTag} ."

    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKERHUB_USER',
        passwordVariable: 'DOCKERHUB_PASS'
    )]) {
        env.FULL_IMAGE = "${env.DOCKERHUB_USER}/${imageName}:${imageTag}"
        echo "Full image reference (for deployments): ${env.FULL_IMAGE}"
        sh """
            export DOCKER_CONFIG=\$(mktemp -d)
            echo \$DOCKERHUB_PASS | docker login -u \$DOCKERHUB_USER --password-stdin
            docker tag ${imageName}:${imageTag} \$DOCKERHUB_USER/${imageName}:${imageTag}
            docker push \$DOCKERHUB_USER/${imageName}:${imageTag}
        """
    }
}
