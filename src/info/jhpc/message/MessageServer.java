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

/*
 * MessageServer.java - Server for George Method Invocation. A very simple
 * RMI framework for Java in the making.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

// begin-class-MessageServer
public class MessageServer extends Thread {
   private ServerSocket callListener;
   private Hashtable subscribers;

   public static final boolean logging = true;

   public void log(String s) {
      if (!logging)
         return;
      System.err.println("MessageServer: " + s);
   }

   public MessageServer(int port) throws IOException {
      log("Simple Messaging Architecture (SMA) version 1.0");
      log("Copyright (c) 2000, George K. Thiruvathukal");
      callListener = new ServerSocket(port);
      subscribers = new Hashtable();
      log("Created MessageServer instance fully!");
   }

   public void subscribe(int messageType, MessageService d) {
      subscribers.put(messageType + "", d);
   }

   public MessageService getSubscriber(int messageType) {
      return (MessageService) subscribers.get(messageType + "");
   }

   public void run() {
      log("MessageServer thread started. run() method dispatched.");
      while (true) {
         try {
            Socket s = callListener.accept();
            MessageServerDispatcher csd = new MessageServerDispatcher(this, s);
            csd.setDaemon(false);
            csd.start();
         } catch (Exception e) {
            log("Exception " + e);
            e.printStackTrace();
         }
      }
   }
}
//end-class-MessageServer
