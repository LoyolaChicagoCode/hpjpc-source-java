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

import java.io.*;

public class TranslateReader extends FilterReader {
   private String from;
   private String to;
   private static char[] oneChar;

   static {
      oneChar = new char[1];
   }

   public TranslateReader(Reader r, String from, String to) {
      super(r);
      this.from = from;
      this.to = to;
   }

   public TranslateReader(Reader r) {
      super(r);
      String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String lower = "abcdefghijklmnopqrstuvwxyz";

      this.from = upper + lower;
      this.to = lower + upper;
   }

   public int read() throws IOException {
      int result = read(oneChar, 0, 1);
      if (result < 0)
         return result;
      else
         return oneChar[0];
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      int n = super.read(cbuf, off, len);
      int i;
      for (i = 0; i < n; i++) {
         int mapIndex = from.indexOf(cbuf[i]);
         if (mapIndex >= 0)
            cbuf[i] = to.charAt(mapIndex);
      }
      return n;
   }

   public static void main(String[] args) throws IOException {
      StringReader sr = new StringReader("George Thiruvathukal");
      TranslateReader translateReader = new TranslateReader(sr);
      LineNumberReader lnr = new LineNumberReader(translateReader);

      try {
         String line = lnr.readLine();
         System.out.println(line);
      } catch (Exception e) {
      }
      lnr.close();
   }

}
