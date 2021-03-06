pipeline{
    
    agent any
    environment{
            registry='abhilash007/stark'
            registryCredential='docker-creds'
            dockerImage=''
        }
        
        stages{
            stage('CheckOut'){
                steps{
                    //checkout code
                    checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/abhilashindulkar/linux_tweet_app.git']]])
                }
            }
            stage('DockerBuild'){
                steps{
                    //Build Docker Image
                    script{
                        dockerImage=docker.build registry
                    }
                }
            }
            stage('DockerPush')
            {
                steps{
                    //Push Docker Image into Registry
                    script{
                        docker.withRegistry( '', registryCredential ) {
                        dockerImage.push()
                        }
                    }
                }
            }
            stage('DockerContainerStop'){
                steps{
                      //StopContainerIfPresent
                      sh 'docker ps -f name=testContainer -q | xargs --no-run-if-empty docker container stop'
                      sh 'docker container ls -a -fname=testContainer -q | xargs -r docker container rm'
                }
            }
            stage('DockerContainerStart'){
                steps{
                    //RunContainer
                    script{
                        dockerImage.run("-p 80:80 --rm --name testContainer")
                    }
                }
            }
    }
}