package info.jhpc.textbook.chapter03;

// begin-class-Buffer

public class Buffer {
    private char[] buffer;
    private int size;

    public Buffer(int bufferSize) {
        buffer = new char[bufferSize];
        size = bufferSize;
    }

    public char[] getBuffer() {
        return buffer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int newSize) {
        if (newSize > size) {
            char[] newBuffer = new char[newSize];
            System.arraycopy(buffer, 0, newBuffer, 0, size);
            buffer = newBuffer;
        }

        size = newSize;
    }
}

// end-class-Buffer
