pipeline {
    agent any
    stages {
        stage('down'){
            steps {
                sh 'docker-compose down -v'
            }
        }
        stage('build') {
            agent {
                    docker {
                        image 'gradle:jdk14'
                        args '-v /root/.gradle:/home/gradle/.gradle -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker'
                    }
                }
            steps {
                script {
                    env.MY_GIT_TAG = sh(returnStdout: true, script: 'git tag -l --points-at HEAD').trim()
                }
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
				sh 'printenv'
				//sh 'gradle build'
				sh 'docker'
				sh 'gradle docker --debug'
				sh 'gradle dockerTagLatest'
            }
        }
        stage('compose'){
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}