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

import info.jhpc.gmi.*;
import info.jhpc.thread.SharedTableOfQueues;

import java.io.Serializable;

public class MemoServer implements Callable {

    private SharedTableOfQueues stoq = new SharedTableOfQueues();

    public Serializable call(CallMessage message) throws Exception {

        if (message instanceof MemoMessage) {
            MemoMessage mm = (MemoMessage) message;
            return mm.go(stoq);
        } else
            return new Ok(false);
    }

    public static class Server {
        public static int MEMO_DEFAULT_PORT = 2099;
        public static int memoPort;

        public static void message(String message) {
            System.out.println("Memo.Server: " + message);
        }

        public static void main(String[] args) {
            System.out.println("MemoServer version 1.0");
            System.out.println("Copyright (c) 2000, TC, GKT, John, etc.");

            RemoteCallServer cs;
            try {
                memoPort = Integer.parseInt(args[0]);
            } catch (Exception e) {
                memoPort = MEMO_DEFAULT_PORT;
            }

            message("running server on " + memoPort);
            try {
                cs = new RemoteCallServer(memoPort);
            } catch (Exception e) {
                System.err.println();
                return;
            }

            message("registering 'memo2000' name for Memo instance");
            /* create some callables. */
            cs.bind("memo", new MemoServer());

            /* listen for remote calls */
            message("Starting RMI-Lite Listener");
            cs.start();
            try {
                cs.join();
            } catch (Exception e) {
                message("could not join() with main thread");
            }
        }

    }

    public static class SimpleTest1 {

        public static void main(String[] args) {
            try {
                RemoteCallClient rc = new RemoteCallClient("127.0.0.1", 2099);
                MemoPut p = new MemoPut("memo2000", "A", "value of A");
                rc.call(p);
                MemoGet g = new MemoGet("memo2000", "A");
                System.out.println("A -> " + rc.call(g));
                rc.disconnect();
            } catch (Exception e) {
                System.err.println(e);
            }

        }
    }

    public static class WeirdTest1 {

        public static void main(String[] args) {
            try {
                MemoClient c1 = new MemoClient("127.0.0.1", 2099, "memo2000");
                MemoClient c2 = new MemoClient("127.0.0.1", 2099, "memo2001");

                System.out.println("connected to both");
                c1.put("A", "memo2000:A");
                System.out.println("put1 ");
                c2.put("A", "memo2001:A");
                System.out.println("put2 ");
                System.out.println("A @ memo2000 = " + c1.get("A"));
                System.out.println("A @ memo2001 = " + c2.get("A"));
                c1.goodbye();
                c2.goodbye();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
