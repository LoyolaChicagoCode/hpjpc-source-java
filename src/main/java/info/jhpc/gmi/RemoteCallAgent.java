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

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class RemoteCallAgent extends Thread {
    ErrorLog err = new ErrorLog("RemoteCallAgent", false);
    private ObjectOutputStream out;
    private SharedTableOfQueues callStore;
    private int unissuedCalls = 0;
    private boolean disconnected = false;

    public RemoteCallAgent(Socket socket, ObjectOutputStream out,
                           SharedTableOfQueues callStore) {
        err.setFunction("RemoteCallAgent");
        err.setTag("1");
        this.out = out;
        this.callStore = callStore;
        err.information("starting RemoteCallAgent thread");
        this.start();
    }

    public void setDebug(OutputStream debug) {
    }

    public void call(CallMessage message) throws Exception {
        /*
         * To accompany High-Performance Java Platform(tm) Computing: Threads and
         * Networking, published by Prentice Hall PTR and Sun Microsystems Press.
         *
         * Threads and Networking Library Copyright (C) 1999-2000 Thomas W.
         * Christopher and George K. Thiruvathukal
         *
         * This library is free software; you can redistribute it and/or modify it
         * under the terms of the GNU Library General Public License as published
         * by the Free Software Foundation; either version 2 of the License, or
         * (at your option) any later version.
         *
         * This library is distributed in the hope that it will be useful, but
         * WITHOUT ANY WARRANTY; without even the implied warranty of
         * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
         * Library General Public License for more details.
         *
         * You should have received a copy of the GNU Library General Public
         * License along with this library; if not, write to the Free Software
         * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
         * USA.
         */

        err.setFunction("call");
        err.setTag("2");
        synchronized (this) {
            unissuedCalls++;
            notify();
        }
        err.information("bumped unissued calls to " + unissuedCalls);
        callStore.put("call", message);
        err.information("wrote call to STOQ");
    }

    public void disconnect() throws InterruptedException {
        synchronized (this) {
            disconnected = true;
            notify();
        }
        this.join();
    }

    public void run() {
        err.setFunction("run");
        err.setTag("3");
        while (true) {
            System.err.println("RemoteCallAgent thread");
            synchronized (this) {
                err.information("unissued calls " + unissuedCalls);
                err.information("disconnected status " + disconnected);
                while (unissuedCalls == 0 && !disconnected)
                    try {
                        wait();
                    } catch (Exception e) {
                        System.err
                                .println("RemoteCallAgent interrupted unexpectedly");
                        return;
                    }
            }
            if (unissuedCalls == 0 && disconnected)
                break;

            err.information("about to issue a call");
            CallMessage message;
            try {
                message = (CallMessage) callStore.get("call");
            } catch (Exception e) {
                System.out.println("RemoteCallAgent " + e);
                return;
            }

            err.information("issuing call " + message.getTicket());
            try {
                out.writeObject(message);
                out.flush();
            } catch (Exception e) {
                System.err.println("RemoteCallAgent.run(): I/O Exception");
                // throw new GMICallIssueException(message);
            }
            err.information("call issued " + message.getTicket());

            synchronized (this) {
                unissuedCalls--;
                err.information("remaining calls = " + unissuedCalls);
            }
        }
    }
}
