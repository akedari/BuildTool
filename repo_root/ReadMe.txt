READ ME

1. Copy the repo_root.jar to the location at /src directory Level (same level as ReadMe.txt)
2. Open terminal and navigate to the location where you copied JAR (repo_root.jar)
3. Type command "java -jar repo_root.jar" and press Enter
4. Please Enter the command to test 
   Format:
   validate_approvals --approvers <approvers-loginName> --changed-files <relative-address-updatedFile>
5. If command runs Successfully, Output will be displayed on terminal Or Error Message will be displayed on terminal



Example 1:
validate_approvals --approvers alovelace --changed-files src/com/twitter/user/User.java src/com/twitter/follow/Follow.java
Insufficient approvals

Example 2:
validate_approvals --approvers ghooper eclarke ghopper --changed-files src/com/twitter/user/User.java
Approved

Example 3:
validate_approvals  ghooper eclarke ghopper --changed-files src/com/twitter/user/User.java
Invalid Command !!!

Example 4:
validate_approvals --approvers ghooper eclarke ghopper --changed-files src/com/twitter/user/User.java src/com/twitter/follow/Follow.java
Approved

Example 5:
validate_approvals --approvers  --changed-files
Insufficient approvals




EXTRA STEPS:

1. If you want to build the project, please open source folder in any IDE.
2. Follow the steps to build jar from that IDE or Directly run the project in IDE itself, Project has included 
   sample project structure so you can Test on that or update source folder structure.