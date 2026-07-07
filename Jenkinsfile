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

                               sh '''
                                   mvn dependency-check:check \
                                       -DnvdApiKey=$NVD_API_KEY \
                                       -DdataDirectory=/var/jenkins_home/owasp-dc-data \
                                       -Danalyzer.assembly.enabled=false
                               '''

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

                    sh 'mkdir -p trivy-reports'

                    for (service in env.SERVICES.split()) {

                        sh """
                            trivy image \
                              --severity HIGH,CRITICAL \
                              --exit-code 0 \
                              --format table \
                              -o trivy-reports/${service}.txt \
                              ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER}
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

            archiveArtifacts(
                artifacts: 'trivy-reports/*.txt',
                fingerprint: true
            )

            cleanWs()
        }

        success {
            echo "======================================"
            echo "CI Pipeline completed successfully!"
            echo "Build Number: ${BUILD_NUMBER}"
            echo "======================================"
        }

        failure {
            echo "======================================"
            echo "CI Pipeline failed!"
            echo "======================================"
        }
    }
}