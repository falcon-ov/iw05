pipeline {
    agent {
        label 'ansible-agent'
    }
    
    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning repository with Ansible playbook...'
                checkout scm
            }
        }
        
        stage('Run Ansible Playbook') {
            steps {
                echo 'Executing Ansible playbook...'
                dir('lab05/ansible') {
                    sh '''
                        ansible-playbook -i hosts.ini setup_test_server.yml -v
                    '''
                }
            }
        }
    }
    
    post {
        always {
            echo 'Ansible pipeline completed.'
        }
        success {
            echo 'Test server configured successfully!'
        }
        failure {
            echo 'Configuration failed!'
        }
    }
}