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
 * Factory to create Barriers. Barriers allows multiple threads and runnables to
 * gather at a point before proceeding.
 * 
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */
public class BarrierFactory {

   /**
    * The RunQueue for the Barriers created.
    */

   protected RunQueue rq = null;

   /**
    * Create a BarrierFactory.
    * 
    * @param rq
    *           The RunQueue to run any synchronized Runnables in.
    */

   public BarrierFactory(RunQueue rq) {
      super();
      this.rq = rq;
   }

   /**
    * Get the RunQueue for a BarrierFactory object.
    * 
    * @return The RunQueue where runDelayed Runnables will be run.
    */

   public RunQueue getRunQueue() {
      return rq;
   }

   /**
    * Set the RunQueue for a BarrierFactory object.
    */

   public void setRunQueue(RunQueue rq) {
      this.rq = rq;
   }

   /**
    * Create a Barrier.
    */

   public Barrier make(int n) {
      Barrier b = new Barrier(n);
      b.setRunQueue(rq);
      return b;
   }
}