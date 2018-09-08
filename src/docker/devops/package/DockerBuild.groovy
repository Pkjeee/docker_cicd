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
        println "\u001B[32mINFO => Building Docker Images, please wait..."
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

