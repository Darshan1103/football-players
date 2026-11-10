
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
