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

package info.jhpc.message;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

// begin-class-Message
public class Message {
   private static boolean debug = true;
   private static int maxDebugLevel = 1;
   private static final String P_STRING = "S$";
   private static final String P_INTEGER = "I$";
   private static final String P_LONG = "L$";
   private static final String P_BOOLEAN = "B$";

   private Hashtable<Object, String> parameters = new Hashtable<Object, String>();
   private int type = 0;
   private int tag = 0;
   private int length = 0;

   public Message() {
      // nothing additional to do
   }

   public static void log(int level, String function, String message) {
      if (debug && level <= maxDebugLevel)
         System.out.println("Message::" + function + "> " + message);
   }

   public void encode(DataOutputStream out) throws IOException {
      // output a header
      out.writeUTF("SMA");
      // output length, type, tag
      out.writeInt(length);
      out.writeInt(type);
      out.writeInt(tag);
      // output # of pairs
      out.writeInt(parameters.size());
      // output pairs
      Enumeration<Object> e = parameters.keys();
      while (e.hasMoreElements()) {
         String key = (String) e.nextElement();
         out.writeUTF(key);
         String value = parameters.get(key);
         out.writeUTF(value);
      }
   }

   public void decode(DataInputStream in) throws IOException {
      // read header
      String header = in.readUTF();
      if (!header.equals("SMA"))
         throw new IOException();
      // read length, type, tag
      length = in.readInt();
      type = in.readInt();
      tag = in.readInt();
      int parameterCount = in.readInt();
      for (int i = 0; i < parameterCount; i++) {
         String key = in.readUTF();
         String value = in.readUTF();
         parameters.put(key, value);
      }
   }

   public void setType(int type) {
      this.type = type;
   }

   public int getType() {
      return type;
   }

   public void setTag(int tag) {
      this.tag = tag;
   }

   public int getTag() {
      return tag;
   }

   public void setParam(String key, String value) {
      parameters.put(P_STRING + key, value);
   }

   public String getParam(String key) {
      return parameters.get(P_STRING + key);
   }

   public void setStringParam(String key, String value) {
      parameters.put(P_STRING + key, value);
   }

   public String getStringParam(String key) {
      return parameters.get(P_STRING + key);
   }

   public void setIntegerParam(String key, int value) {
      parameters.put(P_INTEGER + key, value + "");
   }

   public int getIntegerParam(String key) {
      try {
         return Integer.parseInt(parameters.get(P_INTEGER + key));
      } catch (Exception e) {
         return 0; // This cannot happen. I'm just making javac happy.
      }
   }

   public void setLongParam(String key, long value) {
      parameters.put(P_LONG + key, value + "");
   }

   public long getLongParam(String key) {
      try {
         return Long.parseLong(parameters.get(P_LONG + key));
      } catch (Exception e) {
         return 0; // This cannot happen. I'm just making javac happy.
      }
   }

   public void setBooleanParam(String key, boolean value) {
      parameters.put(P_BOOLEAN + key, value + "");
   }

   public boolean getBooleanParam(String key) {
      String value = parameters.get(P_BOOLEAN + key);
      return value.equals(true + "");
   }

   public void merge(Message m) {
      Enumeration<Object> e = m.parameters.keys();
      while (e.hasMoreElements()) {
         Object key = e.nextElement();
         parameters.put(key, m.parameters.get(key));
      }
   }

   public String toString() {
      return "Message: type = " + type + " param = " + parameters;
   }

   public static void main(String args[]) {
      Message m1 = new Message();
      Message m2 = new Message();
      m1.setType(2);
      m1.setTag(3);
      m1.setStringParam("s1", "George");
      m1.setBooleanParam("b2", true);
      m1.setIntegerParam("i3", 100);
      m1.setIntegerParam("i4", 100);

      try {
         FileOutputStream fos = new FileOutputStream("m1.dat");
         DataOutputStream dos = new DataOutputStream(fos);
         m1.encode(dos);
         fos.close();
      } catch (Exception e) {
         System.out.println("exception/m1.dat" + e);
      }
      System.out.println("Message written to m1.dat");
      try {
         FileInputStream fis = new FileInputStream("m1.dat");
         DataInputStream dis = new DataInputStream(fis);
         m2.decode(dis);
         fis.close();
      } catch (Exception e) {
         System.out.println("exception/m2 " + e);
      }
      System.out.println("Read m2");
      System.out.println("Message m1 " + m1);
      System.out.println("Message m2 " + m2);

   }
}
// end-class-Message
