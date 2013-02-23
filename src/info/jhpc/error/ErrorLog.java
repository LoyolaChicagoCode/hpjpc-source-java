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

package info.jhpc.error;

import java.io.PrintStream;

/**
 * This class provides a very simple and consistent facility for logging. It
 * will probably be extended to support levels and filtering of certain
 * messages.
 */
public class ErrorLog {
   public static final String WARNING = "Warning";
   public static final String ERROR = "Error";
   public static final String FATAL = "Fatal Error";
   public static final String INFORMATION = "Information";

   private String className;
   private String funcName;
   private String tag;
   private boolean forceStderr;
   private PrintStream ps;

   /**
    * create an instance
    * 
    * @param ps
    *           the stream to use for writing messages
    * @param className
    *           the name of the class issuing messages
    * @param forceStderr
    *           forcibly write all messages to System.err
    */
   public ErrorLog(PrintStream ps, String className, boolean forceStderr) {
      this.ps = ps;
      this.className = className;
      this.forceStderr = forceStderr;
   }

   /**
    * create an instance
    * 
    * @param className
    *           the name of the class issuing messages
    * @param forceStderr
    *           forcibly write all messages to System.err
    */
   public ErrorLog(String className, boolean forceStderr) {
      this(System.err, className, forceStderr);
   }

   /**
    * set the method (or function) name where messages are being issued
    * 
    * @param funcName
    *           name of the method (or function)
    */
   public void setFunction(String funcName) {
      this.funcName = funcName;
   }

   /**
    * set a tag so you can easily search for the error with your editor
    * 
    * @param tag
    *           a tag that (hopefully) will be unique
    */
   public void setTag(String tag) {
      this.tag = tag;
   }

   /**
    * internal method to print message without new line
    * 
    * @param message
    *           the message to print
    */
   private void print(String message) {
      ps.print(message);
      if (ps != System.err)
         System.err.print(message);
   }

   /**
    * internal method to print message with new line
    * 
    * @param message
    *           the message to print
    */
   private void println(String message) {
      ps.println(message);
      if (ps != System.err)
         System.err.println(message);
   }

   /**
    * write an error message
    * 
    * @param type
    *           kind of message (see static constants)
    * @param message
    *           the message to be printed
    * @param errorCode
    *           an exit code (zero is success)
    */
   public void error(String type, String message, int errorCode) {
      print(type);
      print(":");
      print(className);
      if (funcName != null) {
         print(".");
         print(funcName);
         print("(...)");
      }
      if (tag != null) {
         print(":");
         print(tag);
         print(" ");
      }
      println(message);
      if (errorCode != 0)
         System.exit(errorCode);
   }

   public void warning(String message) {
      error(WARNING, message, 0);
   }

   public void information(String message) {
      error(INFORMATION, message, 0);
   }

   public void nonFatalError(String message) {
      error(ERROR, message, 0);
   }

   public void fatalError(String message) {
      error(FATAL, message, -1);
   }

   public void fatalError(String message, int exitCode) {
      error(FATAL, message, exitCode);
   }
}
