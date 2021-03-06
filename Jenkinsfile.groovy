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
        stage('Deploy Stage')
        {
            steps{
                //Depoy war into Tomcat Server
                deploy adapters: [tomcat9(credentialsId: 'tomcatcreds', path: '', url: 'http://52.66.255.84:8080')], contextPath: null, onFailure: false, war: '**/*.war'
            }
        }
    }
}