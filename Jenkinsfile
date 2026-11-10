
pipeline {
    agent any

    environment {
        DOCKER = 'C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        IMAGE_NAME = 'darshan1103/football-players'
        IMAGE_TAG = '1.0'
    }

    stages {

        stage('Build and Test') {
            steps {
                echo 'Building and testing Football Players application...'
                bat 'mvnw.cmd clean package'
            }
        }

        stage('Docker Check') {
            steps {
                echo 'Checking Docker connectivity...'
                bat '"%DOCKER%" version'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Football Players Docker image...'
                bat '"%DOCKER%" build -t football-players:1.0 .'
            }
        }

        stage('Docker Hub Login and Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"

                        $docker = $env:DOCKER
                        $configDir = Join-Path $env:TEMP "football-players-docker-$env:BUILD_NUMBER"

                        try {
                            New-Item -ItemType Directory -Path $configDir -Force | Out-Null

                            # Use an isolated Docker configuration.
                            $env:DOCKER_CONFIG = $configDir

                            Write-Output "Logging in to Docker Hub..."

                            $env:DOCKERHUB_TOKEN |
                                & $docker login --username $env:DOCKERHUB_USER --password-stdin

                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker Hub login failed."
                            }

                            Write-Output "Docker Hub login successful."

                            Write-Output "Tagging Docker image..."

                            & $docker tag football-players:1.0 "$env:IMAGE_NAME`:$env:IMAGE_TAG"

                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker image tagging failed."
                            }

                            Write-Output "Pushing image to Docker Hub..."

                            & $docker push "$env:IMAGE_NAME`:$env:IMAGE_TAG"

                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker image push failed."
                            }

                            Write-Output "Docker image pushed successfully."
                        }
                        finally {
                            & $docker logout 2>$null | Out-Null

                            Remove-Item -Path $configDir -Recurse -Force -ErrorAction SilentlyContinue

                            Remove-Item Env:DOCKER_CONFIG -ErrorAction SilentlyContinue
                        }
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build, tests, Docker build and Docker Hub push completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Please check the Jenkins console output.'
        }
    }
}
