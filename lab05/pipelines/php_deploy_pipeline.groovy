pipeline {
    agent {
        label 'ansible-agent'
    }
    
    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning repository with PHP project...'
                checkout scm
            }
        }
        
        stage('Deploy to Test Server') {
            steps {
                echo 'Deploying PHP project to test server...'
                dir('lab05') {
                    sh '''
                        # Копируем файлы проекта на test server
                        ansible test_servers -i ansible/hosts.ini -m file -a "path=/var/www/php-project state=directory owner=www-data group=www-data mode=0755" --become
                        
                        # Копируем исходники
                        ansible test_servers -i ansible/hosts.ini -m copy -a "src=php-project/src/ dest=/var/www/php-project/ owner=www-data group=www-data" --become
                        
                        # Создаем простой index.php если его нет
                        ansible test_servers -i ansible/hosts.ini -m copy -a "dest=/var/www/php-project/index.php content='<?php require_once \"Calculator.php\"; echo \"Calculator deployed successfully!\"; ?>' owner=www-data group=www-data" --become
                    '''
                }
            }
        }
    }
    
    post {
        always {
            echo 'Deployment pipeline completed.'
        }
        success {
            echo 'PHP project deployed successfully!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}