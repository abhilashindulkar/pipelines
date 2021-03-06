pipeline{
    
    agent any
    
    environment{
        mavenHome="/usr/share/maven"
    }
    
    stages{
        stage('CheckOut Stage'){
            steps{
                //Checkout code from GitHub
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/abhilashindulkar/maven-web-application.git']]])
            }
        }
        stage('Build Stage'){
            steps{
                //Maven Build with goal clean install
                sh "${mavenHome}/bin/mvn -f /var/lib/jenkins/workspace/JobPipeline/pom.xml clean install"
            }
        }
    }
    post{
            success{
                sh '''echo "moving war to s3"
                aws s3 cp /var/lib/jenkins/workspace/JobPipeline/target/maven-web-application.war "s3://stark-jenkins/$JOB_NAME/$BUILD_NUMBER/"
                '''
            }
        
        }
}