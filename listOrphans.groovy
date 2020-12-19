import jenkins.model.* ; 
import hudson.model.* ; 
import com.cloudbees.hudson.plugins.folder.* ; 
import org.jenkinsci.plugins.workflow.multibranch.* ; 
// import jenkins.branch.* ; //org folder
// import org.jenkinsci.plugins.workflow.job.* ; 

// def out = getBinding().out ; 

class Utilities {
    private String name = "NoName" ; 
    Script script ; 

    Utilities( String name ) {
        this.name = name 
    }

    Map<String,String> listOrphansMultiBranches() {
        def orphanBranches = [:] ; 
        Jenkins.instance.getAllItems(Folder.class).each { folder -> 
            folder.getItems().findAll { job -> 
                job instanceof WorkflowMultiBranchProject 
            }.each { job -> 
                job.getItems().each { branch -> 
                    if( ! branch.buildable ) { 
                        if ( (orphanBranches[job.fullName] == "") || (orphanBranches[job.fullName] == null) ) {
                            orphanBranches[job.fullName] = "${branch.name}" ; 
                        } else {
                            orphanBranches[job.fullName] = orphanBranches[job.fullName] + "; " + "${branch.name}" ; 
                        }
                    } // if 
                } // myItem.getItems
            } // job
        } // allFolder
        return ( orphanBranches ) ; 
    } // listOrphanMultiBranchJob

    void deleteOrphansMultiBranches() { 
        println("Delete orphan branches in MultiBranch Job") ; 
        Jenkins.instance.getAllItems(Folder.class).each { folder ->
            folder.getItems().findAll { job ->
                job instanceof WorkflowMultiBranchProject
            }.each { job -> 
                job.getItems().each { branch -> 
                    if ( ! branch.buildable && branch.name == "branch20" ) {
                        branch.delete() ; 
                    }
                }
            }
        }
    }

    String getName() {
        script.println("===========Hello =============") ; 
        return name ; 
    }
} // class def

def myUtil = new Utilities("James") ; 
println("The name is = " + myUtil.getName() ) ; 

/*
// test listOrphansMultiBranch
def orphanBranches = [:] ; 
orphanBranches = myUtil.listOrphansMultiBranches()
def counter = 1 ; 
orphanBranches.each { branch ->
  // println(branch) ; 
  println("${counter++} - Project = ${branch.key} <=> orphanBranches = ${branch.value}" )
} ;
*/

// test deleteOrphansMultiBranches
// myUtil.deleteOrphansMultiBranches() ;  
