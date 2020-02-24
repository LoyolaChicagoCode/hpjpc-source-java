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

import java.util.Vector;

public class ChatUserData {
    private String userName;
    private Vector<String> inList = new Vector<String>();

    ChatUserData(String userName) {
        this.userName = userName;
    }

    public void appendMessage(String message) {
        inList.addElement(message);
    }

    public Vector<String> getSomeMessages(int max) {
        Vector<String> results = new Vector<String>();
        int n;
        if (max <= 0)
            return results;
        for (n = 0; n < max && n < inList.size(); n++)
            results.addElement(inList.elementAt(n));
        for (int i = 0; i < n; i++)
            inList.removeElementAt(0);
        return results;
    }

    @SuppressWarnings("unchecked")
    public Vector<String> getAllMessages() {
        Vector<String> clone = (Vector<String>) inList.clone();
        inList.removeAllElements();
        return clone;
    }

    public String getUserName() {
        return userName;
    }

    public String toString() {
        return "ChatUserData: " + userName + " " + inList + ".";
    }
}
