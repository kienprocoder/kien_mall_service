pipeline {
    agent any
    stages {
        stage('git repo & clean') {
            steps {
               // bat "rmdir  /s /q kien_mall_service"
                bat "git clone https://github.com/kienprocoder/kien_mall_service.git"
                bat "mvn clean -f kien_mall_service"
            }
        }
        stage('install') {
            steps {
                bat "mvn install -f kien_mall_service"
            }
        }
        stage('deploy') {
            steps {
                bat "mvn deploy -f kien_mall_service"
            }
        }
        stage('test') {
            steps {
                bat "mvn test -f kien_mall_service"
            }
        }
        stage('package') {
            steps {
                bat "mvn package -f kien_mall_service"
            }
        }
    }
}
