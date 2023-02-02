pipeline {
    agent any
    stages {

        stage('Unit test') {
            steps {
                echo "on à pas fait les UnitTests !!!"
            }
        }   

        stage('Build docker image') {
            steps {
                sh "docker build -t mbodji/test:v1.0.${BUILD_NUMBER} ."
                script {
                    try {
                        sh 'docker rm -f test'
                    }catch (exc) {
                        echo 'Erreur de supp'
                    }
                }
                sh "docker run --name test -d -p 8088:8080 mbodji/test:v1.0.${BUILD_NUMBER}"
            }
        }

        stage('Push docker image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD"
                    sh "docker push mbodji/test:v1.0.${BUILD_NUMBER}"
                }
            }
        }
    }
}
