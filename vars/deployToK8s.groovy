/**
 * Deploy to Kubernetes (Minikube) using the image just built (env.FULL_IMAGE).
 * Uses "minikube kubectl --profile=ecomm-{dev|staging|prod} --" so the right cluster is targeted.
 * Applies manifests in order (with PLACEHOLDER_IMAGE replaced), then sets the deployment image.
 *
 * @param deploymentName  e.g. 'ecomm-database', 'ecomm-product-service'
 * @param containerName   container name in the Deployment spec, e.g. 'postgres', 'product-service'
 * @param manifestFiles   list of filenames under kubernetes/${PIPELINE_ENV}/, in apply order
 */
def call(String deploymentName, String containerName, List<String> manifestFiles) {
    def envName = env.PIPELINE_ENV ?: 'dev'
    def profile = "ecomm-${envName}"
    def namespace = "ecomm-${envName}"
    def k = "minikube kubectl --profile=${profile} --"
    def fullImage = env.FULL_IMAGE

    if (!fullImage?.trim()) {
        error "deployToK8s: FULL_IMAGE is not set. Run buildAndPushDockerImage first."
    }

    echo "Deploying to namespace ${namespace} (profile ${profile}), image ${fullImage}"

    def baseDir = "kubernetes/${envName}"
    for (String f : manifestFiles) {
        def path = "${baseDir}/${f}"
        if (f.contains('deployment')) {
            sh "sed 's#PLACEHOLDER_IMAGE#${fullImage}#g' ${path} | ${k} apply -f -"
        } else {
            sh "${k} apply -f ${path}"
        }
    }

    sh "${k} set image deployment/${deploymentName} ${containerName}=${fullImage} -n ${namespace}"
    echo "Deploy done: deployment/${deploymentName} image set to ${fullImage}"
}
