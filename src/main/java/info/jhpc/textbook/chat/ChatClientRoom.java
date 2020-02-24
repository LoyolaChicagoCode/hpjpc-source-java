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

import info.jhpc.gmi.RemoteCallClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Vector;

/*
 * You should study code for WindowListener to allow this window to
 * be dismissed and gracefully disconnect the client from the chat
 * server.
 */

@SuppressWarnings("serial")
public class ChatClientRoom extends Frame implements ActionListener, Runnable,
        WindowListener {

    RemoteCallClient rpc;
    String sessionId;
    TextArea chatText;
    TextField sendText;
    Thread pollingThread;
    volatile boolean keepPolling = true;
    boolean dead = false;

    public ChatClientRoom(RemoteCallClient rpc, String sessionId) {
        this.rpc = rpc;
        this.sessionId = sessionId;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        Label label = new Label("Welcome to Chat: Session " + sessionId);
        add(label, gbc);

        chatText = new TextArea(24, 40);
        add(chatText, gbc);

        Panel p = new Panel();
        p.setLayout(new FlowLayout());
        Button b = new Button("Send");
        b.addActionListener(this);
        p.add(b);

        sendText = new TextField(32);
        p.add(sendText);
        add(p, gbc);

        pollingThread = new Thread(this);
        pollingThread.start();
        addWindowListener(this);

    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        ChatClientRoom ccr = new ChatClientRoom(null, "AAA");
        ccr.pack();
        ccr.show();
    }

    public void actionPerformed(ActionEvent e) {
        String command;
        command = e.getActionCommand();
        if (command.equals("Send"))
            doSend();
    }

    public void doSend() {
        String toBeSent = sendText.getText();
        PutMessage pm = new PutMessage("aol2000", sessionId, toBeSent);
        try {
            rpc.call(pm);
        } catch (Exception e) {
            System.out.println("ChatClientRoom.doSend() GMI call failed.");
            return;
        }
        sendText.setText("");
    }

    @SuppressWarnings("rawtypes")
    public void run() {
        while (keepPolling) {
            /* sleep 1000 milliseconds; you may want to change this */
            try {
                Thread.sleep(2500);
            } catch (Exception e) {
                return;
            }

            GetMessages gm = new GetMessages("aol2000", sessionId, 2);
            Object result;
            try {
                result = rpc.call(gm);
            } catch (Exception e) {
                System.out.println("ChatClientRoom.run() failed GMI (1)");
                return;
            }

            if (result instanceof Ok)
                continue; /* probably an error but can be ignored for now */
            else {
                GetResults gr = (GetResults) result;
                Vector messages = gr.getMessages();
                Enumeration m = messages.elements();
                while (m.hasMoreElements()) {
                    String lineOfText = (String) m.nextElement();
                    chatText.append(lineOfText + "\n");
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public void goAway() {
        if (dead)
            return;
        dead = true;
        keepPolling = false;
        try {
            pollingThread.join();
        } catch (Exception error) {
        }
        Logout logout = new Logout("aol2000", sessionId);
        try {
            Object logoutResult = rpc.call(logout);
        } catch (Exception e) {
            System.out.println("ChatClientRoom.goAway() failed GMI");
        }
        this.dispose();
    }

    public void windowClosing(WindowEvent e) {
        goAway();
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        goAway();
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}
