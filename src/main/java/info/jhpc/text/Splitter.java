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

package info.jhpc.text;

import java.util.StringTokenizer;
import java.util.Vector;

public class Splitter {

    private String text;
    private String delimiters;
    private Vector<String> tokens;
    private StringTokenizer tokenizer;
    private String[] labels;

    public Splitter(String[] labels, String text, String delimiters) {
        this.text = text;
        this.delimiters = delimiters;
        tokens = new Vector<String>();
        tokenizer = null;
        this.labels = labels;
        performSplit();
    }

    public Splitter(String text, String delimiters) {
        this(new String[0], text, delimiters);
    }

    public Splitter(String[] labels, String delimiters) {
        String defaultText = "";
        for (int i = 0; i < labels.length; i++) {
            defaultText += labels[i];
            defaultText += delimiters.charAt(0);
        }
        this.text = defaultText;
        this.delimiters = delimiters;
        tokens = new Vector<String>();
        tokenizer = null;
        this.labels = labels;
        performSplit();
    }

    public static void main(String[] args) {
        String[] labels = {"username", "password", "home dir", "shell"};
        Splitter s = new Splitter("gkt:X8kk43jkjs:/home/people/gkt:/bin/ksh", ":");
        s.setLabels(labels);

        /* setLabels labelled the fields for us, "username" refers to field 0 */
        System.out.println("username = " + s.getTokenAt("username"));

        /* the password is in field number 1 (0 represents the first field) */
        System.out.println("password = " + s.getTokenAt(1));

        /*
         * inevitably, people will want to be able to put a numbered field
         * reference in a string. this will work provided the field has not been
         * labelled by setLabels with numbers
         */
        System.out.println("home dir = " + s.getTokenAt("2"));

        /* a null reference is returned if a label has not been defined but used */
        System.out.println("bad ref \"bad\" = " + s.getTokenAt("bad"));

        /* similar story for out of bounds field reference */
        System.out.println("bad ref (500) = " + s.getTokenAt(500));
        System.out.println("all tokens (gkt) = " + s);

        s.setText("tc:X4kkjk3jkjs:/home/people/tc:/bin/csh");
        System.out.println("all tokens (tc) = " + s);
    }

    public void setText(String text) {
        this.text = text;
        performSplit();
    }

    public void setDelimiters(String delimiters) {
        this.delimiters = delimiters;
        performSplit();
    }

    public void setTextAndDelimiters(String text, String delimiters) {
        this.text = text;
        this.delimiters = delimiters;
        performSplit();
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public String getLabel(int index) {
        if (labels == null || index < 0 || index >= labels.length)
            return index + "";
        else
            return labels[index];
    }

    private void performSplit() {
        tokenizer = new StringTokenizer(text, delimiters);
        tokens.removeAllElements();
        while (tokenizer.hasMoreTokens()) {
            tokens.addElement(tokenizer.nextToken());
        }
    }

    public int getTokenCount() {
        return tokens.size();
    }

    public String getTokenAt(int position) {
        if (position >= 0 && position < tokens.size())
            return tokens.elementAt(position);
        else
            return null;
    }

    public String getTokenAt(String label) {
        int index = findLabel(label);
        if (index < 0) {
            try {
                index = Integer.parseInt(label);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return getTokenAt(index);
    }

    private int findLabel(String label) {
        int index;
        for (index = 0; index < labels.length; index++)
            if (label.equals(labels[index]))
                return index;
        return -1;
    }

    public Vector<String> getAllTokens() {
        return tokens;
    }

    public void setTokenAt(String text, int position) {
        tokens.setElementAt(text, position);
    }

    public String toString() {
        int i;
        String s = "";

        for (i = 0; i < getTokenCount(); i++) {
            if (i > 0)
                s = s + "\n";

            s = s + "[" + getLabel(i) + "] = " + getTokenAt(i);
        }
        return s;
    }
}
