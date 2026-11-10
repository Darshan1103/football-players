
pipeline {
    agent any

    stages {
        stage('Build and Test') {
            steps {
                echo 'Building Football Players application...'
                bat 'mvnw.cmd clean package'
            }
        }

        stage('Docker Check') {
            steps {
                echo 'Checking Docker access from Jenkins...'
                bat '"C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" version'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Football Players Docker image...'
                bat '"C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t football-players:1.0 .'
            }
        }


        // stage('Docker Push') {
        //     steps {
        //         withCredentials([usernamePassword(
        //             credentialsId: 'dockerhub-credentials',
        //             usernameVariable: 'DOCKERHUB_USER',
        //             passwordVariable: 'DOCKERHUB_TOKEN'
        //         )]) {
        //             bat '''
        //                 @echo off
        //                 echo %DOCKERHUB_TOKEN% | "C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" login -u %DOCKERHUB_USER% --password-stdin

        //                 "C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" tag football-players:1.0 darshan1103/football-players:1.0

        //                 "C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" push darshan1103/football-players:1.0
        //             '''
        //         }
        //     }
        // }


        stage('Docker Hub Token Fingerprint') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    powershell '''
                        $sha = [System.Security.Cryptography.SHA256]::Create()
                        $bytes = [System.Text.Encoding]::UTF8.GetBytes($env:DOCKERHUB_TOKEN.Trim())
                        $hash = [BitConverter]::ToString($sha.ComputeHash($bytes)).Replace("-", "")

                        Write-Output "Jenkins fingerprint: $hash"
                    '''
                }
            }
        }
        stage('Docker Environment Check') {
            steps {
                powershell '''
                    $docker = "C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"

                    Write-Output "Docker context:"
                    & $docker context show

                    Write-Output "Docker root directory:"
                    & $docker info --format '{{.DockerRootDir}}'

                    Write-Output "Docker configuration location:"
                    Write-Output $env:DOCKER_CONFIG
                '''
            }
        }

        stage('Docker Hub Login Test') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    powershell '''
                        $docker = "C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"

                        $env:DOCKERHUB_TOKEN |
                            & $docker login -u $env:DOCKERHUB_USER --password-stdin

                        if ($LASTEXITCODE -ne 0) {
                            throw "Docker Hub login failed."
                        }

                        Write-Output "Docker Hub authentication successful."
                    '''
                }
            }
        }

        stage('Docker Environment Variables') {
            steps {
                powershell '''
                    Write-Output "DOCKER_CONFIG:"
                    Write-Output $env:DOCKER_CONFIG

                    Write-Output "DOCKER_HOST:"
                    Write-Output $env:DOCKER_HOST

                    Write-Output "DOCKER_CONTEXT:"
                    Write-Output $env:DOCKER_CONTEXT

                    Write-Output "DOCKER_AUTH_CONFIG:"
                    Write-Output $(if ($env:DOCKER_AUTH_CONFIG) { "Present" } else { "Not present" })
                '''
            }
        }

    }

    post {
        success {
            echo 'Build, tests and Docker check completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}
