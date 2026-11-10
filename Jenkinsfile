
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
                            $env:DOCKER_CONFIG = $configDir

                            Write-Output "Authenticating with Docker Hub API..."

                            $body = @{
                                username = $env:DOCKERHUB_USER
                                password = $env:DOCKERHUB_TOKEN
                            } | ConvertTo-Json -Compress

                            $response = Invoke-RestMethod `
                                -Uri "https://hub.docker.com/v2/users/login/" `
                                -Method POST `
                                -ContentType "application/json" `
                                -Body $body

                            $authToken = $response.token

                            if (-not $authToken) {
                                throw "Docker Hub did not return an authentication token."
                            }

                            Write-Output "Docker Hub authentication successful."

                            $env:DOCKERHUB_TOKEN = $null

                            $authBytes = [System.Text.Encoding]::UTF8.GetBytes(
                                "$($env:DOCKERHUB_USER):$authToken"
                            )
                            $authBase64 = [Convert]::ToBase64String($authBytes)

                            $config = @{
                                auths = @{
                                    "https://index.docker.io/v1/" = @{
                                        auth = $authBase64
                                    }
                                }
                            } | ConvertTo-Json -Depth 5

                            Set-Content -Path (Join-Path $configDir "config.json") `
                                -Value $config -Encoding ASCII

                            $authToken = $null
                            $authBase64 = $null
                            $authBytes = $null
                            $config = $null
                            $body = $null
                            $response = $null

                            Write-Output "Tagging image..."

                            & $docker tag football-players:1.0 darshan1103/football-players:1.0
                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker image tagging failed."
                            }

                            Write-Output "Pushing image to Docker Hub..."

                            & $docker push darshan1103/football-players:1.0
                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker image push failed."
                            }

                            Write-Output "Docker image pushed successfully."
                        }
                        finally {
                            Remove-Item -Path $configDir -Recurse -Force `
                                -ErrorAction SilentlyContinue
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
