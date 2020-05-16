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
                        args """
-v /root/.gradle:/home/gradle/.gradle
-v /var/run/docker.sock:/var/run/docker.sock
-v /usr/bin/docker:/usr/bin/docker
-e MYENV="${env.MYENV}"
-e HTTP_PROXY="http://172.17.14.139:1081"
-e HTTPS_PROXY="http://172.17.14.139:1081"
-e NO_PROXY="121.36.41.244"
"""
                    }
                }
            steps {
				sh """
gradle \
 -DsystemProp.http.proxyHost=172.17.14.139 \
 -DsystemProp.http.proxyPort=1081 \
 -DsystemProp.http.nonProxyHosts=121.36.41.244 \
 -DsystemProp.https.proxyHost=172.17.14.139 \
 -DsystemProp.https.proxyPort=1081 \
 -DsystemProp.https.nonProxyHosts=121.36.41.244 \
 docker
"""
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