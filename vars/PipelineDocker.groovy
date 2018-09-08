/***********************************************************************************
**** Description :: This groovy code is used Build JAVA Code and Deploye Docker  ****
**** Created By  :: Pramod Vishwakarma                                           ****
**** Created On  :: 08/09/2018                                                   ****
**** version     :: 1.0                                                          ****
************************************************************************************/
import docker.devops.scm.*
import docker.devops.build.*
import docker.devops.sonar.*
import docker.devops.packages.*
import docker.devops.deploy.*

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   timestamps {
     try {
        def g = new git()
	def m = new mavenBuild()
        def D = new DockerBuild()
	def Dkr = new DockerAppDeploy()
        currentBuild.result = "SUCCESS"
        NEXT_STAGE = "none"
        branch_name = new ChoiceParameterDefinition('BRANCH', ['development','master'] as String[],'')
        value = input(message: 'Please select specified inputs', parameters: [branch_name])
        if(value == 'development') {
               LINUX_CREDENTIALS = 'FCA-DEV-R1'
               DEPLOYMENT_SERVERS = '192.168.56.101'
	       ENVIRONMENT = 'development'
               BRANCH = 'development'
               }
	if(value == 'master') {
               LINUX_CREDENTIALS = 'FCA-TEST-R1'
               DEPLOYMENT_SERVERS = '192.168.56.102'
	       ENVIRONMENT = 'master'
	       BRANCH = 'master'
	       }

        stage ('\u2780 Code Checkout') {
           	g.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
           	NEXT_STAGE="maven_build"
           	}
	stage ('\u2781 Maven Build') {
	        while (NEXT_STAGE != "maven_build") {
                continue
                }
	        m.MavenBuild("${config.MAVEN_HOME}","${config.MAVEN_GOAL}")
		NEXT_STAGE="code_analysis"
	   	}
	stage ('\u2782 Sonar Analysis') {
		while (NEXT_STAGE != "code_analysis") {
                continue
		}
	        def s = new MavenSonarAnalysis()
		s.SonarAnalysis("${config.SONAR_PROPERTY}")
		NEXT_STAGE="dockerImageBuild"
		}
	stage ('\u2783 Docker Tasks') {
          parallel (
		"\u278A Docker Build" : {
                while (NEXT_STAGE != "dockerImageBuild") {
                continue
                }
                D.buildDockerImages("${config.DOCKER_USER}","${config.DOCKER_APP_NAME}","${config.DOCKER_TAG}")
                NEXT_STAGE='pushDcokerHub'
                },
		"\u278B Docker Push Hub" : {
             	while (NEXT_STAGE != "pushDcokerHub") {
               	continue
             	}
		D.pushDockerImages("${config.DOCKER_USER}","${config.DOCKER_APP_NAME}","${config.DOCKER_TAG}")
		NEXT_STAGE='UnDeployContainer'
		},
		failFast: true
		)
	      }
	stage ('\u2784 Deployment Tasks') {
          parallel (
                "\u278A UnDeploy Container" : {
                while (NEXT_STAGE != "UnDeployContainer") {
                continue
                }
                Dkr.UnDeployContainer("${config.DEPLOYMENT_SERVERS}","${config.LINUX_USER}","${config.CONTAINER_NAME}")
                NEXT_STAGE='DeployContainer'
                },
                "\u278B Container Deployment" : {
                while (NEXT_STAGE != "DeployContainer") {
                continue
                }
                Dkr.DeployContainer("${config.DEPLOYMENT_SERVERS}","${config.LINUX_USER}","${config.CONTAINER_NAME}","${config.DOCKER_TAG}","${config.DOCKER_USER}")
		NEXT_STAGE='none'
                },
                failFast: true
                )
              }
	}
       	catch (Exception caughtError) {
          wrap([$class: 'AnsiColorBuildWrapper']) {
             print "\u001B[41mERROR => Docker pipeline failed, check detailed logs..."
             currentBuild.result = "FAILURE"
             throw caughtError
        }
     }
  } 
}
