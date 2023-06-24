/**
 * The PathFromRoot class provides a static method for checking if a path exists from the root of a binary tree
 * that matches a given string.
 */
public class PathFromRoot {
    /**
     * Checks if a path exists from the root of a binary tree that matches a given string.
     *
     * @param root the root node of the binary tree
     * @param str the string to be matched along the path from the root
     * @return true if a path exists from the root that matches the given string, false otherwise
     */
    public static boolean doesPathExist(BinNode<Character> root, String str) {
        if(str.length() == 0) return true; //// Reached the end of the string, path exists
        if (root.getData().equals(str.charAt(0))) {
            // Check if the current node's data matches the next character in the string
            // Recursively check for a matching path in the left and right subtrees
            return doesPathExist(root.getLeft(), str.substring(1)) || doesPathExist(root.getRight(), str.substring(1));
        }
        // Current node's data doesn't match the next character in the string
        return false;
    }
}
