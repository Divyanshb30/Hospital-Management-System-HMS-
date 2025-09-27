pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    
    environment {
        PROJECT_NAME = 'Hospital Management System'
    }
    
    stages {
        stage('📋 Checkout') {
            steps {
                echo "🔄 Checking out source code..."
                checkout scm
            }
        }
        
        stage('🧹 Clean') {
            steps {
                echo "🧹 Cleaning previous builds..."
                bat 'mvn clean'
            }
        }
        
        stage('⚙️ Compile') {
            steps {
                echo "⚙️ Compiling Java source code..."
                bat 'mvn compile'
            }
        }
        
        stage('🧪 Test') {
            steps {
                echo "🧪 Running unit tests..."
                bat 'mvn test'
            }
            post {
                always {
                    // Publish test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    // Archive test reports
                    archiveArtifacts artifacts: 'target/surefire-reports/*', allowEmptyArchive: true
                }
            }
        }
        
        stage('📦 Package') {
            steps {
                echo "📦 Creating JAR file..."
                bat 'mvn package -DskipTests'
            }
        }
        
        stage('📊 Archive Artifacts') {
            steps {
                echo "📊 Archiving build artifacts..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('🔍 Code Quality Check') {
            steps {
                echo "🔍 Running code quality checks..."
                // You can add SonarQube or other code quality tools later
                bat 'mvn verify'
            }
        }
    }
    
    post {
        always {
            echo "🧹 Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "✅ ${PROJECT_NAME} build completed successfully!"
            echo "📱 Sending success notification..."
        }
        failure {
            echo "❌ ${PROJECT_NAME} build failed!"
            echo "📱 Sending failure notification..."
        }
    }
}
