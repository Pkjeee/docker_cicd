/**************************************************************
***** Description :: This Package is used for Maven Build *****
***** Author      :: Prmaod Vishwakarma                   *****
***** Date        :: 08/08/2018                           *****
***** Revision    :: 1.0                                  *****
***************************************************************/

package docker.devops.docker

/**********************************************
***** Function to Build Project packages  *****
***********************************************/
def createPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY, String WAR_NAME)
{
  try {
    wrap([$class: 'AnsiColorBuildWrapper']) {
      println "\u001B[32mINFO => Creating compressed package ${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, please wait..."
      sh "echo $BUILD_NUMBER > BUILD_NUMBER && cd $BUILD_PACKAGE_DIRECTORY && tar -cvzf ${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz ${WAR_NAME}"
    }
  }
  catch (Exception caughtException) {
    wrap([$class: 'AnsiColorBuildWrapper']) {
       println "\u001B[41mERROR => failed to compressed package ${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, exiting..."
       currentBuild.result = 'FAILED'
       throw caughtException
    }
  }
}

/**************************************************
***** Function to copy the mavaen build package *****
***************************************************/
def copyBuildPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY, String LINUX_CREDENTIALS, String DEPLOYMENT_PACKAGE_DIRECTORY, String DEPLOYMENT_SERVERS, String LINUX_USER)
{
  try {
    wrap([$class: 'AnsiColorBuildWrapper']) {
       println "\u001B[32mINFO => Copying ${WAR_NAME} archived package ${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz to ${DEPLOYMENT_SERVERS}, please wait..."
        for (LINUX_SERVER in DEPLOYMENT_SERVERS.split(',')) {
           sshagent(["${LINUX_CREDENTIALS}"]) {
             sh "scp ${BUILD_PACKAGE_DIRECTORY}/${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz ${LINUX_USER}@${LINUX_SERVER}:$DEPLOYMENT_PACKAGE_DIRECTORY"
           }
      }
    }
  }
  catch(Exception caughtException) {
    wrap([$class: 'AnsiColorBuildWrapper']) {
       println "\u001B[41mERROR => failed to copy the JAVA package  ${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, exiting..."
       currentBuild.result = 'FAILED'
       throw caughtException
    }
  }
}

/*********************************************************
***** Function to cleanup the old  build package     *****
**********************************************************/
def cleanBuildPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY)
{
   try {
     wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Cleaning up old Java Packages for brand ${BRAND_NAME}, please wait..."
        def PACKAGES = sh (script: "ls -t ${BUILD_PACKAGE_DIRECTORY}/${BRAND_NAME}*.tar.gz | tail -n +3",returnStdout: true).trim()
        if (PACKAGES) {
        sh "ls -t ${BUILD_PACKAGE_DIRECTORY}/${BRAND_NAME}*.tar.gz | tail -n +3 | xargs rm --"
        }
      }
    }
   catch(Exception caughtException) {
     wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to clean the old JAVA packages for brand ${BRAND_NAME}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}
