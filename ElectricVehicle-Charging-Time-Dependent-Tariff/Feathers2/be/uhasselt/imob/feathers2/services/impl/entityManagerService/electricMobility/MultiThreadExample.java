package be.uhasselt.imob.feathers2.services.impl.entityManagerService.electricMobility;

/*
FileName: ATEST15.java

The output of this example is as follows:
 Entered the testcase
 Create some worker threads
 Start the thread
 Thread Thread-1: Entered
 Thread Thread-1: Working
 Thread Thread-2: Entered
 Thread Thread-2: Working
 Thread Thread-3: Entered
 Thread Thread-3: Working
 Thread Thread-4: Entered
 Thread Thread-4: Working
 Wait for worker threads to complete
 Thread Thread-5: Entered
 Thread Thread-5: Working
 Thread Thread-1: Done with work
 Thread Thread-2: Done with work
 Thread Thread-3: Done with work
 Thread Thread-4: Done with work
 Thread Thread-5: Done with work
 Check all thread's results
 Testcase completed
 */
import java.lang.*;


public class MultiThreadExample 
{
	public final static int THREADGROUPSIZE   = 5;

	static class theThread extends Thread 
	{
		public final static int THREADPASS     = 0;
		public final static int THREADFAIL     = 1;
		int _status;

		public int status() 
		{
			return _status;
		}
		public theThread() 
		{
			_status = THREADFAIL;
		}
		public void run() 
		{
			System.out.print("Thread " + getName() + ": Entered\n");
			System.out.print("Thread " + getName() + ": Working\n");
			safeSleep(15000, "Thread " + getName() + " work");
			System.out.print("Thread " + getName() + ": Done with work\n");
			_status = THREADPASS;
		}
	}

	public static void main(String argv[]) 
	{
		int i=0;
		theThread thread[] = new theThread[THREADGROUPSIZE];

		System.out.print("Entered the testcase\n");

		System.out.print("Create some worker threads\n");
		System.out.print("Start the thread\n");
		/* We won't use a ThreadGroup for this example, because we'd */
		/* still have to join all the threads individually           */
		for (i=0; i <THREADGROUPSIZE; ++i) 
		{
			thread[i] = new theThread();
			thread[i].start();
		}

		System.out.print("Wait for worker threads to complete\n");
		for (i=0; i <THREADGROUPSIZE; ++i) 
		{
			try {
				thread[i].join();
			}
			catch (InterruptedException e) {
				System.out.print("Join interrupted\n");
			}
		}
		System.out.print("Check all thread's results\n");
		for (i=0; i <THREADGROUPSIZE; ++i) 
		{
			if (thread[i].status() != theThread.THREADPASS) 
			{
				System.out.print("Unexpected thread status\n");
			}
		}

		System.out.print("Testcase completed\n");
		System.exit(0);
	}

	public static void safeSleep(long milliseconds, String s) 
	{
		try {
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			System.out.print(s);
		}
	}

}

