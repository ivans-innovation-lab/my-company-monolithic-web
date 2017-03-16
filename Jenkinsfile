pipeline {
    agent any
    tools { 
        maven 'maven-3' 
    }
    stages {
        stage ('Build') {
            when {
                branch 'feature/*'
            }
            steps {
                sh 'mvn clean install'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
        stage ('Build & Deploy artifact') {
            when {
                branch 'master'
            }
            steps {
                sh 'mvn clean deploy'
                sh 'mvn -B gitflow:release-start deploy gitflow:release-finish'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
    }
}
