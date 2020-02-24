package info.jhpc.textbook.chapter03;

import info.jhpc.thread.Lock;

// begin-class-Fork
public class Fork {
    public char id;

    private Lock lock = new Lock();

    public Fork(int value) {
        this.id = new Integer(value).toString().charAt(0);
    }

    public void pickup() throws InterruptedException {
        lock.lock();
    }

    public void putdown() throws InterruptedException {
        lock.unlock();
    }
}
// end-class-Fork
