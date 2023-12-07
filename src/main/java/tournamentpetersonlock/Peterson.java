/**
 * Peterson.java
 *
 * Created on January 20, 2006, 10:36 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 *
 * @note    Alex Towell modified this class to accept a single constructor
 *          parameter, int lessThan. It works like the unmodified Peterson lock
 *          if you call the constructor without a parameter.
 */

package tournamentpetersonlock;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Peterson lock
 * @author Maurice Herlihy
 */
class Peterson implements Lock
{
    private AtomicBoolean[] flag;
    private volatile int victim;
    private int lessThan;

    public Peterson()
    {
        this(1);
    }

    public Peterson(int lessThan)
    {
        this.lessThan = lessThan;
        this.flag = new AtomicBoolean[2];

        for(int  i = 0; i < 2; ++i)
        {
            flag[i] = new AtomicBoolean(false);
        }
    }

    public void lock()
    {
        int me = (ThreadID.get() < lessThan ? 0 : 1);
        int other = 1 - me;

        flag[me].set(true);
        victim = me;
        while (flag[other].get() && victim == me) {}; // spin
    }

    public void unlock()
    {
        int me = (ThreadID.get() < lessThan ? 0 : 1);
        flag[me].set(false);
    }
}


