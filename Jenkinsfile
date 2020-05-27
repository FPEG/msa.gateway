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
                sh "docker-compose -f docker-compose.yml -f docker-compose.${env.MYENV}.yml down -v"
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
--network=msa-dev-net
-e MYENV="${env.MYENV}"
""" + ((env.MYPROXY_HOST == null)? "" : """
-e HTTP_PROXY="http://${env.MYPROXY_HOST}:${env.MYPROXY_PORT}"
-e HTTPS_PROXY="http://${env.MYPROXY_HOST}:${env.MYPROXY_PORT}"
-e NO_PROXY="121.36.41.244"
""")
                    }
                }
            steps {
				sh """
gradle \
""" + ((env.MYPROXY_HOST == null)? "" : """ \
 -Dhttp.proxyHost=${env.MYPROXY_HOST} \
 -Dhttp.proxyPort=${env.MYPROXY_PORT} \
 -Dhttp.nonProxyHosts=121.36.41.244 \
 -Dhttps.proxyHost=${env.MYPROXY_HOST} \
 -Dhttps.proxyPort=${env.MYPROXY_PORT} \
 -Dhttps.nonProxyHosts=121.36.41.244 \
""") + """ \
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
                sh "docker-compose -f docker-compose.yml -f docker-compose.${env.MYENV}.yml up -d"
            }
        }
    }
}