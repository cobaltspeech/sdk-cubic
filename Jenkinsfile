#!groovy
// Copyright (2020) Cobalt Speech and Language Inc.

// Keep only 10 builds on Jenkins
properties([
    buildDiscarder(logRotator(
        artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10'))
])

// setBuildStatus tells github the status of our build for a given "context"
def setBuildStatus(String context, String state, String message) {
    step([$class: "GitHubCommitStatusSetter",
          reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/cobaltspeech/sdk-cubic"],
          contextSource: [$class: "ManuallyEnteredCommitContextSource", context: context],
          errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
          statusResultSource: [$class: "ConditionalStatusResultSource", results: [[$class: 'AnyBuildResult', message: message, state: state]]]])
}

if (env.CHANGE_ID) {
    // Building a pull request
    node {
	try {
	    timeout(time: 30, unit: 'MINUTES'){
		sh '$(aws ecr get-login --region us-east-1 --no-include-email)'
		withCredentials([string(credentialsId: 'cobalt-ecr-registry-url', variable: 'ecrRegistry')]) {
		    docker.withRegistry("${ecrRegistry}") {
			docker.image("private/sdk-generator").inside('-u root') {
			    try {
				stage("validate") {
				    checkout scm
				    setBuildStatus("validate-built-artifacts", "PENDING", "Validating generated artifacts.")
				    sh './ci/validate_generated_artifacts.sh'
				}
			    } finally {
				// Allow Jenkins to cleanup workspace
				sh "chown -R 1000:1000 ."
			    }
			}
		    }
		}
		setBuildStatus("validate-built-artifacts", "SUCCESS", "Successfully validated.")
	    }
	} catch(err) {
	    setBuildStatus("validate-built-artifacts", "ERROR", "Validation failed.")
	    throw err
	} finally {
	    deleteDir()
	}
    }
}
