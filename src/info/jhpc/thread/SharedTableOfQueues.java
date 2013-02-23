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

import java.util.Hashtable;

/**
 * Provide a shared table of queues for thread communication. This combines two
 * of the most useful thread communication data structures: directories and
 * queues.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

public class SharedTableOfQueues {
   /**
    * The table used to hold the shared queues.
    */
   Hashtable tbl = new Hashtable();

   /**
    * The FutureFactory to use when creating futures in the contained
    * FutureQueues.
    */
   FutureFactory ff = null;

   /**
    * The default constructor, which uses the default FutureFactory.
    */
   public SharedTableOfQueues() {
      ff = new FutureFactory();
   }

   /**
    * The constructor that uses an explicit FutureFactory.
    * 
    * @param f
    *           The FutureFactory to use.
    */
   public SharedTableOfQueues(FutureFactory f) {
      ff = f;
   }

   /**
    * The constructor that uses an explicit RunQueue in its future factory.
    * 
    * @param rq
    *           The RunQueue to use it its FutureFactory.
    */
   public SharedTableOfQueues(RunQueue rq) {
      ff = new FutureFactory(rq);
   }

   /**
    * Look up the FutureQueue associated with key in tbl, creating one and
    * inserting it into tbl if none is present.
    */
   protected synchronized FutureQueue getQueue(Object key) {
      FutureQueue f;
      f = (FutureQueue) tbl.get(key);
      if (f == null)
         tbl.put(key, f = new FutureQueue());
      return f;
   }

   /**
    * Put an object, <i>value</i>, into the queue with name <i>key</i>. Create a
    * queue with name <i>key</i> if not already present.
    * 
    * @param key
    *           The name of the queue.
    * @param value
    *           The value to put in.
    */

   public void put(Object key, Object value) {
      synchronized (this) {
         FutureQueue q = getQueue(key);
         q.put(value);
         if (q.isVacant())
            tbl.remove(key);
      }
   }

   /**
    * Remove and return an object from the queue named <i>key</i>. If the queue
    * is empty, wait for an object to be put into the queue.
    * 
    * @param key
    *           The name of the queue.
    * @return The object removed from the queue.
    * @throws InterruptedException
    *            If interrupted while waiting.
    */

   public Object get(Object key) throws InterruptedException {
      Future f;
      synchronized (this) {
         FutureQueue q = getQueue(key);
         f = q.get();
         if (q.isVacant())
            tbl.remove(key);
      }
      return f.getValue();
   }

   /**
    * Return a reference to the first object in the queue named <i>key</i>. If
    * the queue is empty, wait for an object to be put into the queue. The
    * object is not removed from the queue.
    * 
    * @param key
    *           The name of the queue.
    * @return The object removed from the queue.
    * @throws InterruptedException
    *            If interrupted while waiting.
    */

   public Object look(Object key) throws InterruptedException {
      Future f;
      synchronized (this) {
         FutureQueue q = getQueue(key);
         f = q.look();
      }
      return f.getValue();
   }

   /**
    * Test whether the FutureQueue associated with <i>key</i> is empty.
    * 
    * @param key
    *           The name of the queue.
    * @return true if the queue is empty (or not present), false otherwise.
    */

   public boolean isEmpty(Object key) {
      synchronized (this) {
         if (tbl.get(key) == null)
            return true;
         FutureQueue q = getQueue(key);
         return q.isEmpty();
      }
   }

   /**
    * Get a Future by calling get()in the FutureQueue. named <i>key</i>.
    * 
    * @param key
    *           The name of the queue.
    * @return The Future from the queue.
    */

   public Object getFuture(Object key) {
      Future f;
      synchronized (this) {
         FutureQueue q = getQueue(key);
         f = q.get();
         if (q.isVacant())
            tbl.remove(key);
      }
      return f;
   }

   /**
    * Return a reference by calling look() in the FutureQueue. named <i>key</i>.
    * 
    * @param key
    *           The name of the queue.
    * @return The Future from the queue.
    */

   public Object lookFuture(Object key) {
      return getQueue(key).look();
   }

   /**
    * Remove and return an object from the queue named <i>key</i>. If the queue
    * is empty, return null immediately.
    * 
    * @param key
    *           The name of the queue.
    * @return The object removed from the queue or null if the queue is empty.
    */

   public Object getSkip(Object key) {
      synchronized (this) {
         FutureQueue q = getQueue(key);
         Object value = q.getSkip();
         if (q.isVacant())
            tbl.remove(key);
         return value;
      }
   }

   /**
    * Return a reference to the first object in the queue named <i>key</i>. If
    * the queue is empty, return null immediately. The object is not removed
    * from the queue.
    * 
    * @param key
    *           The name of the queue.
    * @return The object removed from the queue or null if the queue is empty.
    */

   public Object lookSkip(Object key) {
      synchronized (this) {
         FutureQueue q = getQueue(key);
         return q.lookSkip();
      }
   }

   /**
    * Execute the <i>run()</i> method in Runnable object <i>r</i> in a new
    * thread as soon as the queue named <i>key</i> is non-empty. Method
    * <i>runDelayed()</i> returns immediately.
    * 
    * @param key
    *           The name of the queue.
    * @param r
    *           The Runnable object to run in a new thread.
    */

   public void runDelayed(Object key, Runnable r) {
      Future f = null;
      synchronized (this) {
         FutureQueue q = getQueue(key);
         f = q.look();
      }
      f.runDelayed(r);
   }
}
