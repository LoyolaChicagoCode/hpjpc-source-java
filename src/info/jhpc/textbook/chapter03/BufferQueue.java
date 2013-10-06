package info.jhpc.textbook.chapter03;

import java.util.*;

// begin-class-BufferQueue
class BufferQueue {
   public Vector<Buffer> buffers = new Vector<Buffer>();

   public synchronized void enqueueBuffer(Buffer b) {
      if (buffers.size() == 0)
         notify();
      buffers.addElement(b);
   }

   public synchronized Buffer dequeueBuffer() throws InterruptedException {
      while (buffers.size() == 0)
         wait();

      Buffer firstBuffer = buffers.elementAt(0);
      buffers.removeElementAt(0);
      return firstBuffer;
   }
}
// end-class-BufferQueue
