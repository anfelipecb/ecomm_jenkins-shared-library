/**
 * Pauses for manual approval before production deploy.
 * Only run when PIPELINE_ENV == 'prod'
 */
def call() {
    input message: 'Approve deployment to Production?', ok: 'Deploy to Prod'
}
