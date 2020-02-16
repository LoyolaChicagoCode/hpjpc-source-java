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

package info.jhpc.textbook.concurrency;

/**
 * Implementation of MultipleReadersWritersMonitor that gives preference to
 * readers. A reader may not begin reading if there is a writer writing;
 * otherwise, it may begin immediately. A writer may not begin writing if there
 * are any readers present or if another writer is writing.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

public class ReadersPreferred implements MultipleReadersWritersMonitor {
   /**
    * number readers reading
    */
   protected int nr = 0;
   /**
    * number of readers total (reading or waiting to read)
    */
   protected int nrtotal = 0;
   /**
    * number writers writing, 0 or 1
    */
   protected int nw = 0;
   /**
    * number of writers total (writing or waiting to write)
    */
   protected int nwtotal = 0;

   /**
    * Reset the monitor. You will need to interrupt the threads in the monitor
    * and sleep a while before resetting the monitor.
    */
   public void reset() {
      nr = 0;
      nrtotal = 0;
      nw = 0;
      nwtotal = 0;
   }

   /**
    * Called to begin reading the shared data structure. Will wait for access if
    * necessary.
    * <p/>
    * Pattern for use:
    * <p/>
    * 
    * <pre>
    * 	mon.startReading();
    * 	try {
    * 	   ... read ...
    * 	} finally {
    * 	   mon.stopReading();
    * 	}
    * </pre>
    * 
    * @throws InterruptedException
    *            If interrupted while waiting for access.
    */
   public synchronized void startReading() throws InterruptedException {
      nrtotal++;
      while (nw != 0)
         wait();
      nr++;
   }

   /**
    * Called when the thread is finished reading the shared data structure.
    */
   public synchronized void stopReading() {
      nr--;
      nrtotal--;
      if (nrtotal == 0)
         notify();
   }

   /**
    * Called to begin writing the shared data structure. Will wait for access if
    * necessary.
    * <p/>
    * Pattern for use:
    * <p/>
    * 
    * <pre>
    * 	mon.startWriting();
    * 	try {
    * 	   ... write ...
    * 	} finally {
    * 	   mon.stopWriting();
    * 	}
    * </pre>
    * 
    * @throws InterruptedException
    *            If interrupted while waiting for access.
    */
   public synchronized void startWriting() throws InterruptedException {
      nwtotal++;
      while (nrtotal + nw != 0)
         wait();
      nw = 1;
   }

   /**
    * Called when the thread is finished writing the shared data structure.
    */
   public synchronized void stopWriting() {
      nw = 0;
      nwtotal--;
      notifyAll();
   }

   /**
    * Get legible information about the identity of the monitor.
    * 
    * @return "Readers-Preferred Monitor"
    */
   public String getMonitorInfo() {
      return "Readers-Preferred Monitor";
   }
}
