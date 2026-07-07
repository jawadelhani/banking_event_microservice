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

       stage('OWASP Dependency Check') {
           steps {
               script {

                   withCredentials([
                       string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')
                   ]) {

                       for (service in env.SERVICES.split()) {

                           echo "Scanning dependencies for ${service}"

                           dir(service) {

                               withEnv(["NVD_API_KEY=${NVD_API_KEY}"]) {
                                   sh '''
                                       mvn dependency-check:check \
                                         -DnvdApiKey=$NVD_API_KEY
                                   '''
                               }

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

        stage('Trivy Image Scan') {
            steps {
                script {

                    for (service in env.SERVICES.split()) {

                        echo "Scanning ${service}"

                        sh """
                            mkdir -p trivy-reports

                            trivy image \
                                --severity HIGH,CRITICAL \
                                --exit-code 0 \
                                --format table \
                                ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER} \
                                > trivy-reports/${service}.txt
                        """
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

       always {

           publishHTML([
               allowMissing: true,
               alwaysLinkToLastBuild: true,
               keepAll: true,
               reportDir: 'account-service/target',
               reportFiles: 'dependency-check-report.html',
               reportName: 'Account Service Dependency Check'
           ])

           publishHTML([
               allowMissing: true,
               alwaysLinkToLastBuild: true,
               keepAll: true,
               reportDir: 'agency-service/target',
               reportFiles: 'dependency-check-report.html',
               reportName: 'Agency Service Dependency Check'
           ])

           publishHTML([
               allowMissing: true,
               alwaysLinkToLastBuild: true,
               keepAll: true,
               reportDir: 'notification-service/target',
               reportFiles: 'dependency-check-report.html',
               reportName: 'Notification Service Dependency Check'
           ])

           publishHTML([
               allowMissing: true,
               alwaysLinkToLastBuild: true,
               keepAll: true,
               reportDir: 'transaction-simulator-service/target',
               reportFiles: 'dependency-check-report.html',
               reportName: 'Transaction Simulator Dependency Check'
           ])

           cleanWs()
       }

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
   }
}