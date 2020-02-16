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

package info.jhpc.memo;

import info.jhpc.gmi.RemoteCallClient;

import java.io.Serializable;

public class MemoClient {

   RemoteCallClient rc;
   String target;
   String host;
   int port;

   public MemoClient(String host, int port, String target) throws Exception {
      this.host = host;
      this.port = port;
      this.rc = new RemoteCallClient(host, port);
      this.target = target;
   }

   public void goodbye() {
      try {
         rc.disconnect();
      } catch (Exception e) {
         System.err.println("warning: Failed to disconnect from GMI.");
      }
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public Object get(Serializable key) throws InterruptedException, Exception {
      MemoGet mg = new MemoGet(target, key);
      return rc.call(mg);
   }

   public Object put(Serializable key, Serializable value) throws Exception {
      MemoPut mp = new MemoPut(target, key, value);
      return rc.call(mp);
   }

   public Object getCopy(Serializable key) throws InterruptedException,
         Exception {
      MemoGetCopy mgc = new MemoGetCopy(target, key);
      return rc.call(mgc);
   }

   public Object getCopySkip(Serializable key) throws Exception {
      MemoGetCopySkip mgcs = new MemoGetCopySkip(target, key);
      return rc.call(mgcs);
   }

   public Object getSkip(Serializable key) throws Exception {
      MemoGetSkip mgs = new MemoGetSkip(target, key);
      return rc.call(mgs);
   }

   public Object runDelayed(Serializable key, Runnable r) throws Exception {
      if (r instanceof Serializable) {
         Serializable rs = (Serializable) r;
         MemoRunDelayed mrd = new MemoRunDelayed(target, key, rs);
         return rc.call(mrd);
      } else
         throw new Exception("r must be both Runnable and Serializable");
   }
}
