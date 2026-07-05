pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile Services') {
            steps {
                script {
                    def services = [
                        'account-service',
                        'agency-service',
                        'notification-service',
                        'transaction-simulator-service'
                    ]

                    for (service in services) {
                        dir(service) {
                            sh 'mvn clean compile'
                        }
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    def services = [
                        'account-service',
                        'agency-service',
                        'notification-service',
                        'transaction-simulator-service'
                    ]

                    for (service in services) {
                        dir(service) {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Compilation and tests succeeded!'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}