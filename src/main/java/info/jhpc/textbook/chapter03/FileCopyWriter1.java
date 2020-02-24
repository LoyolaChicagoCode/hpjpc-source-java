package info.jhpc.textbook.chapter03;

import java.io.FileWriter;
import java.io.IOException;

// begin-class-FileCopyWriter1
public class FileCopyWriter1 extends Thread {

    private FileWriter fw;
    private Pool pool;
    private BufferQueue copyBuffers;

    public FileCopyWriter1(String filename, Pool pool, BufferQueue copyBuffers)
            throws IOException {

        this.pool = pool;
        this.copyBuffers = copyBuffers;
        fw = new FileWriter(filename);
    }

    public void run() {
        Buffer buffer;

        while (true) {
            try {
                buffer = copyBuffers.dequeueBuffer();
            } catch (Exception e) {
                return;
            }
            if (buffer.getSize() > 0) {
                try {
                    char[] bufferData = buffer.getBuffer();
                    int size = bufferData.length;
                    fw.write(bufferData, 0, size);
                } catch (Exception e) {
                    break;
                }
                pool.release(buffer);
            } else
                break;
        }

        try {
            fw.close();
        } catch (Exception e) {
            return;
        }
    }
}
// end-class-FileCopyWriter1
