/**
 * @title   tournamentpetersonlock.TournamentLock
 *
 * @author  Alex Towell <lex@metafunctor.com>
 * @file    TournamentLock.java
 * @since   1.6
 * @date    2/8/2011
 * @course  CS 590-002 Multiprocessor Synchronization
 * @desc    Implementation of an n-thread lock using a binary tree of 2-thread
 *          Peterson locks. The number of threads the lock correctly works with
 *          must be specified upon lock instantiation.
 * 
 * @require Lock.java, Peterson.java, ThreadID.java
 *
 * @precondition
 *          each thread must have a ThreadID in the range of 0 to threads-1,
 *          where threads is an integer passed to the constructor.
 */

package tournamentpetersonlock;

/**
 * TournamentLock: Tournament tree of Peterson locks to provide mutually
 * exclusive access to a critical section for n threads.
 * 
 * Each Peterson lock is a 2-thread lock, so the Tournament tree is a binary
 * tree of such locks which only permits one thread from each of its left and
 * right subtrees to pass to it.
 *
 * @note            the number of threads the lock correctly works with must be
 *                  specified upon construction of the Tournament lock
 *
 * @precondition    each thread using the Tournament tree must have a unique
 *                  thread ID in the range of [0, threads-1]
 *
 * @see Lock
 * @see Peterson
 * @see ThreadID
 */
public class TournamentLock implements Lock
{
    /**
     * Construct a lock for the specified number of threads.
     *
     * @param threads   the number of threads to configure the lock for
     */
    public TournamentLock(int threads)
    {
        this.threads = threads;
        this.locks = new Peterson[threads]; // note: root will be at index 1,
                                            // so locks array size is locks+1

        // instantiate and configure each Peterson lock
        createLocks(1, threads/2, threads/2);
    }

    /**
     * Used by current thread to request a lock on a critical section protected
     * by this Tournament lock.
     *
     * @precondition    thread does not have the lock
     * @postcondition   thread acquired the lock
     */
    public void lock()
    {
        // start at the leaf node lock for current thread
        int index = getLeafLock();

        // root is index 1, so exit the while loop when index 0 (index of
        // unitialized node lock) is reached.
        while (index != 0)
        {
            locks[index].lock();
            index /= 2;
        }
    }

    /**
     * Release a lock on a critical section (releases each of the Peterson
     * locks a thread acquired).
     *
     * @precondition    thread has the lock
     * @postcondition   thread released the lock
     *
     * @see #unlock(int)
     */
    public void unlock()
    {
        unlock(getLeafLock()); // call unlock(int) on leaf node for thread
    }

    /**
     * Private method helper for unlock().
     *
     * @param index
     *      unlock all nodes along the path from the root to the leaf node
     *      corresponding to current thread
     */
    private void unlock(int index)
    {
        if (index != 0)
        {
            unlock(index/2);
            locks[index].unlock(); // post-order: unlock after recursive call
                                   // to unlock from root down to leaf
        }
    }

    /**
     * Private helper function that creates Peterson locks for subtree rooted
     * at specified index.
     *
     * Each Peterson lock is created such that when a thread calls lock() or
     * unlock(), it will be assigned the correct flag index into each of the
     * Peterson nodal locks of the Tournament tree.
     *
     * @precondition    index > 0
     * @postcondition   every lock in the subtree rooted at index will be
     *                  configured correctly
     * 
     * @param index
     *      subtree's rooted index (array-based binary tree) to create locks
     *      for
     * @param lessThan
     *      if a thread arrives at this lock node, any thread ID < lessThan
     *      will be assigned to index 0 in the Peterson lock flag array,
     *      otherwise it will be assigned to index 1
     * @param size
     *      the size of left or right subtree of parent at specified index
     */
    private void createLocks(int index, int lessThan, int size)
    {
        if (index < threads)
        {
            // instantiate Peterson lock at specified node
            locks[index] = new Peterson(lessThan);

            // instantiate the left and right subtrees of this node
            size /= 2;
            createLocks(getLeftChild(index), lessThan - size, size);
            createLocks(getRightChild(index), lessThan + size, size);
        }
    }

    /**
     * Returns the index of the leaf node for current thread.
     *
     * This private helper method will map a ThreadID to the proper leaf
     * node index in the Tournament tree of Peterson locks.
     */
    private int getLeafLock()
    {
        return (threads + ThreadID.get())/2;
    }

    /**
     * Returns the index of the left child node of the parent node at
     * specified index.
     *
     * @param index index of parent node
     * @return int
     *      index of left child node, note that if return value is >= threads
     *      then node at index is a leaf
     */
    private static int getLeftChild(int index)
    {
        return 2*index;
    }

    /**
     * Returns the index of the right child node of the parent node at
     * specified index.
     *
     * @param index index of parent node.
     * @return int
     *      index of right child node, note that if return value is >= threads
     *      then node at index is a leaf
     */
    private static int getRightChild(int index)
    {
        return 2*index+1;
    }

    /**
     * Array of 2-thread Peterson locks.
     *
     * @see Peterson
     */
    private Peterson[] locks;

    /**
     * The number of threads constructed to work with.
     *
     * @see #Tournament(int)
     */
    private int threads;
}
