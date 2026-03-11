/**
 * Builds and pushes Docker image to Docker Hub.
 * @param imageName - name of the image (e.g. ecomm-product-service)
 * @param credentialsId - Jenkins credential ID for Docker Hub (default: docker-hub-credentials)
 */
def call(String imageName, String credentialsId = 'docker-hub-credentials') {
    def gitCommit = env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'unknown'
    def imageTag = "${env.BUILD_NUMBER}-git-${gitCommit}"
    env.IMAGE_TAG = imageTag

    echo "Building Docker image ${imageName}:${imageTag}"
    sh "docker build -t ${imageName}:${imageTag} -t ${imageName}:latest ."

    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKERHUB_USER',
        passwordVariable: 'DOCKERHUB_PASS'
    )]) {
        sh """
            export DOCKER_CONFIG=\$(mktemp -d)
            echo \$DOCKERHUB_PASS | docker login -u \$DOCKERHUB_USER --password-stdin
            docker tag ${imageName}:${imageTag} \$DOCKERHUB_USER/${imageName}:${imageTag}
            docker tag ${imageName}:latest \$DOCKERHUB_USER/${imageName}:latest
            docker push \$DOCKERHUB_USER/${imageName}:${imageTag}
            docker push \$DOCKERHUB_USER/${imageName}:latest
        """
    }
}
