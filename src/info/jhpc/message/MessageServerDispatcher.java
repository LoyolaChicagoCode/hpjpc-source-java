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

package info.jhpc.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class MessageServerDispatcher extends Thread {
   MessageServer callServer;
   Socket socket;
   DataInputStream in;
   DataOutputStream out;
   public static final boolean logging = true;

   public MessageServerDispatcher(MessageServer callServer, Socket socket)
         throws IOException {
      this.callServer = callServer;
      this.socket = socket;
      this.in = new DataInputStream(socket.getInputStream());
      this.out = new DataOutputStream(socket.getOutputStream());
   }

   public void log(String s) {
      if (!logging)
         return;
      System.err.println("MessageServerDispatcher: " + s);
   }

   public void run() {
      log("Beginning of dispatch run() method.");
      try {
         while (true) {
            Message m = new Message();
            m.decode(in);
            Message result = null;
            log("Received Message " + m + ".");
            if (m.getType() == 0 && m.getParam("$disconnect") != null) {
               log("Message found with reserved $disconnect parameter.");
               System.err.println("-> Disconnect received by server.");
               Message ack = new Message();
               ack.encode(out);
               socket.close();
               return;
            }
            Deliverable d = callServer.getSubscriber(m.getType());
            if (d != null)
               result = d.send(m);
            else {
               System.err.println("-> No subscribers for this message.");
               result = new Message();
            }
            result.encode(out);
         }
      } catch (EOFException e1) {
         try {
            log("End of file exception." + e1);
            out.close();
            socket.close();
         } catch (Exception e2) {
            log("Unable to free open resources " + e2);
            e2.printStackTrace();
         }
      } catch (Exception e) {
         log("Unknown exception of unknown origin. Possibly a bug: " + e);
         e.printStackTrace();
      }
   }
}
