// need credentials jcn2020 to the public repo.
// working ok - build per trigger << need to login as admin >> 

import jenkins.model.*
import hudson.model.*

import org.jenkinsci.plugins.workflow.job.* ;  //  job
import com.cloudbees.hudson.plugins.folder.* ;  // folder 
import org.jenkinsci.plugins.workflow.multibranch.* ;  // multibranch
import jenkins.branch.* ; // OrganizationFolder

def myFolder = "folder-DslMultibranchGithub2" ; 
def myJobName = "job-dslMultibranchGithub" ; 
def int unitToKeep = 1 ; 
def String intervalFolderTrigger = "3d" ; //m mins; d days 

def String owner = "jcn2020"
def String userPassCredId = "jcn2020Pat" 
// def String myId = "001122334"
def String myRepo = "https://github.com/jcn2020/tempLoc2.git" 
def myId = UUID.nameUUIDFromBytes(myRepo.getBytes()).toString() 
def myApiUri = "http://api.github.com"

def mainRepo = myRepo =~ /\/([^\/]+)\.git/ ; 

// def myApiUri = "https://scm.starbucks.com/api/v3"

// create folder
folder(myFolder) {
  displayName = myFolder ; 
  description = "jobDslMultibranch" ;
} 


// create multibranch pipeline job
def jobPath = myFolder + "/" + myJobName ; 

multibranchPipelineJob(jobPath) {
    displayName(myJobName)
    description("Play with multibranch pipeline")

    branchSources {
      branchSource {  
 /*
        buildStrategies { 
          skipInitialBuildOnFirstBranchIndexing()
          
          buildTags { 
            atMostDays("3")
          } // buildTags
          
        } // buildStrategies
 */
        source {
          github {
              repoOwner(owner) ; 
              repository(mainRepo[0][1]) 
              repositoryUrl(myRepo)
              configuredByUrl(true)
              credentialsId(userPassCredId)
              id(myId) 
              // includes("**") // deprecated
              traits {
                gitTagDiscovery()
                gitHubBranchDiscovery {
                  strategyId(3) // 3 = all branches.
                }
                
              }
              // apiUri(myApiUri) doing what? 
              // ignoreOnPushNotifications(false)
          } // github
        } // source 

      } // branchSource
    } //branchSources
  
    /*
    // trigger folder scan
    triggers {
      periodicFolderTrigger {
        interval(intervalFolderTrigger) 
      } // periodicFolderTrigger
    } // triggers 
    */
     
    // this is to keep number of builds
    orphanedItemStrategy {
      // Hope jenkins would remove deadbranch itself
      // may try to play with unit to keep value. 
  /*
      defaultOrphanedItemStrategy {
        // remove dead branches.
        pruneDeadBranches(true)
        daysToKeepStr(unitToKeep.toString())
        numToKeepStr(unitToKeep.toString())
      }
  */
      discardOldItems {
        numToKeep(unitToKeep)
        daysToKeep(unitToKeep)
      }
    } // orphanItemStrategy
  

    // queue(jobPath) ;
    // sleep(30) ; 
    // queue(jobPath) ; 
}
