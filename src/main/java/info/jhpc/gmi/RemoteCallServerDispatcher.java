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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class RemoteCallServerDispatcher extends Thread {
   RemoteCallServer callServer;
   Socket socket;
   ObjectInputStream in;
   ObjectOutputStream out;

   public RemoteCallServerDispatcher(RemoteCallServer callServer, Socket socket)
         throws IOException {
      this.callServer = callServer;
      this.socket = socket;
      this.in = new ObjectInputStream(socket.getInputStream());
      this.out = new ObjectOutputStream(socket.getOutputStream());
   }

   public void run() {
      while (true) {
         /*
          * To accompany High-Performance Java Platform(tm) Computing: Threads
          * and Networking, published by Prentice Hall PTR and Sun Microsystems
          * Press.
          * 
          * Threads and Networking Library Copyright (C) 1999-2000 Thomas W.
          * Christopher and George K. Thiruvathukal
          * 
          * This library is free software; you can redistribute it and/or modify
          * it under the terms of the GNU Library General Public License as
          * published by the Free Software Foundation; either version 2 of the
          * License, or (at your option) any later version.
          * 
          * This library is distributed in the hope that it will be useful, but
          * WITHOUT ANY WARRANTY; without even the implied warranty of
          * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
          * Library General Public License for more details.
          * 
          * You should have received a copy of the GNU Library General Public
          * License along with this library; if not, write to the Free Software
          * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
          * 02111-1307, USA.
          */

         CallMessage message;
         String replyTicket;

         try {
            message = (CallMessage) in.readObject();
         } catch (Exception e0) {
            System.err
                  .println("GMI Exception: run(): Could not read CallMessage; stream may be corrupted.");
            break;
         }

         replyTicket = message.getTicket();
         /*
          * Check whether client wants to disconnect. Goodbye is reserved for
          * this purpose. If Goodbye is received, simply send it back to the
          * client. If for some reason the message cannot be written or the
          * flush fails, it is time to get out anyway, since no more messages
          * will be received.
          */
         if (message instanceof Goodbye) {
            try {
               message.setTicket(replyTicket);
               out.writeObject(message);
               out.flush();
            } catch (Exception e1) {
               System.err.println("GMI Exception: run(): e1 = " + e1);
            }
            break;
         }

         /*
          * Find the invocation target. The message contains the name of the
          * invocation target, which must be used to index the list of Callables
          * in the RemoteCallServer that created this RemoteCallServerDispatcher
          * instance.
          */
         Callable callTarget = callServer.lookup(message.getTarget());

         /*
          * If the invocation target is null, this means that the client
          * specified the name of an object that is not registered in the list
          * of Callables. This corresponds to the "else" clause.
          * 
          * CallMessageException is used so the client can distinguish
          * Exceptions (subclasses of CallMessageException) from normal return
          * values (subclasses of CallMessage). If an exception is thrown when
          * the invocation is performed, the Exception instance is wrapped in a
          * CallMessageException instance and returned.
          */
         Serializable result;
         if (callTarget != null) {
            try {
               result = callTarget.call(message);
            } catch (Exception e2) {
               result = new CallMessageException(e2);
            }
         } else {
            GMINullTargetException gmiException = new GMINullTargetException();
            result = new CallMessageException(gmiException);
         }

         /*
          * At this point, result refers to either (a) the return value of the
          * invocation or (b) an Exception that was thrown in performing the
          * invocation. In either case, the result is returned. The client will
          * rethrow the exception in the case where a CallMessageException
          * instance is returned.
          * 
          * The result may in fact have been a general Serializable. Since I
          * need a CallMessage to encode the replyTicket, the Serializable needs
          * to be wrapped in CallMessageGeneralReply.
          */
         try {
            CallMessage resultCM;
            if (result instanceof CallMessage)
               resultCM = (CallMessage) result;
            else
               resultCM = new CallMessageGeneralReply(result);
            resultCM.setTicket(replyTicket);
            out.writeObject(result);
            out.flush();
         } catch (Exception e3) {
            System.err.println("GMI Exception: run(): e3 = " + e3);
         }
      }
      try {
         out.flush();
         out.close();
         in.close();
         socket.close();
      } catch (Exception e4) {
         System.err.println("GMI Exception: run(): tear down failed (warning)");
         return;
      }
   }
}
