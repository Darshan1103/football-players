
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


        stage('Docker Hub Credential Check') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USER',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    powershell '''
                        Write-Output "Docker Hub username: $env:DOCKERHUB_USER"
                        Write-Output "Token length: $($env:DOCKERHUB_TOKEN.Length)"

                        if ([string]::IsNullOrEmpty($env:DOCKERHUB_TOKEN)) {
                            throw "Docker Hub token was not received from Jenkins credentials."
                        }
                    '''
                }
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
