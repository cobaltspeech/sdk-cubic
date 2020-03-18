#!groovy
// Copyright (2020) Cobalt Speech and Language Inc.

// Keep only 10 builds on Jenkins
properties([
    buildDiscarder(logRotator(
        artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10'))
])

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
				    commit.setBuildStatus("validate-built-artifacts", "PENDING", "Validating generated artifacts.")
				    sh './ci/validate_generated_artifacts.sh'
				}
			    } finally {
				// Allow Jenkins to cleanup workspace
				sh "chown -R 1000:1000 ."
			    }
			}
		    }
		}
		commit.setBuildStatus("validate-built-artifacts", "SUCCESS", "Successfully validated.")
	    }
	} catch(err) {
	    commit.setBuildStatus("validate-built-artifacts", "ERROR", "Validation failed.")
	    throw err
	} finally {
	    deleteDir()
	}
    }
}
