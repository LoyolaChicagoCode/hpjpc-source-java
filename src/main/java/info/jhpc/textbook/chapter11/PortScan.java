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

import info.jhpc.text.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class PortScan {
   private Hashtable<String, String> services;

   public static String[] serviceLabels = { "service", "portinfo" };

   public static String[] portInfoLabels = { "port", "protocol" };

   private Splitter lineSplitter;

   private Splitter portInfoSplitter;

   public PortScan(String servicesFile) throws Exception {
      FileReader fr = new FileReader(servicesFile);
      @SuppressWarnings("resource")
      BufferedReader br = new BufferedReader(fr);
      String inLine;
      lineSplitter = new Splitter(serviceLabels, " \t\n");
      portInfoSplitter = new Splitter(portInfoLabels, "/");
      services = new Hashtable<String, String>();

      while (true) {
         inLine = br.readLine();
         if (inLine == null)
            break;
         if (inLine.startsWith("#"))
            continue;
         lineSplitter.setText(inLine);
         String service = lineSplitter.getTokenAt("service");
         String portinfo = lineSplitter.getTokenAt("portinfo");
         if (portinfo == null)
            continue;
         portInfoSplitter.setText(portinfo);
         String port = portInfoSplitter.getTokenAt("port");
         String protocol = portInfoSplitter.getTokenAt("protocol");
         if (protocol.equals("tcp"))
            services.put(port, service);
      }
   }

   public void scan(String host, int lo, int hi) {
      int count = 0;
      for (int port = lo; port <= hi; port++) {
         count++;
         if (count % 1000 == 0)
            System.out.println("Tested " + count + " ports.");
         try {
            Socket s = new Socket(host, port);
            String service = services.get(port + "");
            if (service != null)
               System.out.println(port + " -> " + service);
            else
               System.out.println(port + " found but unknown service");
            s.close();
         } catch (Exception e) {
         }
      }
   }

   public static void main(String args[]) {
      try {
         PortScan ps = new PortScan("/etc/services");
         ps.scan(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
      } catch (Exception e) {
         System.out.println(e);
         e.printStackTrace();
      }
   }

}
