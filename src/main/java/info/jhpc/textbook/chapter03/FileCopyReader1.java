package info.jhpc.textbook.chapter03;

import java.io.FileReader;
import java.io.IOException;

// begin-class-FileCopyReader1
public class FileCopyReader1 extends Thread {

    private FileReader fr;
    private Pool pool;
    private BufferQueue copyBuffers;

    public FileCopyReader1(String filename, Pool pool, BufferQueue copyBuffers)
            throws IOException {
        this.pool = pool;
        this.copyBuffers = copyBuffers;
        fr = new FileReader(filename);
    }

    public void run() {
        Buffer buffer;
        int bytesRead = 0;

        do {
            try {
                buffer = pool.use();
                bytesRead = fr.read(buffer.getBuffer());
            } catch (Exception e) {
                buffer = new Buffer(0);
                bytesRead = 0;
            }
            if (bytesRead < 0) {
                buffer.setSize(0);
            } else {
                buffer.setSize(bytesRead);
            }
            copyBuffers.enqueueBuffer(buffer);
        } while (bytesRead > 0);

        try {
            fr.close();
        } catch (Exception e) {
            return;
        }
    }
}
// end-class-FileCopyReader1

