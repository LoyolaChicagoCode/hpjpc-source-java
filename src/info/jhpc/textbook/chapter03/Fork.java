package info.jhpc.textbook.chapter03;

import info.jhpc.thread.*;

// begin-class-Fork
class Fork {
    public char id;

    private Lock lock=new Lock();

    public void pickup() throws InterruptedException {
        lock.lock();
    }

    public void putdown() throws InterruptedException {
        lock.unlock();
    }

    public Fork(int i) {
        Integer i = new Integer(i);
        id = i.toString().charAt(0);
    }
}
// end-class-Fork
