import java.util.*;
import java.io.*;

// begin-class-BufferQueue
class BufferQueue {
    public Vector buffers = new Vector();

    public synchronized void enqueueBuffer(Buffer b) {
        if (buffers.size() == 0)
            notify();
        buffers.addElement(b);
    }

    public synchronized Buffer dequeueBuffer()
    throws InterruptedException {
        while (buffers.size() == 0)
            wait();

        Buffer firstBuffer = (Buffer) buffers.elementAt(0);
        buffers.removeElementAt(0);
        return firstBuffer;
    }
}
// end-class-BufferQueue