import jenkins.model.* ; 
import hudson.model.* ; 
import com.cloudbees.hudson.plugins.folder.* ; 
import org.jenkinsci.plugins.workflow.multibranch.* ; 

out = getBinding().out ; 

class Utilities {
    def private out = out ; 
    def private Folder[] allFolders  ; 
    Script script ; 
    final String CONFIRMED_MSG = "YES_I_DO_WANT_TO_DELETE_ALL_EM_ORPHAN"

    Utilities( out ) {
        this.out = out ; 
        this.getFolders() ; 
    }

    void getFolders() { 
        allFolders = Jenkins.instance.getAllItems(Folder.class) ; 
    } 
    
    void printFolders() { 
        this.allFolders.each { folder -> 
          out.println("Foldername is = " + folder.fullName)
        }
    } 

    Map<String,String> listOrphansMultiBranches( String folderName = ".*") {
        def orphanBranches = [:] ; 
        def int orphanBranchCounter = 0 ; 
        def int totalBranchCounter  = 0 ; 
        def int totalFolderCounter  = 0 ; 
        def int folderOrphanBranchCounter = 0 ; 
        this.getFolders() ; 
        
        allFolders.each { folder ->  
            folder.getItems().findAll { job -> 
              (job instanceof WorkflowMultiBranchProject) && (folder.name ==~ /${folderName}/) 
            }.each { job -> 
                folderOrphanBranchCounter = 0 ; 
                job.getItems().each { branch -> 
                    if( ! branch.buildable ) { 
                        if ( (orphanBranches[job.fullName] == "") 
                          || (orphanBranches[job.fullName] == null) ) {
                            orphanBranches[job.fullName] = "${branch.name}" ;
                        } else {
                            orphanBranches[job.fullName] = orphanBranches[job.fullName] + ";" + "${branch.name}" ; 
                        }
                        orphanBranchCounter++ ; 
                        folderOrphanBranchCounter++ ; 
                    } 
                    totalBranchCounter++ ; 
                } 
            } 
            totalFolderCounter++ ; 
        }
    out.println("=================>  Found ${orphanBranchCounter} orphan branches") ;   
    out.println("=================>  Found ${totalBranchCounter} total branches") ;  
    out.println("=================>  Found ${totalFolderCounter} total folders") ; 
    return ( orphanBranches ) ; 
    }

    // usage:  utilInstance.deleteOrphanBranches("Folder", "job", "branch") ; 
    int deleteOrphanBranches( String folderName = "", 
      String jobName = ".*", 
      String branchName = ".*") { 
        println("Delete orphan branches in MultiBranch Job") ; 
        try { 
          assert( folderName != null && folderName != "") ; 
        }
        catch (AssertionError  ex) {
          out.println("No folder is specified ... No action taken") ;
          return 1 ; 
        }
        allFolders.findAll { f -> 
            (f.name == folderName) && (f instanceof Folder)
        }.each { folder -> 
            folder.getItems().findAll { job ->
                (job instanceof WorkflowMultiBranchProject) && (job.name ==~ /${jobName}/)
            }.each { job -> 
                job.getItems().findAll { br -> 
                    (! br.buildable) && (br.name ==~ /${branchName}/)
                }.each { branch -> 
                    out.println("== danger: deleting orphan branch ${branch.fullName}==") ; 
                    branch.delete() ; 
                    sleep(50) ; // breathing room to avoide racing
                } 
            } 
        } 
        return 0 ; 
    } // deleteOrphanBranches

    void listAllOrphans() {
        def orphanBranches = [:] ; 
        def counter = 1 ; 
        orphanBranches = this.listOrphansMultiBranches() ; 
        out.println("\n== ORPHAN BRANCHES LISTED BY PROJECT NAME ==")
        orphanBranches.each { branch -> 
            out.println("${counter++} - Project = ${branch.key} has " 
                     + branch.value.split(";").size() 
                     + " orphanBranches <=> ${branch.value}\n" )
        }
    }

    ArrayList autoDeleteAllOrphans(String confirmation) {
        def ArrayList problemFolders = [] ; 
        if ( "${confirmation}" != "${CONFIRMED_MSG}") {
            out.println("[WARN] Incorrect confirmation msg entered.  Please double check before re-run") ; 
            return problemFolders ; 
        }
        allFolders.each { folder -> 
            if (deleteOrphanBranches(folder.name)) {
                problemFolders.add(folder.name)
            }
        }
        return problemFolders ; 
    }
} // class def

def utilInstance = new Utilities(out) ; 

// list all orphan branches by project names
utilInstance.listAllOrphans() ; 

// auto delete all orphans in all folders
def ArrayList folderOrphanErrList = utilInstance.autoDeleteAllOrphans("YES_I_DO_WANT_TO_DELETE_ALL_EM_ORPHANS") ; 
if (folderOrphanErrList.size() > 0 ) {
    println("====> Unable to delete orphans from the following branches ...")
    folderOrphanErrList.each { folderName -> 
        println("=> Check folderName = " + folderName) ; 
    }
    return 1 ; 
} 
