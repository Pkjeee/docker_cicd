/**********************************************************************
***** Description :: This Package is used to run Sonar Analysis   *****
***** Author      :: Pramod Vishwakarma                           *****
***** Date        :: 08/09/2018                                   *****
***** Revision    :: 1.0                                          *****
***********************************************************************/

package docker.devops.sonar

def SonarAnalysis(String SONAR_PROPERTY)
{
    try {
      
        if ( "${SONAR_PROPERTY}" == "null" ) {
	      SONAR_PROPERTY = "sonar-runner.properties"
	    }
	    if (fileExists("${SONAR_PROPERTY}"))
        {
            wrap([$class: 'AnsiColorBuildWrapper']) {
              println "\u001B[32m[INFO] running sonar analysis with file ${SONAR_PROPERTY}, please wait..."
              withSonarQubeEnv ('SONARQUBE') {
                 sh "/app/sonarscanner3/bin/sonar-scanner -Dproject.settings=${SONAR_PROPERTY}"
              }
		currentBuild.result = 'SUCCESS'
            }
        }
        else
        {
            wrap([$class: 'AnsiColorBuildWrapper']) {
               println "\u001B[41m[ERROR] ${SONAR_PROPERTY} file does not exist..."
            }
        }
    }
    catch (Exception error) {
        wrap([$class: 'AnsiColorBuildWrapper']) {
           println "\u001B[41m[ERROR] failed to run sonar analysis using ${SONAR_PROPERTY}..."
		   currentBuild.result = 'FAILED'
           throw error
        }
    }
}

def SonarPreview(String SONAR_PROPERTY)
{
    try {
      
        if ( "${SONAR_PROPERTY}" == "null" ) {
	      SONAR_PROPERTY = "sonar-runner.properties"
	    }
	    if (fileExists("${SONAR_PROPERTY}"))
        {
            wrap([$class: 'AnsiColorBuildWrapper']) {
              println "\u001B[32m[INFO] running sonar analysis with file ${SONAR_PROPERTY}, please wait..."
              withSonarQubeEnv {
                 sh "sonar-scanner -Dsonar.analysis.mode=preview -Dsonar.dryRun=true -Dproject.settings=${SONAR_PROPERTY}"
              }
			  currentBuild.result = 'SUCCESS'
            }
        }
        else
        {
           wrap([$class: 'AnsiColorBuildWrapper']) {
              println "\u001B[41m[ERROR] ${SONAR_PROPERTY} file does not exist..."
           }
        }
    }
    catch (Exception error) {
        wrap([$class: 'AnsiColorBuildWrapper']) {
           println "\u001B[41m[ERROR] failed to run sonar analysis using ${SONAR_PROPERTY}..."
		   currentBuild.result = 'FAILED'
           throw error
        }
    }
}

