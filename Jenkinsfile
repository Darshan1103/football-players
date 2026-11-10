
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
