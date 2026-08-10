pipeline {
    agent any

    options {
        timestamps()
    }

    tools {
        maven 'Maven'
    }

    environment {
        SERVICES = "account-service agency-service notification-service transaction-simulator-service ai-service"
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
                                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar \
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
                                   mvn org.owasp:dependency-check-maven:12.2.2:check \
                                       -DnvdApiKey=$NVD_API_KEY \
                                       -DdataDirectory=/var/jenkins_home/owasp-dc-data \
                                       -Danalyzer.assembly.enabled=false \
                                       -DnvdApiDelay=6000
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
                                docker build --platform linux/amd64 \
                                    -t ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER} \
                                    -t ${DOCKERHUB_USERNAME}/${service}:latest .
                            """
                        }
                    }

                    dir('frontend') {
                        sh """
                            docker build --platform linux/amd64 \
                                -t ${DOCKERHUB_USERNAME}/banking-frontend:${BUILD_NUMBER} \
                                -t ${DOCKERHUB_USERNAME}/banking-frontend:latest .
                        """
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
                              --cache-dir /var/jenkins_home/trivy-cache \
                              --timeout 20m \
                              --skip-version-check \
                              --scanners vuln \
                              --severity HIGH,CRITICAL \
                              --exit-code 0 \
                              --format template \
                              --template "@/var/jenkins_home/trivy-template/html.tpl" \
                              -o trivy-reports/${service}.html \
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
                        echo "$DOCKER_PASSWORD" | docker login \
                            -u "$DOCKER_USER" \
                            --password-stdin
                    '''

                    script {
                        for (service in env.SERVICES.split()) {
                            sh """
                                docker push ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER}
                            """
                        }

                        sh """
                            docker push ${DOCKERHUB_USERNAME}/banking-frontend:${BUILD_NUMBER}
                        """
                    }

                    sh 'docker logout'
                }
            }
        }


        stage('Update Kubernetes Manifests (CD)') {
            steps {
                script {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'github-token',
                            usernameVariable: 'GIT_USER',
                            passwordVariable: 'GIT_PASSWORD'
                        )
                    ]) {
                        sh """
                            echo 'Updating Image Tags...'

                            # Update Backend Services
                            for service in ${SERVICES}; do
                                sed -i "s|image: jawadelhani/\\\${service}:.*|image: ${DOCKERHUB_USERNAME}/\\\${service}:${BUILD_NUMBER}|g" \
                                banking-k8s-manifests/backend/\\\${service}/deployment.yaml
                            done

                            # Update Frontend
                            sed -i "s|image: jawadelhani/banking-frontend:.*|image: ${DOCKERHUB_USERNAME}/banking-frontend:${BUILD_NUMBER}|g" \
                            banking-k8s-manifests/frontend/angular-app/deployment.yaml

                            echo 'Committing Changes...'

                            cd banking-k8s-manifests

                            git config user.email 'jenkins@banking.local'
                            git config user.name 'Jenkins CI'

                            git add .

                            if ! git diff-index --quiet HEAD; then
                                git commit -m "chore: Update image tags to build ${BUILD_NUMBER} [skip ci]"

                                git push https://${GIT_USER}:${GIT_PASSWORD}@github.com/jawadelhani/banking-k8s-manifests.git HEAD:main
                            else
                                echo 'No changes to commit.'
                            fi
                        """
                    }
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
                reportName: 'OWASP - Account Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'agency-service/target',
                reportFiles: 'dependency-check-report.html',
                reportName: 'OWASP - Agency Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'notification-service/target',
                reportFiles: 'dependency-check-report.html',
                reportName: 'OWASP - Notification Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'transaction-simulator-service/target',
                reportFiles: 'dependency-check-report.html',
                reportName: 'OWASP - Transaction Simulator'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'trivy-reports',
                reportFiles: 'account-service.html',
                reportName: 'Trivy - Account Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'trivy-reports',
                reportFiles: 'agency-service.html',
                reportName: 'Trivy - Agency Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'trivy-reports',
                reportFiles: 'notification-service.html',
                reportName: 'Trivy - Notification Service'
            ])

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'trivy-reports',
                reportFiles: 'transaction-simulator-service.html',
                reportName: 'Trivy - Transaction Simulator'
            ])

            archiveArtifacts(
                artifacts: '**/*.html',
                allowEmptyArchive: true
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