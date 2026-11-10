
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

        
        stage('Docker Hub API Authentication Test') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_TOKEN'
                    )
                ]) {
                    powershell '''
                        $body = @{
                            username = $env:DOCKERHUB_USER
                            password = $env:DOCKERHUB_TOKEN
                        } | ConvertTo-Json -Compress

                        try {
                            $response = Invoke-RestMethod `
                                -Uri "https://hub.docker.com/v2/users/login/" `
                                -Method POST `
                                -ContentType "application/json" `
                                -Body $body

                            if ($response.token) {
                                Write-Output "Docker Hub API authentication successful."
                            } else {
                                throw "No authentication token returned."
                            }
                        }
                        catch {
                            Write-Output "Docker Hub API authentication failed."
                            Write-Output "HTTP status: $([int]$_.Exception.Response.StatusCode)"
                            throw "Credential validation failed."
                        }
                        finally {
                            $body = $null
                            $response = $null
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
