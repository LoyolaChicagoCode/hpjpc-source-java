package info.jhpc.textbook.chapter03;

import java.io.OutputStream;
import java.util.Vector;

// begin-class-Pool
public class Pool {

    Vector<Buffer> freeBufferList = new Vector<Buffer>();
    OutputStream debug = System.out;
    int buffers, bufferSize;

    public Pool(int buffers, int bufferSize) {
        this.buffers = buffers;
        this.bufferSize = bufferSize;
        freeBufferList.ensureCapacity(buffers);

        for (int i = 0; i < buffers; i++)
            freeBufferList.addElement(new Buffer(bufferSize));
    }

    public synchronized Buffer use() throws InterruptedException {
        while (freeBufferList.size() == 0)
            wait();
        Buffer nextBuffer = freeBufferList.lastElement();
        freeBufferList.removeElement(nextBuffer);
        return nextBuffer;
    }

    public synchronized void release(Buffer oldBuffer) {
        if (freeBufferList.size() == 0)
            notify();

        if (freeBufferList.contains(oldBuffer))
            return;

        if (oldBuffer.getSize() < bufferSize)
            oldBuffer.setSize(bufferSize);

        freeBufferList.addElement(oldBuffer);
    }
}
// end-class-Pool

