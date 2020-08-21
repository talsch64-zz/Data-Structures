/**     Name: Eynav Sela
        UserName: eynavsela
        ID: 315859512
        ---------------------------------
        Name: Tal Schneider
        UserName: talschneider
        ID: 206897514
 */


import com.sun.net.httpserver.Authenticator;

import java.lang.reflect.Array;
import java.util.*;

/**
 * AVLTree
 * <p>
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 */

public class AVLTree {

    private IAVLNode root;
    private IAVLNode minNode;
    private IAVLNode maxNode;

    public AVLTree() {
    }

    // note that - in this constructor min and max values are not initialized
    private AVLTree(IAVLNode node) {
        this.root = node;
        this.minNode = new AVLNode(Integer.MIN_VALUE, "min");
        this.maxNode = new AVLNode(Integer.MAX_VALUE, "max");
        node.setParent(null);
    }

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        if (this.root == null) {
            return true;
        }
        return false;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        if (this.empty()) {
            return null;
        }
        IAVLNode node = getNode(this.root, k);
        if (node == null) {
            return null;
        }
        return node.getValue();
    }


    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * returns -1 if an item with key k already exists in the tree.
     */
    public int insert(int k, String i) {
        IAVLNode node = new AVLNode(k, i);
        if (this.empty()) {
            this.root = node;
            this.minNode = node;
            this.maxNode = node;
            return 0;
        }
        IAVLNode tempVirtualNode = this.getInsertedNodePosition(this.root, k);          // the virtual node which we want to replace with the new node
        if (tempVirtualNode == null) {                                                  // null - if a node with key k already exists in the tree
            return -1;
        }
//	    -----  parent-child connection -----
        IAVLNode parent = tempVirtualNode.getParent();
        node.setParent(parent);
        if (parent.getKey() > k) {
            parent.setLeft(node);
        } else {
            parent.setRight(node);
        }
//	    ----- tree rebalancing -----
        int result = rebalanceInsert(node);
        if (k < this.minNode.getKey()) {                                             // update min
            this.minNode = node;
        }
        if (k > this.maxNode.getKey()) {                                             // update max
            this.maxNode = node;
        }
        return result;


    }

    /**
     * private IAVLNode getInsertedNodePosition(IAVLNode node,int k)
     * <p>
     * return the virtual node which we want to replace with the new node.
     * return null if the key is already in the tree.
     */
    private IAVLNode getInsertedNodePosition(IAVLNode node, int k) {
        if (node.isRealNode() == false) {
            return node;
        }
        if (node.getKey() == k) {
            return null;
        }
        if (node.getKey() < k) {
            return getInsertedNodePosition(node.getRight(), k);
        }
        return getInsertedNodePosition(node.getLeft(), k);
    }

    /**
     * private void leftRotation(IAVLNode node)
     * rotates left the edge (node,node's parent)
     */
    private void leftRotation(IAVLNode node) {
        IAVLNode parent = node.getParent();
        IAVLNode grandParent = parent.getParent();               // null if parent is the root
        IAVLNode leftChild = node.getLeft();
        parent.setRight(leftChild);
        leftChild.setParent(parent);
        node.setLeft(parent);
        parent.setParent(node);

        if (grandParent != null) {
            node.setParent(grandParent);
            if (grandParent.getLeft() == parent) {
                grandParent.setLeft(node);
            } else {
                grandParent.setRight(node);
            }
        } else {
            this.root = node;
            node.setParent(null);
        }

    }

    /**
     * private void rightRotation(IAVLNode node)
     * rotates right the edge (node,node's parent)
     */
    private void rightRotation(IAVLNode node) {
        IAVLNode parent = node.getParent();
        IAVLNode grandParent = parent.getParent();                  // null if parent is the root
        IAVLNode rightChild = node.getRight();
        parent.setLeft(rightChild);
        rightChild.setParent(parent);
        node.setRight(parent);
        parent.setParent(node);

        if (grandParent != null) {
            node.setParent(grandParent);
            if (grandParent.getLeft() == parent) {
                grandParent.setLeft(node);
            } else {
                grandParent.setRight(node);
            }
        } else {
            this.root = node;
            node.setParent(null);
        }
    }


    /**
     * private char[]  getCaseForInsertRebalancing((IAVLNode node)
     * <p>
     * return array with 2 values-
     * array[0] - '\u0000' if no rebalancing needed
     * '1' if case1
     * '2' if case2
     * '3' if case3
     * '4' if case4 - Join case
     * array[1] - '\u0000' if not relevant
     * 'R' right
     * 'L' left
     */
    private char[] getCaseForInsertRebalancing(IAVLNode node) {
        char[] result = new char[2];
        IAVLNode otherNode;
        IAVLNode parent = node.getParent();
        boolean isLeftChild = parent.getLeft() == node;
        if (isLeftChild) {
            otherNode = parent.getRight();
        } else {
            otherNode = parent.getLeft();
        }
        if ((heightDifferences(parent, otherNode) == 1) && (heightDifferences(parent, node) == 0)) {  //Case1
            result[0] = '1';

        } else if (heightDifferences(parent, otherNode) == 2) {
            if (isLeftChild) {
                if (heightDifferences(node, node.getLeft()) == 1 && heightDifferences(node, node.getRight()) == 2) {  //Case2
                    result[0] = '2';
                } else if (heightDifferences(node, node.getLeft()) == 2 && heightDifferences(node, node.getRight()) == 1) {  //Case3

                    result[0] = '3';
                } else {  //Case4 - Join Case
                    result[0] = '4';
                }
                result[1] = 'L';  //left
            } else {
                if (heightDifferences(node, node.getRight()) == 1 && heightDifferences(node, node.getLeft()) == 2) {  //Case2
                    result[0] = '2';
                } else if (heightDifferences(node, node.getRight()) == 2 && heightDifferences(node, node.getLeft()) == 1) {  //Case3
                    result[0] = '3';
                } else {  //Case4 - Join Case
                    result[0] = '4';
                }
                result[1] = 'R';  //right
            }
        }
        return result;

    }


    /**
     * private int rebalanceInsert(IAVLNode node)
     * <p>
     * rebalancing the tree after insertion and updates its height and size.
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     */
    private int rebalanceInsert(IAVLNode node) {
        if (node.getParent() == null) {
            return 0;                                                                  // node is the root
        }

        char[] rebalanceCase = getCaseForInsertRebalancing(node);
        IAVLNode parent = node.getParent();

        // ------ Case1 ------
        if (rebalanceCase[0] == '1') {
            parent.setHeight(parent.getHeight() + 1);
            parent.setSize(parent.getSize() + 1);
            int recResult = rebalanceInsert(parent);
            return 1 + recResult;                                                     // 1 promote + ...
        }
        // ------ Case2 left ------
        if ((rebalanceCase[0] == '2') && (rebalanceCase[1] == 'L')) {
            parent.setHeight(parent.getHeight() - 1);
            parent.setSize(parent.getSize() + 1);                                     // increase parent's size by one due to insertion
            node.setSize(parent.getSize());                                           // node's size after rotation = parent's size
            parent.setSize(parent.getSize() - node.getLeft().getSize() - 1);          // parent's size after rotation = parent's size - node.left.size - 1
            rightRotation(node);
            fixAncestorsSize(node,1);
            return 2;                                                                 // rotate + 1 demote
        }
        // ------ Case2 right ------
        if ((rebalanceCase[0] == '2') && (rebalanceCase[1] == 'R')) {
            parent.setHeight(parent.getHeight() - 1);
            parent.setSize(parent.getSize() + 1);                                     // increase parent's size by one due to insertion
            node.setSize(parent.getSize());                                           // node's size after rotation = parent's size
            parent.setSize(parent.getSize() - node.getRight().getSize() - 1);         // parent's size after rotation = parent's size - node.right.size - 1
            leftRotation(node);
            fixAncestorsSize(node,1);
            return 2;                                                                 // rotate + 1 demote
        }
        // ------ Case3 left ------
        if ((rebalanceCase[0] == '3') && (rebalanceCase[1] == 'L')) {
            IAVLNode rightChild = node.getRight();
            rightChild.setHeight(rightChild.getHeight() + 1);
            node.setHeight(node.getHeight() - 1);
            parent.setSize(parent.getSize() + 1);                                     // increase parent's size by one due to insertion
            parent.setHeight(parent.getHeight() - 1);
            node.setSize(node.getSize() - rightChild.getRight().getSize() - 1);       // node's size after double rotation = node's size - node.right.right.size - 1
            rightChild.setSize(parent.getSize());                                     // node.right's size after double rotation = parent's size
            parent.setSize(parent.getSize() - node.getSize() - 1);                    // parent's size after double rotation = parent's size - updated node.size -1
            leftRotation(rightChild);
            rightRotation(rightChild);
            fixAncestorsSize(rightChild,1);                                   // right child is the new subtree root
            return 5;                                                                  // 2 demote + 1 promote + double rotation

        }
        // ------ Case3 right ------
        if ((rebalanceCase[0] == '3') && (rebalanceCase[1] == 'R')) {
            IAVLNode leftChild = node.getLeft();
            leftChild.setHeight(leftChild.getHeight() + 1);
            node.setHeight(node.getHeight() - 1);
            parent.setHeight(parent.getHeight() - 1);
            parent.setSize(parent.getSize() + 1);                                         // increase parent's size by one due to insertion
            node.setSize(node.getSize() - leftChild.getLeft().getSize() - 1);             // node's size after double rotation = node's size - node.left.left.size - 1
            leftChild.setSize(parent.getSize());                                          // node.left's size after double rotation = parent's size
            parent.setSize(parent.getSize() - node.getSize() - 1);                        // parent's size after double rotation = parent's size - updated node.size -1
            rightRotation(leftChild);
            leftRotation(leftChild);
            fixAncestorsSize(leftChild,1);                                        // left child is the new subtree root
            return 5;                                                                     // 2 demote + 1 promote + double rotation
        }
        //------ Case4 left ------
        if ((rebalanceCase[0] == '4') && (rebalanceCase[1] == 'L')) {
			node.setSize(parent.getSize()+1);
			node.setHeight(node.getHeight()+1);
			parent.setSize(parent.getRight().getSize()+node.getRight().getSize()+1);
			rightRotation(node);
			return 2 + rebalanceInsert(node);
		}
		//------ Case4 right ------
		if ((rebalanceCase[0] == '4') && (rebalanceCase[1] == 'R')) {
			node.setSize(parent.getSize()+1);
			node.setHeight(node.getHeight()+1);
			parent.setSize(parent.getLeft().getSize()+node.getLeft().getSize()+1);
			leftRotation(node);
			return 2 + rebalanceInsert(node);
		}
			fixAncestorsSize(node,1);
        return 0;
    }


    /**
     * private int heightDifferences (IAVLNode node1,IVALNode node2)
     * <p>
     * return (node1's height - node2's height)
     */
    private int heightDifferences(IAVLNode node1, IAVLNode node2) {
        return Math.abs(node1.getHeight() - node2.getHeight());
    }


    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        if (this.empty()) {
            return -1;
        }
        IAVLNode deletedNode = getNode(this.root, k);
        IAVLNode node;
        IAVLNode successor;
        if (deletedNode == null) {
            return -1;
        }
        IAVLNode parent = deletedNode.getParent();
        boolean isRoot = deletedNode == this.root;
        boolean isLeftChild = false;
        if (k == this.minNode.getKey()) {                                    // update min
            if (this.root.getSize() > 1) {
                this.minNode = getSuccessor(this.minNode);
            }
        }
        if (k == this.maxNode.getKey()) {                                    // update max
            if (this.root.getSize() > 1) {
                this.maxNode = getPredecessor(this.maxNode);
            }
        }
        if (!isRoot) {
            isLeftChild = parent.getLeft().getKey() == k;
        }
        if (deletedNode.getHeight() == 0) {                                  // deleted node is a leaf. connect between parent and deleted node's left virtual child;
            if (isRoot) {                                                    // tree has only one node. delete -> empty.
                this.root = null;
                return 0;
            }
            if (isLeftChild) {
                parent.setLeft(deletedNode.getLeft());
            } else {
                parent.setRight(deletedNode.getLeft());
            }
            deletedNode.getLeft().setParent(parent);
            node = deletedNode.getLeft();
        } else if (!deletedNode.getLeft().isRealNode()) {                     // deleted node is unary - Right
            if (isRoot) {
                this.root = deletedNode.getRight();
                deletedNode.getRight().setParent(null);
                return 0;
            }
            if (isLeftChild) {
                parent.setLeft(deletedNode.getRight());
            } else {
                parent.setRight(deletedNode.getRight());
            }
            deletedNode.getRight().setParent(parent);
            node = deletedNode.getRight();
        } else if (!deletedNode.getRight().isRealNode()) {                     // deleted node is unary - left
            if (isRoot) {
                this.root = deletedNode.getLeft();
                deletedNode.getLeft().setParent(null);
                return 0;
            }
            if (isLeftChild) {
                parent.setLeft(deletedNode.getLeft());
            } else {
                parent.setRight(deletedNode.getLeft());
            }
            deletedNode.getLeft().setParent(parent);
            node = deletedNode.getLeft();
        } else {                                                                  // deleted node has 2 children
            successor = getMinNodeInSubtree(deletedNode.getRight());
            node = successor.getRight();
            if (successor == deletedNode.getRight()) {
                successor.setLeft(deletedNode.getLeft());
                deletedNode.getLeft().setParent(successor);
            } else {
                successor.getParent().setLeft(successor.getRight());
                successor.getRight().setParent(successor.getParent());
                successor.setLeft(deletedNode.getLeft());
                successor.setRight(deletedNode.getRight());
                deletedNode.getLeft().setParent(successor);
                deletedNode.getRight().setParent(successor);
            }
            successor.setParent(deletedNode.getParent());
            if (isRoot) {
                this.root = successor;
            } else {
                if (isLeftChild) {
                    deletedNode.getParent().setLeft(successor);
                } else {
                    deletedNode.getParent().setRight(successor);
                }
            }

            successor.setSize(deletedNode.getSize());
            successor.setHeight(deletedNode.getHeight());

        }
        return rebalanceDelete(node);

    }

    /**
     * private IAVLNode getNode (IAVLNode node, int k)
     * <p>
     * returns the node of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    private IAVLNode getNode(IAVLNode node, int k) {
        if (!node.isRealNode()) {
            return null;
        }
        if (node.getKey() == k) {
            return node;
        } else if (node.getKey() > k) {
            return getNode(node.getLeft(), k);
        }
        return getNode(node.getRight(), k);
    }

    /**
     * private char[] getCaseForDeleteRebalancing((IAVLNode node)
     * <p>
     * return array with 2 values-
     * array[0] - '\u0000' if no rebalancing needed
     * '1' if case1
     * '2' if case2
     * '3' if case3
     * '4' if case4
     * array[1] - '\u0000' if not relevant
     * 'R' right
     * 'L' left
     */
    private char[] getCaseForDeleteRebalancing(IAVLNode node) {
        char[] result = new char[2];
        IAVLNode otherNode;
        IAVLNode parent = node.getParent();
        if (parent == null) {                                         // if node is root - done.
            return result;
        }
        boolean isLeftChild = parent.getLeft() == node;
        if (isLeftChild) {
            otherNode = parent.getRight();
        } else {
            otherNode = parent.getLeft();
        }
        if ((heightDifferences(parent, otherNode) == 2) && (heightDifferences(parent, node) == 2)) {  //Case1
            result[0] = '1';

        } else if (heightDifferences(parent, node) == 3) {
            if (isLeftChild) {
                if (heightDifferences(otherNode.getRight(), otherNode.getLeft()) == 0) {  //Case2
                    result[0] = '2';
                } else if (heightDifferences(otherNode, otherNode.getLeft()) == 2) {  //Case3
                    result[0] = '3';
                } else {  //Case4
                    result[0] = '4';
                }
                result[1] = 'L';  //left
            } else {
                if (heightDifferences(otherNode.getRight(), otherNode.getLeft()) == 0) {  //Case2
                    result[0] = '2';
                } else if (heightDifferences(otherNode, otherNode.getRight()) == 2) {  //Case3
                    result[0] = '3';
                } else {  //Case4
                    result[0] = '4';
                }
                result[1] = 'R';  //right
            }
        }
        return result;
    }

    /**
     * private IAVLNode getSuccessor(IAVLNode node)
     * return the node's successor.
     * if node has right subtree - reutrn the minumum node of this subtree.
     * else search for an ancestor's node which has node as a left descendant, return ancestor.
     * if not found return null.
     */
    private IAVLNode getSuccessor(IAVLNode node) {
        IAVLNode rightChild = node.getRight();
        IAVLNode parent = node.getParent();
        if (rightChild.isRealNode()) {
            return getMinNodeInSubtree(rightChild);
        }
        while ((parent != null) && (parent.getRight() == node)) {
            node = parent;
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * private IAVLNode getPredecessor(IAVLNode node)
     * return the node's predecessor.
     * if node has left subtree - return the maximum node of this subtree.
     * else search for an ancestor's node which has node as a right descendant, return ancestor.
     * if not found return null.
     */
    private IAVLNode getPredecessor(IAVLNode node) {
        IAVLNode leftChild = node.getLeft();
        IAVLNode parent = node.getParent();
        if (leftChild.isRealNode()) {
            return getMaxNodeInSubtree(leftChild);
        }
        while ((parent != null) && (parent.getLeft() == node)) {
            node = parent;
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * private IAVLNode getMinNodeInSubtree(IAVLNode node)
     * <p>
     * Returns the node with the smallest key in the subtree,
     */
    private IAVLNode getMinNodeInSubtree(IAVLNode node) {
        while (node.getLeft().isRealNode()) {
            node = node.getLeft();
        }
        return node;
    }

    /**
     * private IAVLNode getMaxNodeInSubtree(IAVLNode node)
     * <p>
     * Returns the node with the largest key in the subtree,
     */
    private IAVLNode getMaxNodeInSubtree(IAVLNode node) {
        while (node.getRight().isRealNode()) {
            node = node.getRight();
        }
        return node;
    }

    /**
     * private int rebalanceDelete (IAVLNode node)
     * <p>
     * rebalancing the tree ofter deletion and updates height and size accordingly.
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     */
    private int rebalanceDelete(IAVLNode node) {
        if (node == null) {
            return 0;
        }
        IAVLNode parent = node.getParent();
        IAVLNode otherNode;
        char[] rebalanceCase = getCaseForDeleteRebalancing(node);
//		----------- Case1 -----------
        if (rebalanceCase[0] == '1') {
            parent.setHeight(parent.getHeight() - 1);
            parent.setSize(parent.getSize() - 1);
            return 1 + rebalanceDelete(parent);                                                     // 1 demote + ...
        }
//		----------- Case2 -----------
        else if (rebalanceCase[0] == '2') {
            parent.setHeight(parent.getHeight() - 1);
            parent.setSize(parent.getSize() - 1);                                                     // decrease parent's size by one due to deletion

            if ((rebalanceCase[1] == 'L')) {
                otherNode = parent.getRight();
                otherNode.setHeight(otherNode.getHeight() + 1);
                otherNode.setSize(parent.getSize());                                                // otherNode's size after rotation = parent's size before rotation
                parent.setSize(node.getSize() + otherNode.getLeft().getSize() + 1);                 // parent's size after rotation = (left: node's subtree size) + (root: 1) + (right: otherNode's left subtree size)
                leftRotation(otherNode);
            } else {
                otherNode = parent.getLeft();
                otherNode.setHeight(otherNode.getHeight() + 1);
                otherNode.setSize(parent.getSize());                                                // otherNode's size after rotation = parent's size before rotation
                parent.setSize(node.getSize() + otherNode.getRight().getSize() + 1);                // parent's size after rotation = (left: otherNode's right subtree size ) + (root: 1) + (right: node's subtree size)
                rightRotation(otherNode);
            }
            fixAncestorsSize(otherNode,-1);
            return 3;                                                                                // 1 demote + 1 promote + 1 rotate
        }

//		----------- Case 3 -----------
        else if (rebalanceCase[0] == '3') {
            parent.setHeight(parent.getHeight() - 2);
            parent.setSize(parent.getSize() - 1);
            if (rebalanceCase[1] == 'L') {
                otherNode = parent.getRight();
                otherNode.setSize(parent.getSize());                                                // otherNode's size after rotation = parent's size before rotation
                parent.setSize(node.getSize() + otherNode.getLeft().getSize() + 1);                 // parent's size after rotation = (left: node's subtree size) + (root: 1) + (right: otherNode's left subtree size)
                leftRotation(otherNode);
            } else {
                otherNode = parent.getLeft();
                otherNode.setSize(parent.getSize());                                                // otherNode's size after rotation = parent's size before rotation
                parent.setSize(node.getSize() + otherNode.getRight().getSize() + 1);                // parent's size after rotation = (left:otherNode's right subtree size) + (root: 1) + (right: node's subtree size)
                rightRotation(otherNode);
            }

            return 3 + rebalanceDelete(otherNode);                                                   // 2 demote + 1 rotate + ...
        }
//		----------- Case 4 -----------
        else if (rebalanceCase[0] == '4') {
            IAVLNode otherNodeChild;
            parent.setSize(parent.getSize() - 1);
            if (rebalanceCase[1] == 'L') {
                otherNode = parent.getRight();
                otherNodeChild = otherNode.getLeft();

                otherNodeChild.setSize(parent.getSize());                                                   // otherNodeChild's size after rotation = parent's size before rotation
                otherNode.setSize(otherNode.getRight().getSize() + otherNodeChild.getRight().getSize() + 1);    // otherNode's size = (left: otherNodeChild's right subtree)) + (root: 1) + (right: otherNode's right subtree)
                parent.setSize(node.getSize() + otherNodeChild.getLeft().getSize() + 1);                    // parent's size = (left: node's subtree) + (root: 1) + (right: otherNodeChild's left subtree)

                rightRotation(otherNodeChild);
                leftRotation(otherNodeChild);
            } else {
                otherNode = parent.getLeft();
                otherNodeChild = otherNode.getRight();

                otherNodeChild.setSize(parent.getSize());                                                   // otherNodeChild's size after rotation = parent's size before rotation
                otherNode.setSize(otherNode.getLeft().getSize() + otherNodeChild.getLeft().getSize() + 1);     // otherNode's size = (left:otherNode's left subtree) + (root: 1) + (right:  otherNodeChild's left subtree)
                parent.setSize(node.getSize() + otherNodeChild.getRight().getSize() + 1);                  // parent's size = (left: otherNodeChild's left subtree) + (root:1) + (right: node's right subtree)

                leftRotation(otherNodeChild);
                rightRotation(otherNodeChild);

            }
            parent.setHeight(parent.getHeight() - 2);
            otherNode.setHeight(otherNode.getHeight() - 1);
            otherNodeChild.setHeight(otherNodeChild.getHeight() + 1);
            return 6 + rebalanceDelete(otherNodeChild);                                                  // 3 demote + 1 promote  + 2 rotate + ...
        }
        fixAncestorsSize(node,-1);
        return 0;

    }



    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */
    public String min() {
        if (this.empty()) {
            return null;
        }
        return this.minNode.getValue();
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */
    public String max() {
        if (this.empty()) {
            return null;
        }
        return this.maxNode.getValue();
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */
    public int[] keysToArray() {
        if (empty()) {
            return new int[]{};
        }
        int[] arr = new int[this.root.getSize()];
        recKeysToArray(arr, this.root, 0);
        return arr;
    }

    /**
     * public int recKeysToArray()
     * <p>
     * executes in-order insertion to a given array.
     * returns number of insertions so far.
     */
    private int recKeysToArray(int[] arr, IAVLNode node, int index) {
        if (node.getHeight() == 0) {
            arr[index] = node.getKey();
            return index + 1;
        }
        if (node.getLeft().isRealNode()) {
            index = recKeysToArray(arr, node.getLeft(), index);
        }
        arr[index] = node.getKey();
        index++;
        if (node.getRight().isRealNode()) {
            index = recKeysToArray(arr, node.getRight(), index);
        }
        return index;
    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */
    public String[] infoToArray() {
        if (empty()) {
            return new String[]{};
        }
        String[] arr = new String[this.root.getSize()];
        recInfoToArray(arr, this.root, 0);
        return arr;
    }

    /**
     * public int recInfoToArray()
     * <p>
     * executes in-order insertion to a given array.
     * returns number of insertions so far.
     */
    private int recInfoToArray(String[] arr, IAVLNode node, int index) {
        if (node.getHeight() == 0) {
            arr[index] = node.getValue();
            return index + 1;
        }
        if (node.getLeft().isRealNode()) {
            index = recInfoToArray(arr, node.getLeft(), index);
        }
        arr[index] = node.getValue();
        index++;
        if (node.getRight().isRealNode()) {
            index = recInfoToArray(arr, node.getRight(), index);
        }
        return index;
    }


    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     * <p>
     * precondition: none
     * postcondition: none
     */
    public int size() {
        if (this.empty()) {
            return 0;
        }
        return this.root.getSize();
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root AVL node, or null if the tree is empty
     * <p>
     * precondition: none
     * postcondition: none
     */
    public IAVLNode getRoot() {
        if (this.empty()) {
            return null;
        }
        return this.root;
    }


    /**
     * public string split(int x)
     * <p>
     * splits the tree into 2 trees according to the key x.
     * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
     * precondition: search(x) != null
     * postcondition: none
     */
    public AVLTree[] split(int x) {
        IAVLNode node = getNode(this.root, x);
//	------------- min/max for final t1 and t2 -------------
        IAVLNode t1min;
        IAVLNode t1max;
        IAVLNode t2min;
        IAVLNode t2max;
        if (node == this.maxNode) { //if node is max, t2 will be empty tree
            t2min = null;
            t2max = null;
            t1min = this.minNode;
            t1max = getPredecessor(this.maxNode);
        }
        else if (node == this.minNode) { //if node is min, t1 will be empty tree
            t1min = null;
            t1max = null;
            t2min = getSuccessor(this.minNode);
            t2max = this.maxNode;
        }
        else {
            t1min = this.minNode;
            t1max = getPredecessor(node);
            t2min = getSuccessor(node);
            t2max = this.maxNode;
        }

//  -------------------------------------------------------
		IAVLNode leftChild = node.getLeft();
		IAVLNode rightChild = node.getRight();
		IAVLNode parent = node.getParent();
		IAVLNode grandParent;

		AVLTree t1 = new AVLTree();
        AVLTree t2 = new AVLTree();
        if (leftChild.isRealNode()) {
            t1 = new AVLTree(leftChild);
        }
        if (rightChild.isRealNode()) {
            t2 = new AVLTree(rightChild);
        }

        while (parent != null) {
            grandParent = parent.getParent();
            if (parent.getRight() == node) {
                if (parent.getLeft().isRealNode()) {
                    t1.join(parent, new AVLTree(parent.getLeft()));

                } else {
                    t1.join(parent, new AVLTree());
                }

            } else {
                if (parent.getRight().isRealNode()) {
                    t2.join(parent, new AVLTree(parent.getRight()));
                }
                else {
                    t2.join(parent, new AVLTree());
                }
            }
			node = parent;
            parent = grandParent;
        }
        t1.minNode = t1min;
        t1.maxNode = t1max;
        t2.minNode = t2min;
        t2.maxNode = t2max;
        return new AVLTree[]{t1, t2};


    }

    /**
     * public join(IAVLNode x, AVLTree t)
     * <p>
     * joins t and x with the tree.
     * Returns the complexity of the operation (rank difference between the tree and t + 1)
     * precondition: keys(x,t) < keys() or keys(x,t) > keys()
     * postcondition: none
     */
    public int join(IAVLNode x, AVLTree t) {
        AVLTree highTree;
        AVLTree lowTree;
        int complexity;
        if ((this.empty()) && (t.empty())) {               // this and t are empty trees - insert x to empty this tree
            this.insert(x.getKey(), x.getValue());
            return 1;
        }
        if (this.empty()) {                                // this is empty - insert x to t and set this to t
            complexity = t.root.getHeight() + 2;
            t.insert(x.getKey(), x.getValue());
            this.root = t.root;
            this.minNode = t.minNode;
            this.maxNode = t.maxNode;
            return complexity;
        }
        if (t.empty()) {                                   // t is empty - insert x to this
            complexity = this.root.getHeight() + 2;
            this.insert(x.getKey(), x.getValue());
            return complexity;
        }
        if (this.root.getHeight() > t.root.getHeight()) {
            highTree = this;
            lowTree = t;
        } else {
            highTree = t;
            lowTree = this;
        }
        complexity = heightDifferences(highTree.root, lowTree.root) + 1;
        IAVLNode currentNode = highTree.root;

//     ----------------- highTree's keys > x ---------------------
        if (highTree.root.getKey() > x.getKey()) {
            while (currentNode.getHeight() > lowTree.root.getHeight()) {
                currentNode = currentNode.getLeft();
            }
            if (currentNode.getParent() != null) {               // x will we be new the root
                currentNode.getParent().setLeft(x);
            } else {
                highTree.root = x;
            }
            x.setParent(currentNode.getParent());
            x.setRight(currentNode);
            x.setLeft(lowTree.getRoot());

            this.minNode = lowTree.minNode;
            this.maxNode = highTree.maxNode;
        }

//     ----------------- lowTree's keys > x ---------------------
        else {
            while (currentNode.getHeight() > lowTree.root.getHeight()) {
                currentNode = currentNode.getRight();
            }
            if (currentNode.getParent() != null) {               // x will we be new the root
                currentNode.getParent().setRight(x);
            } else {
                highTree.root = x;
            }
            x.setParent(currentNode.getParent());
            x.setLeft(currentNode);
            x.setRight(lowTree.getRoot());

            this.minNode = highTree.minNode;
            this.maxNode = lowTree.maxNode;
        }

        lowTree.root.setParent(x);
        currentNode.setParent(x);
        x.setHeight(lowTree.root.getHeight() + 1);
        x.setSize(lowTree.root.getSize() + currentNode.getSize() + 1);
        fixAncestorsSize(x, lowTree.root.getSize());                // increase size of all x's ancestors by lowTree's size before rebalancing.
        this.root = highTree.root;
        rebalanceInsert(x);
        return complexity;
    }

    /**
     * private void fixAncestorsSize(IAVLNode node,int addition)
     * <p>
     * increase/decrease size of all node's ancestors by addition.
     * note that node's size and his descendants' size are updated.
     */
    private void fixAncestorsSize(IAVLNode node, int addition) {
        IAVLNode parent = node.getParent();
        if (parent != null) {
            parent.setSize(parent.getSize() + addition);
            fixAncestorsSize(parent, addition);
        }
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IAVLNode {
        public int getKey(); //returns node's key (for virtuval node return -1)

        public String getValue(); //returns node's value [info] (for virtuval node return null)

        public void setLeft(IAVLNode node); //sets left child

        public IAVLNode getLeft(); //returns left child (if there is no left child return null)

        public void setRight(IAVLNode node); //sets right child

        public IAVLNode getRight(); //returns right child (if there is no right child return null)

        public void setParent(IAVLNode node); //sets parent

        public IAVLNode getParent(); //returns the parent (if there is no parent return null)

        public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

        public void setHeight(int height); // sets the height of the node

        public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

        public void setSize(int size); // sets the size of the node

        public int getSize(); // Returns the size of the node (-1 for virtual nodes)


    }

    /**
     * public class AVLNode
     * If you wish to implement classes other than AVLTree
     * (for example AVLNode), do it in this file, not in
     * another file.
     * This class can and must be modified.
     * (It must implement IAVLNode)
     */
    public class AVLNode implements IAVLNode {
        private IAVLNode left;
        private IAVLNode right;
        private IAVLNode parent;
        private Boolean isReal;
        private int height;
        private int size;
        private String value;
        private int key;

        //constructor for v Non-virtual node
        public AVLNode(int key, String value) {
            this.key = key;
            this.value = value;
            this.isReal = true;
            this.left = new AVLNode();
            this.left.setParent(this);
            this.right = new AVLNode();
            this.right.setParent(this);
            this.height = 0;
            this.size = 1;
        }

        //constructor for virtual node
        public AVLNode() {
            this.isReal = false;
            this.height = -1;
            this.size = 0;
        }

        public int getKey() {
            if (isReal == true) {
                return this.key;
            }
            return -1;
        }

        public String getValue() {
            return this.value;
        }

        public void setLeft(IAVLNode node) {
            this.left = node;
        }

        public IAVLNode getLeft() {
            return this.left;
        }

        public void setRight(IAVLNode node) {
            this.right = node;
        }

        public IAVLNode getRight() {
            return this.right;
        }

        public void setParent(IAVLNode node) {
            this.parent = node;
        }

        public IAVLNode getParent() {
            return this.parent;
        }

        // Returns True if this is a non-virtual AVL node
        public boolean isRealNode() {
            return this.isReal;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return this.size;
        }


    }

    public static void main(String[] args) {
    }
}





  

