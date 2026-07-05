pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Account Service') {
            steps {
                dir('account-service') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'Account Service built successfully!'
        }

        failure {
            echo 'Build failed!'
        }
    }
}