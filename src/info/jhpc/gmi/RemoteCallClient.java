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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class RemoteCallClient {
   private Socket socket;
   private ObjectOutputStream out;
   private ObjectInputStream in;
   private RemoteCallAgent callAgent;
   private RemoteReplyAgent replyAgent;
   private SharedTableOfQueues callStore = new SharedTableOfQueues();
   private TicketGenerator ticketGenerator = new TicketGenerator("gmi");
   private ErrorLog e = new ErrorLog("RemoteCallClient", false);

   public RemoteCallClient(String host, int port) throws IOException {
      socket = new Socket(host, port);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
      e.setFunction("RemoteCallClient");
      e.setTag("1");
      e.information("created RemoteCallAgent");
      callAgent = new RemoteCallAgent(socket, out, callStore);
      e.information("created RemoteReplyAgent");
      replyAgent = new RemoteReplyAgent(socket, in, callStore);
   }

   public void setDebug(OutputStream debug) {
   }

   public Object call(CallMessage message) throws Exception {

      e.setFunction("call");
      e.setTag("2");
      String callTicket = ticketGenerator.nextTicket();
      e.information("ticket generated " + callTicket);
      message.setTicket(callTicket);

      e.information("added ticket to wait list " + callTicket);
      /* inform the reply agent that there is a call about to be made */
      replyAgent.addWaitingTicket(callTicket);

      e.information("issuing GMI call " + callTicket);
      /* Make the call. */
      callAgent.call(message);

      e.information("waiting for result of GMI call " + callTicket);
      /*
       * await the reply. This could re-throw a remote exception, hence the
       * "throws Exception" above.
       */
      return replyAgent.getReply(message);
   }

   public Object call(String altTarget, CallMessage message) throws Exception {
      message.setTarget(altTarget);
      return call(message);
   }

   public void disconnect() throws GMIDisconnectException {
      try {
         callAgent.disconnect();
         replyAgent.disconnect();
         out.close();
         in.close();
         socket.close();
      } catch (Exception e) {
         throw new GMIDisconnectException();
      }
   }
}
