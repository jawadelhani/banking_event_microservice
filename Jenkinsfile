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

        stage('Build Agency Service') {
            steps {
                dir('agency-service') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Build Notification Service') {
            steps {
                dir('notification-service') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Build Transaction Simulator Service') {
            steps {
                dir('transaction-simulator-service') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
    }

    post {
        success {
            echo 'All microservices built successfully!'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}