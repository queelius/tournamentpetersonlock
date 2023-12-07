package tournamentpetersonlock;

/*
 * TournamentLockTest.java
 * JUnit based test
 */

import junit.framework.*;

/**
 * Crude & inadequate test of lock class.
 */
public class TournamentLockTest extends TestCase
{
  private final static int THREADS = 4;
  private final static int COUNT = 1024;
  private final static int PER_THREAD = COUNT / THREADS;
  Thread[] thread = new Thread[THREADS];
  int counter = 0;

  Lock instance = new TournamentLock(THREADS);

  public TournamentLockTest(String testName)
  {
    super(testName);
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite(TournamentLockTest.class);
    return suite;
  }

  public void testParallel() throws Exception
  {
    System.out.println("parallel TournamentLock");
    ThreadID.reset();
    for (int i = 0; i < THREADS; i++)
    {
      thread[i] = new MyThread();
    }
    for (int i = 0; i < THREADS; i++)
    {
      thread[i].start();
    }
    for (int i = 0; i < THREADS; i++)
    {
      thread[i].join();
    }
    assertEquals(COUNT, counter);
  }

  class MyThread extends Thread
  {
    public void run()
    {
      for (int i = 0; i < PER_THREAD; i++)
      {
        instance.lock();
        try
        {
          counter = counter + 1;
        }
        finally { instance.unlock();  }
      }
    }
  }
}
