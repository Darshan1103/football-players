pipeline {
    agent any

    environment {
        DOCKER = 'C:\\Users\\Darshan\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        IMAGE_NAME = 'darshan1103/football-players'
        IMAGE_TAG = "${env.BUILD_NUMBER}"      // new tag every build so Kubernetes rolls out the new image
        KUBECTL = 'C:\\tools\\kubectl.exe'
        K8S_NAMESPACE = 'football'
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('Secret Scan (Gitleaks)') {
            steps {
                echo 'Scanning code and git history for leaked secrets...'
                bat '"%DOCKER%" run --rm -v "%WORKSPACE%:/repo" zricethezav/gitleaks:latest detect --source=/repo --redact -v'
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Building and testing Football Players application...'
                bat 'mvnw.cmd clean package'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
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
                bat '"%DOCKER%" build -t football-players:%IMAGE_TAG% .'
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

                            $fullImage = "$($env:IMAGE_NAME):$($env:IMAGE_TAG)"

                            & $docker tag "football-players:$($env:IMAGE_TAG)" $fullImage
                            if ($LASTEXITCODE -ne 0) {
                                throw "Docker image tagging failed."
                            }

                            Write-Output "Pushing image to Docker Hub..."

                            & $docker push $fullImage
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

        stage('Deploy to AKS') {
            steps {
                withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"
                        $kubectl = $env:KUBECTL
                        $fullImage = "$($env:IMAGE_NAME):$($env:IMAGE_TAG)"

                        # Put this build's image into the manifest and apply it
                        $manifest = (Get-Content k8s/deployment.yaml -Raw).Replace("IMAGE_PLACEHOLDER", $fullImage)
                        $manifest | & $kubectl apply -f -
                        if ($LASTEXITCODE -ne 0) { throw "kubectl apply failed" }

                        & $kubectl annotate deployment/football-players -n $env:K8S_NAMESPACE --overwrite `
                            kubernetes.io/change-cause="Jenkins build $env:BUILD_NUMBER - $fullImage"
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                withCredentials([file(credentialsId: 'aks-kubeconfig', variable: 'KUBECONFIG')]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"
                        $kubectl = $env:KUBECTL
                        $ns = $env:K8S_NAMESPACE

                        & $kubectl rollout status deployment/football-players -n $ns --timeout=300s
                        if ($LASTEXITCODE -ne 0) { throw "Rollout did not finish" }

                        & $kubectl get deployment,pods,svc -n $ns -o wide

                        # Wait for the Azure Load Balancer public IP
                        $ip = ""
                        for ($i = 0; $i -lt 30 -and -not $ip; $i++) {
                            $ip = & $kubectl get svc football-players -n $ns -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
                            if (-not $ip) { Write-Output "Waiting for external IP..."; Start-Sleep -Seconds 10 }
                        }
                        if (-not $ip) { throw "Service never got an external IP" }

                        # Smoke test: the API must answer with HTTP 200
                        $ok = $false
                        for ($i = 0; $i -lt 12 -and -not $ok; $i++) {
                            try {
                                $r = Invoke-WebRequest -Uri "http://$ip/api/players" -UseBasicParsing -TimeoutSec 15
                                $ok = ($r.StatusCode -eq 200)
                            } catch { Start-Sleep -Seconds 5 }
                        }
                        if (-not $ok) { throw "Application is not responding at http://$ip/" }

                        Write-Output "Application is live at: http://$ip/"
                    '''
                }
            }
        }

    }

    post {
        success {
            echo "Build ${BUILD_NUMBER}: tested, pushed to Docker Hub and deployed to AKS successfully!"
        }

        failure {
            echo 'Pipeline failed. Please check the Jenkins console output.'
        }
    }
}