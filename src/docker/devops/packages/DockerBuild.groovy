/***************************************************************************
***** Description :: This Package is used to perform Docker Build tasks *****
***** Author      :: Pramod Vishwakarma                                 *****
***** Date        :: 09/08/2018                                        *****
***** Revision    :: 1.0                                               *****
****************************************************************************/
package docker.devops.packages

/***********************************************
***** Function to Build Docker Images	    *****
************************************************/
def buildDockerImages(String DOCKER_USER, String DOCKER_APP_NAME, String DOCKER_TAG)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Building Docker Images ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}, please wait..."
	sh "docker build -t ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG} ."
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to Build Docker Images ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}
/************************************************************
***** Function to Push Docker Images to DockerHub       *****
************************************************************/
def pushDockerImages(String DOCKER_PASSWORD)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Pushing Images ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG} to DockerHub, please wait..."
	   withCredentials([string(credentialsId: 'Docker-Pass', variable: 'DockerHubPass')]) {
           sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}"
	   sh "docker push ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}"
         }
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to Push Images ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}
/**************************************************************

***************************************************************/

