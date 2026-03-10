/**
 * Detects pipeline environment from branch/PR.
 * Sets env.PIPELINE_ENV to: build, dev, staging, or prod
 * @return the environment string
 */
def call() {
    def pipelineEnv = 'build'
    if (env.CHANGE_ID) {
        pipelineEnv = 'build'
    } else if (env.BRANCH_NAME == 'main') {
        pipelineEnv = 'prod'
    } else if (env.BRANCH_NAME == 'develop') {
        pipelineEnv = 'dev'
    } else if (env.BRANCH_NAME?.startsWith('release/')) {
        pipelineEnv = 'staging'
    } else {
        pipelineEnv = 'build'
    }
    env.PIPELINE_ENV = pipelineEnv
    echo "Environment: ${pipelineEnv} (branch: ${env.BRANCH_NAME})"
    return pipelineEnv
}
