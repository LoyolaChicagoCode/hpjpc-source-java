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

package info.jhpc.gmi;

import info.jhpc.error.ErrorLog;
import info.jhpc.thread.SharedTableOfQueues;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

public final class RemoteReplyAgent extends Thread {
   private ObjectInputStream in;
   private SharedTableOfQueues callStore;
   private Vector<String> ticketsAwaited = new Vector<String>();
   private boolean disconnected = false;
   ErrorLog err = new ErrorLog("RemoteReplyAgent", false);

   public RemoteReplyAgent(Socket socket, ObjectInputStream in,
         SharedTableOfQueues callStore) {
      err.setFunction("RemoteReplyAgent");
      err.setTag("1");
      this.in = in;
      this.callStore = callStore;
      err.information("starting RemoteReplyAgent thread");
      this.start();
   }

   public void setDebug(OutputStream debug) {
   }

   /*
    * To accompany High-Performance Java Platform(tm) Computing: Threads and
    * Networking, published by Prentice Hall PTR and Sun Microsystems Press.
    *
    * Threads and Networking Library Copyright (C) 1999-2000 Thomas W.
    * Christopher and George K. Thiruvathukal
    *
    * This library is free software; you can redistribute it and/or modify it
    * under the terms of the GNU Library General Public License as published by
    * the Free Software Foundation; either version 2 of the License, or (at your
    * option) any later version.
    *
    * This library is distributed in the hope that it will be useful, but
    * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library General Public
    * License for more details.
    *
    * You should have received a copy of the GNU Library General Public License
    * along with this library; if not, write to the Free Software Foundation,
    * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
    */

   public synchronized void addWaitingTicket(String ticket) {
      err.setFunction("addWaitingTicket");
      err.setTag("2");
      err.information("adding " + ticket + " to await list");
      ticketsAwaited.addElement(ticket);
      err.information("await list = " + ticketsAwaited.toString());
      notify();
   }

   public Object getReply(CallMessage message) throws Exception {
      String callTicket = message.getTicket();
      CallMessage reply = (CallMessage) callStore.get(callTicket);
      if (reply instanceof CallMessageException) {
         CallMessageException exceptionInfo = (CallMessageException) reply;
         throw exceptionInfo.getException();
      } else if (reply instanceof CallMessageGeneralReply) {
         CallMessageGeneralReply cmgr = (CallMessageGeneralReply) reply;
         return cmgr.getReply();
      } else
         return reply;
   }

   public void disconnect() throws InterruptedException {
      synchronized (this) {
         disconnected = true;
         notify();
      }
      this.join();
   }

   /*
    * the way this code works is a bit tricky basically, the remote reply agent
    * has to wait for any outstanding call tickets. so we first have to await a
    * condition either there are more tickets OR a disconnect call has been made
    * now if a disconnect call has been made, we'll only stop processing replies
    * when there are no more outstanding tickets if there are more tickets, we
    * simply get a reply. the reply can correspond to ANY outstanding ticket. we
    * remove the ticket for the reply from the list of outstanding tickets. then
    * we put the reply in the shared table of queues so it can be gotten (either
    * synchronously or asynchronously)
    */
   public void run() {
      err.setFunction("run");
      err.setTag("3");
      err.information("thread entered");
      while (true) {
         synchronized (this) {
            err.information("# of tickets " + ticketsAwaited.size());
            err.information("disconnected status " + disconnected);
            while (ticketsAwaited.size() == 0 && !disconnected) {
               try {
                  wait();
               } catch (InterruptedException ie) {
                  System.err.println("RemoteReplyAgent.run()/wait() failed");
                  return;
               }
            }
         }
         if (disconnected && ticketsAwaited.size() == 0)
            return;

         err.information("processing reply, # waiting tickets "
               + ticketsAwaited.size());
         CallMessage result;
         try {
            result = (CallMessage) in.readObject();
         } catch (Exception e) {
            System.err.println("RemoteReplyAgent.run() I/O error " + e);
            break;
         }

         err.information("reply obtained for ticket " + result.getTicket());
         ticketsAwaited.removeElement(result.getTicket());
         err.information("new ticket list = " + ticketsAwaited.toString());
         callStore.put(result.getTicket(), result);
      }
   }
}
