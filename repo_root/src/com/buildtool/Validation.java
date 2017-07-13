package com.buildtool;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by abhijeetkedari on 4/20/17.
 */


public class Validation {
    public static final String OWNERS = "OWNERS";
    public static final String DEPENDENCIES = "DEPENDENCIES";
    public static Map<String, DependencyNode> nodes = new HashMap<>();

    public static void main(String[] args) {

        String[] tokens = new Scanner(System.in).nextLine().split(" ");

        Set<String> approvers;
        Set<String> updatedFiles;
        Set<String> updatedDirectories;


        if (isValidArgumentsList(tokens)) {

            approvers = getInitialApprovers(tokens);
            updatedFiles = getInitialUpdatedFiles(tokens);
            updatedDirectories = getInitialUpdatedDirectories(tokens);

            // Check for all files entered from command line inputs are valid
            if (isFileExists(updatedFiles)) {
                String baseAddress = Paths.get(".").toAbsolutePath().normalize().toString();
                generateDependancyGraph(new File(baseAddress).listFiles(), baseAddress);

                if (isValid(approvers, updatedDirectories)) {
                    System.out.println("Approved");
                } else {
                    System.out.println("Insufficient approvals");
                }
            }
        } else {
            System.out.println("Invalid Command !!!");
        }
    }   //end of main()

    /*
         Return set of updated directories
         Eg. input:
              @tokens = ["validate_approvals","--approvers", "alovelace", "--changed-files", "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
             output:
              @Set =  return ("src/com/twitter/user", "src/com/twitter/follow")
    */
    static Set<String> getInitialUpdatedDirectories(String[] tokens) {
        Set<String> uddatedDirectories = new HashSet<>();
        int i = 2;

        if (tokens[1].equalsIgnoreCase("--changed-files")) {
            while (!tokens[i].equalsIgnoreCase("--approvers") && i < tokens.length) {
                uddatedDirectories.add(getParent(tokens[i]));
                i++;
            }
        } else {
            while (!tokens[i].equalsIgnoreCase("--changed-files")) {
                i++;
            }
            i++;
            while (i < tokens.length) {
                uddatedDirectories.add(getParent(tokens[i]));
                i++;
            }
        }
        return uddatedDirectories;
    }

    /*
        Return set of Initially UpdatedFiles
        Eg. input:
             @tokens = ["validate_approvals","--approvers", "alovelace", "--changed-files", "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
            output:
             @Set = return ("src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java")
    */

    static Set<String> getInitialUpdatedFiles(String[] tokens) {
        Set<String> updatedFiles = new HashSet<>();
        int i = 2;

        if (tokens[1].equalsIgnoreCase("--changed-files")) {
            while (!tokens[i].equalsIgnoreCase("--approvers") && i < tokens.length) {
                updatedFiles.add(tokens[i]);
                i++;
            }
        } else {
            while (!tokens[i].equalsIgnoreCase("--changed-files")) {
                i++;
            }
            i++;
            while (i < tokens.length) {
                updatedFiles.add(tokens[i]);
                i++;
            }
        }
        return updatedFiles;
    }

    /*
        Return set of intial approvers
        Eg. input:
             @tokens = ["validate_approvals","--approvers", "alovelace", "--changed-files", "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
            output:
             @Set = return ("alovelace")
    */
    static Set<String> getInitialApprovers(String[] tokens) {
        Set<String> approvers = new HashSet<>();
        int i = 2;

        if (tokens[1].equalsIgnoreCase("--approvers")) {
            while (!tokens[i].equalsIgnoreCase("--changed-files") && i < tokens.length) {
                approvers.add(tokens[i]);
                i++;
            }
        } else {
            while (!tokens[i].equalsIgnoreCase("--approvers")) {
                i++;
            }
            i++;
            while (i < tokens.length) {
                approvers.add(tokens[i]);
                i++;
            }
        }
        return approvers;
    }

    /*
        Eg. input:
             @tokens = ["validate_approvals","--approvers", "alovelace", "--changed-files", "src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
            output:
             @isValidArgument = return boolean; Return true if command is syntactically correct else false
    */
    static boolean isValidArgumentsList(String[] tokens) {

        if ((tokens[0].equalsIgnoreCase("validate_approvals"))) {
            if (tokens[1].equalsIgnoreCase("--approvers") || tokens[1].equalsIgnoreCase("--changed-files")) {
                if (tokens[1].equalsIgnoreCase("--approvers")) {
                    for (int i = 3; i < tokens.length; i++) {
                        while (i < tokens.length && !tokens[i].equalsIgnoreCase("--changed-files")) {
                            if ((i + 2) >= tokens.length) {
                                return false;
                            }
                            i++;
                        }
                        return true;
                    }
                } else {
                    for (int i = 3; i < tokens.length; i++) {
                        while (i < tokens.length && !tokens[i].equalsIgnoreCase("--approvers")) {
                            if ((i + 2) >= tokens.length) {
                                return false;
                            }
                            i++;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /*
        Eg. input:
             @filesParam = ["src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
            output:
             @isFileExists = return boolean; Check all files are exists or not
    */
    static boolean isFileExists(Set<String> filesParam) {
        for (String file : filesParam) {
            if (!isValidFileName(file)) {
                System.out.println("File does not exists: " + file);
                return false;
            }
        }
        return true;
    }

    /*
        Eg. input:
             @filesParam = ["src/com/twitter/user/User.java", "src/com/twitter/follow/Follow.java"]
            output:
             @isFileExists = return boolean; Check all files are exists or not
    */
    static boolean isValidFileName(String file) {
        String base = Paths.get(".").toAbsolutePath().normalize().toString() + "/" + file;
        File f = new File(base);
        return f.exists();
    }

    // Generate Dependency Graph (input params: list of files, Address of those file)
    static void generateDependancyGraph(File[] files, String base) {
        for (File file : files) {
            if (file.isDirectory()) {
                String relativeAddress = getRelativeAddress(file, base);
                boolean check = new File(relativeAddress, DEPENDENCIES).exists();

                if (check) {
                    //create node and link to that node from dependent node
                    DependencyNode node = new DependencyNode(relativeAddress);
                    Set<String> dependedDirectories = readFile(relativeAddress + "/" + DEPENDENCIES);
                    DependencyNode tempNode;
                    for (String dependedDirectory : dependedDirectories) {
                        if (nodes.containsKey(dependedDirectory)) {
                            tempNode = nodes.get(dependedDirectory);
                        } else {
                            tempNode = new DependencyNode(dependedDirectory);
                        }
                        tempNode.addDependencies(node);
                        nodes.put(tempNode.getDirectory(), tempNode);
                    }
                    nodes.put(node.getDirectory(), node);
                } else {
                    if (!nodes.containsKey(relativeAddress)) {
                        DependencyNode node = new DependencyNode(relativeAddress);
                        nodes.put(node.getDirectory(), node);
                    }
                }
                generateDependancyGraph(file.listFiles(), base); // recursively checking for inner directories.
            }
        }
    }

    /*
        Eg. input:
             @file = "/Users/UserName/Documents/workspace/repo_root/src/com";
             @base = "/Users/UserName/Documents/workspace/repo_root/src";
            output:
             @relativePath = "com"; return relative address based on file address and base address (i.e. Current working directory)
    */
    static String getRelativeAddress(File file, String base) {
        URI baseURI = URI.create(base);
        URI absoluteURI = URI.create(file.toString());
        return baseURI.relativize(absoluteURI).toString();
    }

    /*
        Eg. input:
             @approvers = ("alovelace", "eclarke");
             @updatedDirectories = ("src/com/twitter/user", "src/com/twitter/follow")
            output:
             @isValid = boolean; Check number of Approver entered are enough or not for given collection of updated files/directories.
    */
    static boolean isValid(Set<String> approvers, Set<String> updatedDirectories) {

        Set<String> transitiveDependentDirectories;
        transitiveDependentDirectories = getTransitiveDependentDirectories(updatedDirectories);

        //Check for all approvers
        if (!(transitiveDependentDirectories.isEmpty())) {
            for (String directory : transitiveDependentDirectories) {
                Set<String> requiredApprovers = getApprovers(directory);
                Set<String> intersection = new HashSet<String>(approvers);

                intersection.retainAll(requiredApprovers);
                if (intersection.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /*
        Eg. input:
             @updatedDirectories = ("src/com/twitter/user", "src/com/twitter/follow")
            output:
             @transitiveDependentDirectories = ("src/com/twitter/user", "src/com/twitter/follow", "src/com/twitter/tweet", "src/com/twitter/message")
             return all depended directories for updated files
    */
    static Set<String> getTransitiveDependentDirectories(Set<String> updatedDirectories) {
        Set<String> transitiveDependentDirectories = new HashSet<>();
        if (!updatedDirectories.isEmpty()) {
            for (String directory : updatedDirectories) {
                Set<String> dependentDirectories = getDependencies(directory);
                transitiveDependentDirectories.addAll(dependentDirectories);
            }
        }
        return transitiveDependentDirectories;
    }

    /*
        Eg. input:
             @directory = "src/com/twitter/user"
            output:
             @dependentDirectories = ("src/com/twitter/user", "src/com/twitter/follow", "src/com/twitter/tweet", "src/com/twitter/message")
             return all depended directories for single directory, Traverse through dependency graph (Breadth First Search)
                    and return all dependent directories
    */
    static Set<String> getDependencies(String directory) {

        //BFS to get all dependant directories
        Set<String> dependentDirectories = new HashSet<>();
        ArrayList<String> visited = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(directory);

        while (!queue.isEmpty()) {
            String node = queue.poll();
            dependentDirectories.add(node);

            for (int i = 0; i < nodes.get(directory).getDependencies().size(); i++) {
                String near = nodes.get(directory).getDependencies().get(i).getDirectory();
                if (!visited.contains(near)) {
                    queue.add(near);
                    visited.add(near);
                }
            }

        }
        return dependentDirectories;
    }

    /*
        Eg. input:
             @fileName = "src/com/twitter/user/User.java"
            output:
             @directory = "src/com/twitter/user"
             return parent directory for given directory/file
    */
    static String getParent(String fileName) {
        return new File(fileName).getParent();
    }

    /*
        Eg. input:
             @directory = "src/com/twitter/user/"
            output:
             @set = return ("alovelace", "eclarke");
             Read OWNERS file and return collection of Approvers/Owners
    */
    static Set<String> getApprovers(String directory) {
        // here we need to check for owner in the same directory where "file" is present ie. from OWNER file
        // but in case if OWNER file is missing from current directory go to 1 level up and check there. and
        // return list of approvers for mentioned file

        if (new File(directory, OWNERS).exists()) {
            String filename = directory + '/' + OWNERS;
            return readFile(filename);
        } else {
            File file = new File(directory);
            String parentPath = file.getAbsoluteFile().getParent();
            return getApprovers(parentPath);
        }
    }

    // File_read_Helper
    static Set<String> readFile(String fileName) {
        Set<String> content = new HashSet<>();
        String line;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                content.add(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return content;
    }
}

class DependencyNode {

    String directory;
    ArrayList<DependencyNode> dependencies;

    DependencyNode() {
    }

    DependencyNode(String directory) {
        this.directory = directory;
        this.dependencies = new ArrayList<>();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ArrayList<DependencyNode> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<DependencyNode> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependencies(DependencyNode dependencyNode) {
        this.dependencies.add(dependencyNode);
    }
}
