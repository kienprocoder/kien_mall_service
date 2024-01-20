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
        stage('Install') {
            steps {
                bat "mvn install -f kien_mall_service"
            }
        }
        stage('Deploy') {
            steps {
                bat "mvn deploy -f kien_mall_service"
            }
        }
        stage('Test') {
            steps {
                bat "mvn test -f kien_mall_service"
            }
        }
        stage('Package') {
            steps {
                bat "mvn package -f kien_mall_service"
            }
        }
    }
}
