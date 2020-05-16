pipeline {
    agent any
    stages {
        stage('pre'){
            steps{
                script {
                    env.MY_GIT_TAG = sh(returnStdout: true, script: 'git tag -l --points-at HEAD').trim()
                }
            }
        }
        stage('down'){
            when {
                expression {
                    return env.MYENV == 'TEST'||(env.MYENV == 'PROD'&&env.MY_GIT_TAG.startsWith("v"))
                    }
                }
            steps {
                sh 'docker-compose down -v'
            }
        }
        stage('build') {
            when {
                expression {
                    return env.MYENV == 'TEST'||(env.MYENV == 'PROD'&&env.MY_GIT_TAG.startsWith("v"))
                    }
                }
            agent {
                    docker {
                        image 'gradle:jdk14'
                        args '-v /root/.gradle:/home/gradle/.gradle -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker -e "MYENV='+env.MYENV+''
                    }
                }
            steps {
				sh 'gradle docker'
				sh 'gradle dockerTagLatest'
            }
        }
        stage('compose'){
            when {
                expression {
                    return env.MYENV == 'TEST'||(env.MYENV == 'PROD'&&env.MY_GIT_TAG.startsWith("v"))
                    }
                }
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}