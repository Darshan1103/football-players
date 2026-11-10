
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
