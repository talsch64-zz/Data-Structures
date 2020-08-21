
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    private int size;
    private HeapNode first; //pointer to the leftest tree in the heap
    private int amountOfTrees;
    private int amountOfMarkedNodes;
    private static int totalLinks;
    private static int totalCuts;


   /**
    * public static int totalLinks()
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
    * in its root.
    */
    public static int totalLinks()
    {
    	return totalLinks;
    }
		
   /**
    * public static int totalCuts()
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
    */
    public static int totalCuts()
    {
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k)
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)).
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        if (H.isEmpty() || k==0) {
            return new int[]{};
        }
        int[] arr = new int[k];
        int counter = 0;
        FibonacciHeap sideHeap = new FibonacciHeap();
        HeapNode currentMin = H.min;
        HeapNode child;
        do {
            arr[counter] = currentMin.key;
            counter++;
            child = currentMin.child;
            for ( int i = 0; i<currentMin.rank;i++) {
                sideHeap.insert(child.key).pointer = child; // insert key to sideHeap, set pointer to original node
                child = child.next;
            }
            currentMin = sideHeap.min.pointer;
            sideHeap.deleteMin();
        }
        while (counter < k);
        return arr;
    }

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    *
    * The method returns true if and only if the heap
    * is empty.
    *
    */
    public boolean isEmpty()
    {
    	if (first == null){
    	    return true;
        }
        return false;
    }

    
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    */
    public HeapNode insert(int key)
    {
        HeapNode newNode = new HeapNode(key);
        if (isEmpty()) {
            min = newNode;
        }
        else {
            if (newNode.key < min.key){
                min = newNode;
            }
            insertAfter(newNode,first);
        }
        first = newNode;
        size++;
        amountOfTrees++;
    	return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin() {
        if (isEmpty()) {
            return;
        }
        HeapNode prevNode = min.prev;
        HeapNode nextNode = min.next;
        if (min.child==null && prevNode == min) {  // the heap has only one node -> empty the heap
            emptyHeap();
            return;
        }
        if (min.child!=null) { // set to null all min's children's parent
            HeapNode node  = min.child;
            do {
                node.parent = null;
                node = node.next;
            }
            while (node != min.child);
        }
        if (prevNode == min) { // min doesn't have siblings, but has children.
            first = min.child;
        }
        else if (min.child==null) { // min has siblings, but no children.
            if (first == min) {
                first = min.next;
            }
            min.prev.next = min.next;
            min.next.prev = min.prev;
        }
        else {  // min has siblings and children.
            if (first == min) {
                first = min.child;
            }
            HeapNode firstChild = min.child;
            HeapNode lastChild = firstChild.prev;
            min.prev.next = firstChild;
            firstChild.prev = min.prev;
            min.next.prev = lastChild;
            lastChild.next = min.next;
        }
        consolidate();
        size--;

    }
    	
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal.
    *
    */
    public HeapNode findMin() {
        if (isEmpty()){
            return null;
        }
    	return min;
    }
	
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2) {
        if (!isEmpty() && !heap2.isEmpty()) { // both heaps aren't empty
            HeapNode lastHeap1 = first.prev;
            HeapNode lastHeap2 = heap2.first.prev;
            lastHeap1.next = heap2.first;
            heap2.first.prev = lastHeap1;
            first.prev = lastHeap2;
            lastHeap2.next = first;
            if (min.key > heap2.min.key) {
                min = heap2.min;
            }
            size += heap2.size;
            amountOfTrees += heap2.amountOfTrees;
            amountOfMarkedNodes += heap2.amountOfMarkedNodes;
        }
        else if (isEmpty() && !heap2.isEmpty()) { // current heap is empty while heap2 is not
            this.cloneHeap(heap2);
        }
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *
    */
    public int size() {
        return size;
    }

    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
    *
    */
    public int[] countersRep()
    {
        int[] counterArray = new int[findMaxRank()+1];
        HeapNode node = first;
        for (int i = 0; i<amountOfTrees;i++) {
            counterArray[node.rank]++;
            node = node.next;
        }
	    return counterArray;
    }

   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
    *
    */
    public void delete(HeapNode x)
    {
    	decreaseKey(x,x.key + 1 + Math.abs(min.key));
    	deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) {
    	x.key -= delta;
    	if (x.key < min.key) {
    	    min = x;
        }
    	if (x.parent != null && x.parent.key > x.key) {
            cascadingCuts(x);
        }
    }

   /**
    * public int potential()
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
    */
    public int potential() {
    	return amountOfTrees + 2 * amountOfMarkedNodes;
    }

    /**
     * private void disconnectPrevNext(HeapNode node)
     *
     * disconnects node from his siblings.
     *
     */
    private void disconnectPrevNext(HeapNode node) {
        if (node.next != node) {
            node.next.prev = node.prev;
            node.prev.next= node.next;
            node.next = node;
            node.prev = node;
        }
    }

    /**
     * private int findMaxRank()
     *
     * The function returns the maximal tree's rank in the heap.
     */
    private int findMaxRank() {
        HeapNode node = first;
        int maxRank = 0;
        for (int i = 0; i<amountOfTrees;i++) {
            if (node.rank > maxRank) {
                maxRank = node.rank;
            }
            node = node.next;
        }
       return maxRank; 
    }

    /**
     * private void cloneHeap(FibonacciHeap heap2)
     *
     * The function updates this heap's fields with heap2's fields.
     *
     */
    private void cloneHeap(FibonacciHeap heap2) {
        min = heap2.min;
        size =  heap2.size;
        first = heap2.first;
        amountOfTrees = heap2.amountOfTrees;
        amountOfMarkedNodes = heap2.amountOfMarkedNodes;
    }

    /**
     * private HeapNode insertAfter (HeapNode node1, HeapNode node2)
     *
     * inset node2's subheap after node1's subheap.
     */
    private HeapNode insertAfter (HeapNode node1, HeapNode node2) {
        node1.prev.next = node2;
        node2.prev.next = node1;
        HeapNode temp = node1.prev;
        node1.prev = node2.prev;
        node2.prev = temp;
        return node1;
    }

    /**
     * private void cut(HeapNode node)
     *
     * cut node from the heap and place it as first.
     */
    private void cut(HeapNode node,HeapNode parent) {
        if (parent.child == node) { // node is direct child of his parent
            if (node.next == node) { // node is only child
                parent.child = null;
            } else {
                parent.child = node.next;
            }
        }
        disconnectPrevNext(node);
        insertAfter(node, first);
        first = node;

        node.parent = null;
        if (node.mark) {
            node.mark = false;
            amountOfMarkedNodes--;
        }
        parent.rank -= 1;
        amountOfTrees++;
        totalCuts++;
    }

    /**
     * private void cascadingCuts (HeapNode node)
     *
     * perform cascading cuts operations as learned in class.
     */
    private void cascadingCuts (HeapNode node) {
        HeapNode parent = node.parent;
        cut(node,parent);
        if (parent.mark) { // cut parent too if needed
            cascadingCuts(parent);
        }
        else if (parent.parent != null) { // mark parent only if parent is not root
                parent.mark = true;
                amountOfMarkedNodes++;
        }

    }

    /**
     * private HeapNode link(HeapNode node1,HeapNode node2)
     *
     * links 2 trees with the same rank.
     */
    private HeapNode link(HeapNode node1,HeapNode node2) {
        node1.prev = node1; // set node1 to be only child
        node1.next = node1;
        node2.prev = node2; // set node2 to be only child
        node2.next = node2;
        HeapNode bigger = node1;
        HeapNode smaller = node2;
        if (node1.key < node2.key) {
            bigger = node2;
            smaller = node1;
        }
        if (smaller.child != null) {
            insertAfter(bigger,smaller.child);
        }
        smaller.child = bigger;
        bigger.parent = smaller;
        smaller.rank++;
        totalLinks++;
        return smaller;
    }

    /**
     * private HeapNode[] toBuckets(HeapNode node)
     *
     * placing trees inside buckets, while linking and transferring to the next bucket.
     */
    private HeapNode[] toBuckets(HeapNode node) {
        HeapNode[] buckets = new HeapNode[(int)Math.ceil(1.5*(Math.log(size)/Math.log(2)))]; //max rank is at most 1.44*logn (2-base)
        node.prev.next=null;
        HeapNode currentNode;
        int currentRank;
        while (node != null) {
             currentNode = node;
             currentRank = currentNode.rank;
             node = node.next;
             currentNode.next = currentNode; // disconnect current root from sibling
             currentNode.prev = currentNode;
             while (buckets[currentRank]!=null) {
                 currentNode = link(currentNode,buckets[currentRank]);
                 buckets[currentRank] = null;
                 currentRank++;
                 amountOfTrees--;
             }
             buckets[currentRank] = currentNode;
        }
        return buckets;
    }

    /**
     * private void consolidate()
     *
     * perform successive linking as we learned in class.
     */
    private void consolidate() {
        HeapNode[] buckets = toBuckets(first);
        first = null;
        min = null;
        amountOfTrees = 0;
        int i = 0;
        while (i < buckets.length) {
            if (buckets[i] != null) {
                if (first == null) {
                    first = buckets[i];
                    first.next = buckets[i];
                    first.prev = buckets[i];
                    min = buckets[i];
                }
                else {
                    first = insertAfter(first, buckets[i]);
                    if (buckets[i].key < min.key) {
                        min = buckets[i];
                    }
                }
                amountOfTrees++;
            }
            i++;
        }
    }

    /**
     * private void emptyHeap()
     *
     * initialize all fields of the heap.
     */
    private void emptyHeap() {
        min = null;
        first = null;
        size = 0;
        amountOfTrees = 0;
        amountOfMarkedNodes = 0;
    }

   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode {

	    private int key;
	    private HeapNode next;
        private HeapNode prev;
        private HeapNode child;
        private HeapNode parent;
        private boolean mark;
        private int rank;
        private  HeapNode pointer;

  	    public HeapNode(int key) {
	    this.key = key;
	    prev = this;
	    next = this;
      }

       public int getKey() {
           return key;
       }

   }

}
