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

package info.jhpc.textbook.chat;

import info.jhpc.gmi.CallMessage;
import info.jhpc.gmi.Callable;
import info.jhpc.gmi.RemoteCallServer;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/* The ChatServer needs many enhancements. Look for GKT comments */

public class ChatServer implements Callable {

    private static String nextSessionId = "";

    /*
     * Each session will be maintained in Hashtable. There is a supporting class,
     * ChatSession, that keeps various details for each login and correspondence
     * bound for it.
     */
    private Hashtable<String, ChatUserData> sessions = new Hashtable<String, ChatUserData>();

    private static String getNextSessionId() {
        nextSessionId += "A";
        return nextSessionId;
    }

    /*
     * GKT: You should provide a file that maintains the list of users and
     * passwords. I would be impressed if someone supported basic password
     * scrambling and encryption.
     *
     * Right now, anyone can login and participate.
     */

    private boolean authenticate(String userId, String password) {
        /* This needs to be filled in by students. */
        return true;
    }

    /*
     * the chat login process (a) performs authentication, (b) allocates a
     * session id, and (c) creates chat state.
     */
    private CallMessage doChatLogin(ChatLogin in) {
        if (!authenticate(in.getUserId(), in.getPassword()))
            return new ChatSession(null, false);
        String sessionId = getNextSessionId();
        sessions.put(sessionId, new ChatUserData(in.getUserId()));
        return new ChatSession(sessionId, true);
    }

    private void showRoom() {
        System.out.println("--- Chat Room State ---");
        Enumeration<String> e = sessions.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            System.out.println("Session " + key + " " + sessions.get(key));
        }
        System.out.println("-----------------------");
    }

    private CallMessage doPutMessage(PutMessage in) {
        ChatUserData originatingUser = sessions.get(in
                .getSessionId());
        if (originatingUser == null)
            return new Ok(false);
        Enumeration<String> e = sessions.keys();
        while (e.hasMoreElements()) {
            Object sessionId = e.nextElement();
            ChatUserData cud = sessions.get(sessionId);
            String text = "<" + originatingUser.getUserName() + "> "
                    + in.getMessage();
            cud.appendMessage(text);
        }
        showRoom();
        return new Ok();
    }

    /*
     * GKT: You need to implement this to display messages in the ChatClientRoom
     * window
     */

    private CallMessage doGetMessages(GetMessages in) {
        ChatUserData cud = sessions.get(in.getSessionId());
        if (cud == null)
            return new Ok(false);
        return new GetResults(cud.getSomeMessages(in.getMax()));
    }

    /*
     * GKT: Right now, this doesn't do much. Ideally, you would remove a user
     * from the Hashtable (sessions).
     */
    private CallMessage doLogout(Logout in) {
        System.out.println("Removing Session " + in.getSessionId());
        sessions.remove(in.getSessionId());
        showRoom();
        return new Ok();
    }

    /*
     * Basically, I am going to show the gist of how this is implemented. You
     * will fill in the remaining details and functionality.
     */

    public synchronized Serializable call(CallMessage message) throws Exception {
        if (message instanceof ChatLogin) {
            return doChatLogin((ChatLogin) message);
        } else if (message instanceof PutMessage) {
            return doPutMessage((PutMessage) message);
        } else if (message instanceof GetMessages) {
            return doGetMessages((GetMessages) message);
        } else if (message instanceof Logout) {
            return doLogout((Logout) message);
        } else
            return new Ok(false);
    }

    public static class Go {
        public static int chatPort = 1999;

        public static void main(String[] args) {
            System.out.println("ChatServer version 1.0");
            System.out.println("Copyright (c) 1999, George K. Thiruvathukal");
            System.out
                    .println("If you like it, please support the homeless with a donation.");
            RemoteCallServer cs;

            try {
                chatPort = Integer.parseInt(args[0]);
            } catch (Exception e) {
            }

            System.out.println("ChatServer.Go: Launching on port " + chatPort);
            try {
                cs = new RemoteCallServer(chatPort);
            } catch (Exception e) {
                System.err.println();
                return;
            }

            System.out.println("ChatServer.Go: Binding ChatServer instance.");
            /* create some callables. */
            cs.bind("aol2000", new ChatServer());

            /* listen for remote calls */
            System.out.println("ChatServer.Go: Starting RMI-Lite Listener");
            cs.start();
            try {
                cs.join();
            } catch (Exception e) {
                System.err.println("ChatServer.Go: Unlikely Error Encountered");
            }
        }

    }
}
