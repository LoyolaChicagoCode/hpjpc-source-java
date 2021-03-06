/*
Copyright (c) 2000, Thomas W. Christopher and George K. Thiruvathukal

Java and High Performance Computing (JHPC) Organzization
Tools of Computing LLC

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

The names Java and High-Performance Computing (JHPC) Organization,
Tools of Computing LLC, and/or the names of its contributors may not
be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This license is based on version 2 of the BSD license. For more
information on Open Source licenses, please visit
http://opensource.org.
 */

package info.jhpc.thread;

/**
 * Runnable objects are placed in this queue to be executed in threads. It has
 * much the same effect as creating a new thread to run the object, but the
 * threads created in a PriorityRunQueue, called "Xeq" threads, can loop to run
 * another object, saving some of the cost of thread creation.
 * <p>
 * A limit may be placed on the maximum number of Xeq threads that can be
 * created at a time to avoid clogging the system with too many threads. This,
 * however, can lead to deadlock if the threads wait on conditions that objects
 * later in the queue will cause.
 *
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

public class PriorityRunQueue {

    /**
     * The number of elements in the heap of Runnable objects to execute.
     */

    protected int N = 0;

    /**
     * The heap of Runnable objects to execute.
     */

    protected Runnable[] runnables = new Runnable[16];

    /**
     * The priorities for the heap of Runnable objects to execute.
     */

    protected float[] priorities = new float[runnables.length];

    /**
     * The number of threads currently waiting for Runnable objects to execute
     * that have not been notified to wake up yet.
     */

    protected int numThreadsWaiting = 0;

    /**
     * The number of threads currently waiting that have been notified to wake up
     * because a Runnable object has been enqueued. numThreadsWaiting +
     * numNotifies = Xeq threads waiting
     */

    protected int numNotifies = 0;

    /**
     * The maximum number of Xeq threads that can be waiting at a time for
     * Runnable objects to execute. The default value is 1.
     */

    protected int maxThreadsWaiting = 1;

    /**
     * The number of Xeq threads currently in existence.
     */

    protected int numThreadsCreated = 0;

    /**
     * Whether the Xeq threads should continue.
     */

    protected boolean goOn = true;

    /**
     * The maximum number of threads that can be created at a time to execute
     * Runnable objects. The default value is 1.
     */

    protected int maxThreadsCreated = 1;

    /**
     * Whether to make the Xeq threads daemons.
     */

    protected volatile boolean makeDaemon = true;

    /**
     * The priority at which Xeq threads run.
     */

    protected int xeqPriority = Thread.NORM_PRIORITY;

    /**
     * The number of milliseconds Xeq wait for something to run before
     * terminating themselves.
     */

    protected long waitTime = 600000; // 10 min.

    /**
     * Create a PriorityRunQueue with the default maximum number of Xeq threads
     * that can be created at a time and a maximum number that can be waiting at
     * any one time for more Runnable objects to execute.
     */

    public PriorityRunQueue() {
    }

    /**
     * Create a PriorityRunQueue with a specified maximum number of Xeq threads
     * that can be created at a time.
     *
     * @param maxCreatable Initial value for maxThreadsCreated.
     */

    public PriorityRunQueue(int maxCreatable) {
        maxThreadsCreated = maxCreatable;
    }

    /**
     * Create a PriorityRunQueue with a specified maximum number of Xeq threads
     * that can be created at a time and a maximum number that can be waiting at
     * any one time for more Runnable objects to execute.
     *
     * @param maxCreatable Initial value for maxThreadsCreated.
     * @param maxWaiting   Initial value for maxThreadsWaiting.
     */

    public PriorityRunQueue(int maxCreatable, int maxWaiting) {
        maxThreadsCreated = maxCreatable;
        maxThreadsWaiting = maxWaiting;
    }

    /**
     * Enqueue an object to be run when a thread becomes available.
     *
     * @param runnable The Runnable object to be enqueued for execution.
     */

    public void put(Runnable runnable, double priority) {
        boolean createThread = false;
        synchronized (this) {
            N++;
            if (N >= runnables.length) {
                Runnable[] newRunnables = new Runnable[2 * runnables.length];
                System.arraycopy(runnables, 0, newRunnables, 0, runnables.length);
                float[] newPriorities = new float[2 * runnables.length];
                System.arraycopy(priorities, 0, newPriorities, 0, priorities.length);
                runnables = newRunnables;
                priorities = newPriorities;
            }
            runnables[N] = runnable;
            priorities[N] = (float) priority;
            siftDown(N);

            if (numThreadsWaiting > 0) {
                numThreadsWaiting--;
                numNotifies++;
                notify();
            } else if (numThreadsCreated < maxThreadsCreated) {
                // was: if (nt==0 && numThreadsCreated<maxThreadsCreated) {
                numThreadsCreated++;
                createThread = true;
            }
        }
        if (createThread) {
            Thread t = new Xeq();
            // System.out.println("new Xeq():"+numThreadsCreated);
            t.setPriority(xeqPriority);
            t.start();
        }
    }

    // ******************************************************************
    protected void siftUp(int i, int n) {
        int j, k;
        for (j = i, k = 2 * j; k <= n; j = k, k = 2 * j) {
            if (k < n && priorities[k] < priorities[k + 1])
                k++;
            if (priorities[j] < priorities[k]) {
                exchange(j, k);
            } else
                break;
        }
    }

    // ******************************************************************
    protected void siftDown(int i) {
        for (int j = i / 2; i > 1; i = j, j = i / 2) {
            if (priorities[j] < priorities[i]) {
                exchange(i, j);
            } else
                break;
            // i=j;
        }
    }

    // ******************************************************************
    protected void exchange(int i, int j) {
        float fltTmp = priorities[i];
        priorities[i] = priorities[j];
        priorities[j] = fltTmp;
        Runnable runTmp = runnables[i];
        runnables[i] = runnables[j];
        runnables[j] = runTmp;
    }

    /**
     * Same as put(runnable, priority).
     *
     * @param runnable The Runnable object to be enqueued for execution.
     */

    public void run(Runnable runnable, double priority) {
        put(runnable, priority);
    }

    // ******************************************************************

    /**
     * Removes and returns a Runnable object to be executed. Called by an Xeq
     * thread.
     * <p>
     * Will wait for an object to run if the limit on waiting threads hasn't been
     * reached. If it has, dequeue will throw an InterruptedException to kill the
     * Xeq thread.
     *
     * @throws InterruptedException To kill the Xeq thread if the limit of waiting threads has been
     *                              reached and there are no objects to run.
     */

    protected synchronized Runnable dequeue() throws InterruptedException {
        Runnable runnable;
        while (N == 0) {
            if (numThreadsWaiting < maxThreadsWaiting) {
                numThreadsWaiting++;
                wait(waitTime);
                if (numNotifies == 0 /* && N==0 */) {
                    numThreadsWaiting--;
                    throw new InterruptedException();
                } else {
                    numNotifies--;
                }
            } else { // terminate
                throw new InterruptedException();
            }
        }
        runnable = runnables[1];
        runnables[1] = runnables[N];
        priorities[1] = priorities[N];
        N--;
        siftUp(1, N);
        return runnable;
    }

    /**
     * Get the limit on the number of threads created to process objects that may
     * be waiting for new objects to process.
     *
     * @return maxThreadsWaiting
     */

    public int getMaxThreadsWaiting() {
        return maxThreadsWaiting;
    }

    /**
     * Set the limit on the number of threads created by this PriorityRunQueue
     * object that may be waiting at any one time to run objects.
     *
     * @param n The new limit.
     */

    public synchronized void setMaxThreadsWaiting(int n) {
        maxThreadsWaiting = n;
        numNotifies += numThreadsWaiting;
        numThreadsWaiting = 0;
        notifyAll();
    }

    /**
     * Get the limit on the number of threads that may be created to process
     * objects.
     *
     * @return maxThreadsCreated
     */

    public int getMaxThreadsCreated() {
        return maxThreadsCreated;
    }

    /**
     * Set the limit on the number of threads that may be created by this
     * PriorityRunQueue object at any one time to run objects.
     *
     * @param n The new limit.
     */

    public void setMaxThreadsCreated(int n) {
        maxThreadsCreated = n;
    }

    /**
     * Get the number of threads that have been created by this PriorityRunQueue
     * to process objects and which are waiting to process more such objects.
     *
     * @return numThreadsWaiting
     */

    public int getNumThreadsWaiting() {
        return numThreadsWaiting;
    }

    /**
     * Get the number of existing threads that have been created by this
     * PriorityRunQueue to process objects.
     *
     * @return numThreadsCreated
     */

    public int getNumThreadsCreated() {
        return numThreadsCreated;
    }

    /**
     * Same as setMaxThreadsWaiting(0). Any waiting user threads would prevent
     * the system from terminating. This does not force the queue to stop running
     * threads.
     */

    public synchronized void terminate() throws InterruptedException {
        goOn = false;
        setMaxThreadsWaiting(0);
        while (numThreadsCreated > 0)
            wait(10);
    }

    /**
     * Get the time limit an Xeq thread is to wait for a Runnable.
     *
     * @return waitTime
     */

    public long getWaitTime() {
        return waitTime;
    }

    /**
     * Set the time limit an Xeq thread is to wait for a Runnable.
     *
     * @param n The new limit.
     */

    public synchronized void setWaitTime(long n) {
        waitTime = n;
        numNotifies += numThreadsWaiting;
        numThreadsWaiting = 0;
        notifyAll();
    }

    /**
     * Get the priority at which the Runnables are to execute.
     *
     * @return priority
     */

    public int getPriority() {
        return xeqPriority;
    }

    /**
     * Set the priority at which the Runnables are to execute.
     *
     * @param n The new priority.
     */

    public void setPriority(int n) {
        xeqPriority = n;
    }

    /**
     * Find out whether the created threads are daemons.
     *
     * @return true if the created threads are daemons.
     */

    public boolean getDaemon() {
        return makeDaemon;
    }

    /**
     * Set whether the created threads will be daemons.
     *
     * @param d True if the created threads are to be daemon threads; false if
     *          user threads.
     */

    public void setDaemon(boolean d) {
        makeDaemon = d;
    }

    /**
     * A thread that will dequeue and run Runnable objects in the
     * PriorityRunQueue.
     */

    protected class Xeq extends Thread {
        public void run() {
            Runnable r;
            try {
                while (goOn) {
                    r = dequeue();
                    r.run();
                }
            } catch (InterruptedException ie) {// nothing
            } catch (Exception e) {
                e.printStackTrace();
            }
            numThreadsCreated--;
        }
    }

}
