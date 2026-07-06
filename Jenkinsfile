pipeline {
    agent any

     options {
            timestamps()
     }

    tools {
        maven 'Maven'
    }

    environment {
        SERVICES = "account-service agency-service notification-service transaction-simulator-service"
        DOCKERHUB_USERNAME = "jawad1010"
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
                        echo "Running tests for ${service}"

                        dir(service) {
                            sh 'mvn test'
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {

                    withSonarQubeEnv('SonarQube') {

                        for (service in env.SERVICES.split()) {

                            echo "Analyzing ${service}"

                            dir(service) {
                                sh """
                                    mvn sonar:sonar \
                                      -Dsonar.projectKey=${service} \
                                      -Dsonar.projectName=${service}
                                """
                            }

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

                        echo "Building Docker image: ${service}"

                        dir(service) {
                            sh """
                                docker build \
                                -t ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER} \
                                -t ${DOCKERHUB_USERNAME}/${service}:latest .
                            """
                        }
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USER" --password-stdin
                    '''

                    script {
                        for (service in env.SERVICES.split()) {

                            sh """
                                docker push ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER}
                                docker push ${DOCKERHUB_USERNAME}/${service}:latest
                            """
                        }
                    }

                    sh 'docker logout'
                }
            }
        }

    }

    post {

        success {
            echo "======================================"
            echo "CI Pipeline completed successfully!"
            echo "Build Number: ${BUILD_NUMBER}"
            echo "Docker images pushed to Docker Hub."
            echo "======================================"
        }

        failure {
            echo "======================================"
            echo "CI Pipeline failed!"
            echo "======================================"
        }

        always {
            cleanWs()
        }
    }
}