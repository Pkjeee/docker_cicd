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
def pushDockerImages(String DOCKER_USER)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Pushing Images to DockerHub, please wait..."
	   withCredentials([string(credentialsId: 'Docker-Pass', variable: 'DockerHubPass')]) {
           sh "docker login -u ${DOCKER_USER} -p ${DockerHubPass}"
	   sh "docker push ${DOCKER_USER}/${DOCKER_APP_NAME}:${DOCKER_TAG}"
         }
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to Push Images, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}
/**************************************************************

***************************************************************/

