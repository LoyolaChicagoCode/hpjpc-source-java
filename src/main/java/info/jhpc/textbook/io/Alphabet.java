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

public class Alphabet {

    private String in;
    private String key;
    private String out;

    public Alphabet(String in, String key) {
        this.in = in;
        this.key = key;
        createCipherAlphabet();
    }

    public static Alphabet getAlphabet(String in, String key) {
        Alphabet a = new Alphabet(in, key);
        return a;
    }

    public static void main(String[] args) {
        Alphabet a = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "GEORGE");
        System.out.println(a);
    }

    public String getCipherAlphabet() {
        return out;
    }

    public void createCipherAlphabet() {
        out = "";
        for (int i = 0; i < key.length(); i++)
            if (out.indexOf(key.charAt(i)) < 0)
                out += key.charAt(i);

        for (int i = out.length(); i < in.length(); i++) {
            int j;
            for (j = 0; j < in.length(); j++) {
                if (i != j && out.indexOf(in.charAt(j)) < 0) {
                    out += in.charAt(j);
                    break;
                }
            }

        }
    }

    public String toString() {
        String out = "";
        for (int i = 0; i < out.length(); i++)
            out += (in.charAt(i) + ":" + out.charAt(i) + " ");
        return out;
    }
}
