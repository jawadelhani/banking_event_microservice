pipeline {
    agent any

    environment {
        SERVICES = "account-service agency-service notification-service transaction-simulator-service"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                script {
                    for (service in env.SERVICES.split()) {
                        echo "Compiling ${service}"
                        dir(service) {
                            sh 'mvn clean compile'
                        }
                    }
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    for (service in env.SERVICES.split()) {
                        echo "Testing ${service}"
                        dir(service) {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    for (service in env.SERVICES.split()) {
                        echo "Packaging ${service}"
                        dir(service) {
                            sh 'mvn package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    for (service in env.SERVICES.split()) {
                        echo "Building Docker image for ${service}"

                        dir(service) {
                            sh "docker build -t ${service}:latest ."
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline completed successfully!'
        }

        failure {
            echo 'CI Pipeline failed!'
        }
    }
}