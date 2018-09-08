/***********************************************************************************
**** Description :: This groovy code is used Build JAVA Code and Deploye Docker  ****
**** Created By  :: Pramod Vishwakarma                                           ****
**** Created On  :: 08/09/2018                                                   ****
**** version     :: 1.0                                                          ****
************************************************************************************/
import docker.devops.scm.*
import docker.devops.build.*

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
           def git = new git()
           g.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
           NEXT_STAGE="maven_build"
           }
	stage ('\u2781 Maven Build') {
	         while (NEXT_STAGE != "clean_package") {
                 continue
                 }
	         m.MavenBuild("${config.MAVEN_HOME}","${config.MAVEN_GOAL}")
	   }
	}
       catch (Exception caughtError) {
          wrap([$class: 'AnsiColorBuildWrapper']) {
             print "\u001B[41mERROR => fcaa pipeline failed, check detailed logs..."
             currentBuild.result = "FAILURE"
             throw caughtError
        }
     }
  } 
}
