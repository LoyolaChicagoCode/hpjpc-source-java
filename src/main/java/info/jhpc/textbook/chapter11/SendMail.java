/*
 To accompany High-Performance Java Platform(tm) Computing:
 Threads and Networking, published by Prentice Hall PTR and
 Sun Microsystems Press.

 Threads and Networking Library
 Copyright (C) 1999-2000
 Thomas W. Christopher and George K. Thiruvathukal

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as published by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 */
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

package info.jhpc.textbook.chapter11;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class SendMail extends Frame implements Runnable, ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 5601274884960479044L;

    private TextField to, from, subject, smtpHost;

    private TextArea message;

    private Button send;

    private Thread progressThread;

    private Socket smtpConnection;

    private PrintWriter out;

    private BufferedReader in;

    @SuppressWarnings("deprecation")
    public SendMail() {
        super("SendMail by Tools of Computing LLC");
        setLayout(new GridBagLayout());
        GridBagConstraints gbc0 = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc0.gridwidth = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        to = new TextField(40);
        to.setText("gkt@gauss.jhpc.cs.depaul.edu");
        from = new TextField(40);
        from.setText("gkt@cs.depaul.edu");
        subject = new TextField(40);
        subject.setText("GEORGE MAIL: " + new Date().toString());
        smtpHost = new TextField(40);
        smtpHost.setText("gauss.jhpc.cs.depaul.edu");
        add(new Label("To:"), gbc0);
        add(to, gbc);
        add(new Label("From:"), gbc0);
        add(from, gbc);
        add(new Label("Subject:"), gbc0);
        add(subject, gbc);
        add(new Label("SMTP Agent:"), gbc0);
        add(smtpHost, gbc);
        message = new TextArea(25, 40);
        add(new Label("Message"), gbc);
        add(message, gbc);
        send = new Button("Send");
        send.addActionListener(this);
        add(send, gbc);
        pack();
        show();
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        SendMail sm = new SendMail();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == send) {
            if (progressThread != null)
                return;
            sendMail();
            this.dispose();
            System.exit(0);
        }
    }

    // This is used to monitor progress (asynchronously) of the
    // send mail session.

    private void sendMail() {
        try {
            smtpConnection = new Socket(smtpHost.getText(), 25);
            System.out.println("<general> Connected to " + smtpHost.getText()
                    + "\n");
            OutputStream os = smtpConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            out = new PrintWriter(osw);

            InputStream is = smtpConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            in = new BufferedReader(isr);

            progressThread = new Thread(this);
            progressThread.start();
            // Send the EHLO command.
            String command;

            command = "EHLO jhpc.cs.depaul.edu";
            out.println(command);
            System.out.println("<send>" + command);
            out.flush();
            // Send the MAIL FROM command.

            int msgLength = from.getText().length() + message.getText().length();
            command = "MAIL FROM: <" + from.getText() + "> SIZE=" + msgLength;
            out.println(command);
            System.out.println("<send>" + command);
            out.flush();

            command = "RCPT TO: <" + to.getText() + ">";
            out.println(command);
            System.out.println("<send>" + command);
            out.flush();

            command = "DATA";
            out.println(command);
            System.out.println("<send>" + command);

            command = message.getText() + "\n" + ".";
            out.println(command);
            out.flush();
            System.out.println("<send>" + command);

            command = "QUIT";
            out.println(command);
            out.flush();
            System.out.println("<send>" + command);

            progressThread.join();
            System.out.println("<general> We joined successfully.");
            smtpConnection.close();
            progressThread = null;
        } catch (Exception e) {
            System.out.println("<general> " + e.toString() + "\n");
        }

    }

    public void run() {
        try {
            String input;
            while (true) {
                input = in.readLine();
                if (input == null)
                    break;
                System.out.println("<reply> " + input);
                if (input.indexOf("accepted for delivery") >= 0) {
                    System.out.println("<general> Got termination response.");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("<general> " + e);
        }
        System.out.println("Leaving run() method normally.");
    }

}
