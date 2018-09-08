/*****************************************************************************
***** Description :: This Package is used to perform Java Build tasks    *****
***** Author      :: Pramod Vishwakarma                                  *****
***** Date        :: 12/09/2018                                         *****
***** Revision    :: 1.0                                                 *****
******************************************************************************/
package docker.devops.reports

/***********************************************
***** Function to publish the HTML reports *****
************************************************/
def publishHtmlReport(String report_file, String report_directory, String report_title)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => publishing HTML report in Jenkins with index file name ${report_file}, please wait..."
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: "$report_directory", reportFiles: "$report_file", reportName: "$report_title", reportTitles: ''])
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to generate HTML report for ${report_file}, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

