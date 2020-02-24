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

package info.jhpc.textbook.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class TranslateWriter extends FilterWriter {
    private static char[] oneChar;
    private static char[] charArray;

    static {
        oneChar = new char[1];
    }

    private String from;
    private String to;

    public TranslateWriter(Writer w, String from, String to) {
        super(w);
        this.from = from;
        this.to = to;
    }

    public TranslateWriter(Writer w) {
        super(w);
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";

        this.from = upper + lower;
        this.to = lower + upper;
    }

    /*
     * All write operations are implemented in terms of the write(char[],int,int)
     * method.
     */

    public static void main(String[] args) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(System.out);
        TranslateWriter translateWriter = new TranslateWriter(osw);

        try {
            translateWriter.write("George K. Thiruvathukal\n");
            translateWriter.flush();
            // if you do not flush() System.out, you may see no output.
        } catch (Exception e) {
            System.err.println("Could not write to translate writer.");
            System.exit(1);
        }
        translateWriter.close();
    }

    public void write(int c) throws IOException {
        oneChar[0] = (char) c;
        write(oneChar, 0, 1);
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (cbuf == null)
            throw new IOException();

        int i;
        for (i = 0; i < cbuf.length; i++) {
            int mapIndex = from.indexOf(cbuf[i]);
            if (mapIndex >= 0)
                cbuf[i] = to.charAt(mapIndex);
        }
        super.write(cbuf, off, len);
    }

    public void write(String s, int off, int len) throws IOException {
        if (s == null)
            throw new IOException();
        charArray = s.toCharArray();
        write(charArray, off, len);
    }
}
