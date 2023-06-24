import java.util.ArrayDeque;

/**
 * The LevelLargestSum class provides a static method for finding the level with the largest sum in a binary tree.
 */
public class LevelLargestSum {
    /**
     * Finds the level with the largest sum in a binary tree.
     *
     * @param root the root node of the binary tree
     * @return the level with the largest sum in the binary tree, or -1 if the root is null
     */
    public static int getLevelWithLargestSum(BinNode<Integer> root) {
        if (root == null) return -1; // If root is null, return -1 indicating an empty tree

        int maxSum = root.getData();
        int maxLevel = 0;
        int currentLevel = 0;
        int currentSum = root.getData();

        ArrayDeque<BinNode<Integer>> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            int nodesInCurrentLevel = queue.size();
            int nodesInNextLevel = 0;
            currentSum = 0;

            for (int i = 0; i < nodesInCurrentLevel; i++) {
                BinNode<Integer> node = queue.removeFirst();
                currentSum += node.getData();

                if (node.getLeft() != null) {
                    queue.addLast(node.getLeft());
                    nodesInNextLevel++;
                }

                if (node.getRight() != null) {
                    queue.addLast(node.getRight());
                    nodesInNextLevel++;
                }
            }

            if (currentSum > maxSum) {
                maxSum = currentSum;
                maxLevel = currentLevel;
            }

            currentLevel++;
        }

        return maxLevel;
    }
}
