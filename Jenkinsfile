pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub') // Jenkins Credentials ID
        DOCKERHUB_USERNAME = 'ngocle101'
    }

    stages {
        stage('Detect Changes') {
            steps {
                script {
                    // Get current commit SHA
                    env.COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    echo "Commit ID: ${env.COMMIT_ID}"

                    // Compare current commit with previous successful commit
                    def diffTarget = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: "HEAD~1"
                    def changes = sh(
                        script: "git diff --name-only ${diffTarget} HEAD | cut -d/ -f1 | grep -E '^(spring-petclinic-).*' | sort -u",
                        returnStdout: true
                                    ).trim().split("\n")

                    echo "Changed services: ${changes}"

                    env.CHANGED_SERVICES = changes.join(',')
                }
            }
        }

        stage("Build app") {
            when {
                expression { env.CHANGED_SERVICES }
            }
            steps {
                script {
                    env.CHANGED_SERVICES.split(',').each { service ->
                        echo "Building service ${service}"
                        dir(service) {
                            sh '../mvnw clean package -DskipTests'
                            sh 'cp target/*.jar .'
                            sh 'rm -rf target/'
                        }
                    }
                }
            }
        }

        stage('Build Images') {
            when {
                expression { env.CHANGED_SERVICES }
            }
            steps {
                script {
                    env.CHANGED_SERVICES.split(',').each { service ->
                        def prefix = "${env.DOCKERHUB_USERNAME}/${service}"
                        def tag = "${env.COMMIT_ID}"
                        echo "Building image ${prefix}:${tag} for service ${service}"
                        
                        dir(service) {
                            def jarName = sh(script: "ls *.jar", returnStdout: true).trim()
                            def artifactName = jarName.replace('.jar', '')
                            sh "docker build -f ../docker/Dockerfile --build-arg ARTIFACT_NAME=${artifactName} -t ${prefix}:${tag} ."
                        }
                    }
                }
            }
        }

        stage('Push Images') {
            when {
                expression { env.CHANGED_SERVICES }
            }
            steps {
                script {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_USERNAME --password-stdin'
                    env.CHANGED_SERVICES.split(',').each { service ->
                        def imageName = "${env.DOCKERHUB_USERNAME}/${service}:${env.COMMIT_ID}"
                        echo "Pushing image ${imageName}"
                        sh "docker push ${imageName}"
                    }
                }
            }
        }

        stage('Clean Images') {
            when {
                expression { env.CHANGED_SERVICES }
            }
            steps {
                script {
                    env.CHANGED_SERVICES.split(',').each { service ->
                        def imageName = "${env.DOCKERHUB_USERNAME}/${service}:${env.COMMIT_ID}"
                        sh "docker rmi ${imageName} || true"
                    }
                }
            }
        }
    }
}


