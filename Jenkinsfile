pipeline {
    agent any
    stages {
        stage('git repo & clean') {
            steps {
               bat "rmdir  /s /q KienMallServiceJunitTesting"
                bat "git clone https://github.com/kienprocoder/kien_mall_service.git"
                bat "mvn clean -f KienMallServiceJunitTesting"
            }
        }
        stage('Install') {
            steps {
                bat "mvn install -f KienMallServiceJunitTesting"
            }
        }
        stage('Deploy') {
            steps {
                bat "mvn deploy -f KienMallServiceJunitTesting"
            }
        }
        stage('Test') {
            steps {
                bat "mvn test -f KienMallServiceJunitTesting"
            }
        }
        stage('Package') {
            steps {
                bat "mvn package -f KienMallServiceJunitTesting"
            }
        }
    }
}