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
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

/* You should study WindowListener interface to make it possible to
 * make this login window "go away". You'll also want this to gracefully
 * bring down the RemoteCallServer.

 * This class does not require many changes. You'll mostly hack in the
 * ChatClientRoom class (at least for the interface).
 */

public class ChatClientLogin extends Frame implements ActionListener,
        WindowListener {

    /**
     *
     */
    private static final long serialVersionUID = -7907738610923357381L;
    public static String host = "127.0.0.1";
    public static int port = 1999;
    TextField username;
    TextField password;
    RemoteCallClient rpc;
    Vector<ChatClientRoom> clientSessionList = new Vector<ChatClientRoom>();

    ChatClientLogin(RemoteCallClient rpc) {
        super("Login");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        Panel p = new Panel();
        p.setLayout(new FlowLayout());
        p.add(new Label("Login: "));
        username = new TextField(10);
        p.add(username);
        add(p, gbc);

        p = new Panel();
        p.setLayout(new FlowLayout());
        p.add(new Label("Password: "));
        password = new TextField(10);
        password.setEchoChar('*');
        p.add(password);
        add(p, gbc);

        Button b = new Button("Login");
        b.addActionListener(this);
        add(b, gbc);

        this.rpc = rpc;
        addWindowListener(this);
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {

        try {
            InetAddress ia = InetAddress.getByName(args[0]);
            host = ia.getHostAddress();
        } catch (Exception e) {
            System.out.println("usage: ChatClientLogin host [port=1999]");
            System.exit(0);
        }

        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("warning: either no args[1] or invalid integer");
            port = 1999;
        }

        try {
            RemoteCallClient rc = new RemoteCallClient(host, port);
            ChatClientLogin cl = new ChatClientLogin(rc);
            cl.pack();
            cl.show();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Login"))
            doLogin();
    }

    @SuppressWarnings("deprecation")
    public void doLogin() {
        ChatLogin cl = new ChatLogin("aol2000", username.getText(),
                password.getText());
        ChatSession cs;
        try {
            cs = (ChatSession) rpc.call(cl);
        } catch (Exception e) {
            System.err.println("ChatClientLogin.doLogin(): remote call failed");
            System.err.println(e);
            e.printStackTrace();
            return;
        }
        String sessionId = cs.getSessionId();

        if (sessionId != null) {
            ChatClientRoom ccr = new ChatClientRoom(rpc, sessionId);
            clientSessionList.addElement(ccr);
            ccr.pack();
            ccr.show();
        }
    }

    public void goAway() {
        /* need to cleanup here */
        Enumeration<ChatClientRoom> cc = clientSessionList.elements();
        while (cc.hasMoreElements()) {
            ChatClientRoom ccr = cc.nextElement();
            ccr.goAway();
        }
        this.dispose();
        try {
            rpc.disconnect();
        } catch (Exception e) {
            System.err.println("ChatClientLogin could not disconnect from GMI");
        }
        System.exit(0);
    }

    public void windowClosing(WindowEvent e) {
        goAway();
    }

    // All NOP
    public void windowActivated(WindowEvent e) {
        System.out.println("unhandled windowActivated");
    }

    public void windowClosed(WindowEvent e) {
        System.out.println("unhandled windowClosed");
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
        System.out.println("unhandled windowDeiconified");
    }

    public void windowIconified(WindowEvent e) {
        System.out.println("unhandled Icon");
    }

    public void windowOpened(WindowEvent e) {
        System.out.println("windowOpened");
    }
}
