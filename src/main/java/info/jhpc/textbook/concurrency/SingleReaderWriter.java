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
 * Implementation of MultipleReadersWritersMonitor that gives no preference to
 * either readers or writers. This is equivalent to a Semaphore.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

public class SingleReaderWriter implements MultipleReadersWritersMonitor {
   /**
    * Number readers reading and writers writing, 0 or 1.
    */
   protected int n = 0;

   /**
    * Reset the monitor.
    */
   public void reset() {
      n = 0;
   }

   /**
    * Called to begin reading the shared data structure.
    * 
    * @throws InterruptedException
    *            If interrupted while waiting for access.
    */
   public synchronized void startReading() throws InterruptedException {
      while (n != 0)
         wait();
      n = 1;
   }

   /**
    * Called when the thread is finished reading the shared data structure.
    */
   public synchronized void stopReading() {
      n = 0;
      notify();
   }

   /**
    * Called to begin writing the shared data structure.
    * 
    * @throws InterruptedException
    *            If interrupted while waiting for access.
    */
   public synchronized void startWriting() throws InterruptedException {
      while (n != 0)
         wait();
      n = 1;
   }

   /**
    * Called when the thread is finished writing the shared data structure.
    */
   public synchronized void stopWriting() {
      n = 0;
      notify();
   }

   /**
    * Get legible information about the identity of the monitor.
    * 
    * @return "Single Reader Writer Monitor"
    */
   public String getMonitorInfo() {
      return "Single Reader Writer Monitor";
   }
}
