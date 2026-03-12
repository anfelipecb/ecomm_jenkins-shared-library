/**
 * Validate Kubernetes manifests with kubectl apply --dry-run=client.
 * Uses the same profile/namespace as deployToK8s. Run before Deploy to catch schema errors early.
 *
 * @param manifestFiles   list of filenames under kubernetes/${PIPELINE_ENV}/, in apply order
 * @param placeholderImage  image string to substitute for PLACEHOLDER_IMAGE in deployment manifests (default: busybox:latest)
 */
def call(List<String> manifestFiles, String placeholderImage = 'busybox:latest') {
    def envName = env.PIPELINE_ENV ?: 'dev'
    def profile = "ecomm-${envName}"
    def k = "minikube kubectl --profile=${profile} --"
    def baseDir = "kubernetes/${envName}"

    echo "Validating manifests in ${baseDir} (profile ${profile})"

    for (String f : manifestFiles) {
        def path = "${baseDir}/${f}"
        if (f.contains('deployment')) {
            sh "sed 's#PLACEHOLDER_IMAGE#${placeholderImage}#g' ${path} | ${k} apply --dry-run=client -f -"
        } else {
            sh "${k} apply --dry-run=client -f ${path}"
        }
    }

    echo "Manifest validation passed for all ${manifestFiles.size()} files"
}
