import java.io.*;
import java.util.*;

// begin-class-FileCopyWriter1
class FileCopyWriter1 extends Thread {

    private Pool pool;
    private BufferQueue copyBuffers;
    private String filename;

    FileWriter fw;

    public FileCopyWriter1(String filename, Pool pool,
                           BufferQueue copyBuffers) throws IOException {

        this.filename = filename;
        this.pool = pool;
        this.copyBuffers = copyBuffers;
        fw = new FileWriter(filename);
    }

    public void run() {
        Buffer buffer;

        while (true) {
            try {
                buffer = copyBuffers.dequeueBuffer();
            } catch(Exception e) {
                return;
            }
            if (buffer.getSize() > 0) {
                try {
                    char[] buffer = buffer.getBuffer();
                    int size = buffer.getSize();
                    fw.write(buffer, 0, size);
                } catch(Exception e) {
                    break;
                }
                pool.release(buffer);
            } else break;
        }

        try {
            fw.close();
        }

        catch(Exception e) {
            return;
        }
    }
}
// end-class-FileCopyWriter1
