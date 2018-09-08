/***************************************************************************
***** Description :: This Package is used to Depoy Docker Images	*****
***** Author      :: Pramod Vishwakarma                                 *****
***** Date        :: 09/08/2018                                        *****
***** Revision    :: 1.0                                               *****
****************************************************************************/

package docker.devops.deploy

/****************************************************************************
******	Function to stop & undeploye earlir  Containers 	     *******
********************************************************************** *****/

def UnDeployContainer(String DEPLOYMENT_SERVERS, String LINUX_USER, String CONTAINER_NAME) 
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => UnDeploy Docker Conatiner is in progress at ${DEPLOYMENT_SERVERS}, please wait..."
	for (LINUX_SERVER in DEPLOYMENT_SERVERS.split(',')) {
//	   withCredentials([usernameColonPassword(credentialsId: 'LINUX-PASS', variable: 'PASSWORD')]) {
//            sshagent(['PASSWORD']) {
            sh "ssh -o StrictHostKeyChecking=no ${LINUX_USER}@${LINUX_SERVER} | docker rm -f ${CONTAINER_NAME}"
//	    }
//	 }
       }
     }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to UnDeployment at ${DEPLOYMENT_SERVERS}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/****************************************************************************
******  Function to Deploye NEW Containers & Start                  *******
****************************************************************************/

def DeployContainer(String DEPLOYMENT_SERVERS, String LINUX_USER, String DOCKER_USER, String DOCKER_APP_NAME, String DOCKER_TAG, String CONTAINER_NAME)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Docker WebApp Deployment is in progress at ${DEPLOYMENT_SERVERS}, please wait..."
        for (LINUX_SERVER in DEPLOYMENT_SERVERS.split(',')) {
//	withCredentials([usernameColonPassword(credentialsId: 'LINUX-PASS', variable: 'PASSWORD')]) {
//        sshagent(['PASSWORD']) {
        sh "ssh -o StrictHostKeyChecking=no ${LINUX_USER}@${LINUX_SERVER} | docker run -p 8080:8080 -d --name ${CONTAINER_NAME} ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}"
//	  }
       }
     }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to Docker WebApp Deployment at ${DEPLOYMENT_SERVERS}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

