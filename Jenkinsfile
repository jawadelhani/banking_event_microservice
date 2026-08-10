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

        // ============================================================
        // CI - APPLICATION REPOSITORY
        // ============================================================

        stage('Checkout Application') {
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
                        string(
                            credentialsId: 'nvd-api-key',
                            variable: 'NVD_API_KEY'
                        )
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

                    // Backend services
                    for (service in env.SERVICES.split()) {

                        dir(service) {

                            sh """
                                docker build --platform linux/amd64 \
                                    -t ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER} \
                                    -t ${DOCKERHUB_USERNAME}/${service}:latest .
                            """
                        }
                    }

                    // Frontend
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

                    // Scan frontend too
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
                            -o trivy-reports/banking-frontend.html \
                            ${DOCKERHUB_USERNAME}/banking-frontend:${BUILD_NUMBER}
                    """
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


        // ============================================================
        // CD - KUBERNETES MANIFEST REPOSITORY
        // ============================================================

        stage('Checkout Kubernetes Manifests') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASSWORD'
                    )
                ]) {

                    sh '''
                        rm -rf banking-k8s-manifests

                        git clone \
                            --branch main \
                            https://${GIT_USER}:${GIT_PASSWORD}@github.com/jawadelhani/banking-k8s-manifests.git \
                            banking-k8s-manifests
                    '''
                }
            }
        }


        stage('Update Kubernetes Manifests (CD)') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASSWORD'
                    )
                ]) {

                    sh '''
                        set -e

                        echo "======================================"
                        echo "Updating Kubernetes Image Tags"
                        echo "Build: $BUILD_NUMBER"
                        echo "======================================"

                        cd banking-k8s-manifests

                        # ==========================================
                        # Backend services
                        # ==========================================

                        for service in $SERVICES; do

                            echo "Updating $service..."

                            sed -i \
                                "s|image: [^/]*/${service}:.*|image: ${DOCKERHUB_USERNAME}/${service}:${BUILD_NUMBER}|g" \
                                backend/${service}/deployment.yaml

                        done


                        # ==========================================
                        # Frontend
                        # ==========================================

                        echo "Updating banking-frontend..."

                        sed -i \
                            "s|image: [^/]*/banking-frontend:.*|image: ${DOCKERHUB_USERNAME}/banking-frontend:${BUILD_NUMBER}|g" \
                            frontend/angular-app/deployment.yaml


                        # ==========================================
                        # Show changes
                        # ==========================================

                        echo ""
                        echo "Changes:"
                        git diff


                        # ==========================================
                        # Commit
                        # ==========================================

                        git config user.email "jenkins@banking.local"
                        git config user.name "Jenkins CI"

                        git add .

                        if ! git diff --cached --quiet; then

                            git commit \
                                -m "chore: update image tags to build ${BUILD_NUMBER} [skip ci]"

                            echo "Pushing changes to GitHub..."

                            git push origin HEAD:main

                        else

                            echo "No changes to commit."

                        fi

                        echo "Kubernetes manifests updated successfully."
                    '''
                }
            }
        }
    }


    // ================================================================
    // POST ACTIONS
    // ================================================================

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

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'trivy-reports',
                reportFiles: 'banking-frontend.html',
                reportName: 'Trivy - Frontend'
            ])

            archiveArtifacts(
                artifacts: '**/*.html',
                allowEmptyArchive: true
            )

            cleanWs()
        }

        success {

            echo "======================================"
            echo "CI/CD Pipeline completed successfully!"
            echo "Build Number: ${BUILD_NUMBER}"
            echo "======================================"
        }

        failure {

            echo "======================================"
            echo "CI/CD Pipeline failed!"
            echo "======================================"
        }
    }
}