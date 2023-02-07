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
    timeout(time: 30, unit: 'MINUTES') {
      checkout scm
      sh "git config --global --add safe.directory '*'"

      stage("validate-built-artifacts") {
        commit.setBuildStatus("validate-built-artifacts", "PENDING", "Validating generated artifacts.")

        def sdkGenImage = docker.build("sdk-generator", "./ci")
          sdkGenImage.inside('-u root') {
            try {
              sh "git config --global --add safe.directory '*'"
              sh "make"
              sh "git diff --exit-code"
            } finally {
              // Allow Jenkins to cleanup workspace
              sh "chown -R 1000:1000 ."
            }
          }
        }
        commit.setBuildStatus("validate-built-artifacts", "SUCCESS", "Successfully validated.")
      }
    } catch (err) {
      commit.setBuildStatus("validate-built-artifacts", "ERROR", "Validation failed.")
      throw err
    } finally {
      deleteDir()
    }
  }
}