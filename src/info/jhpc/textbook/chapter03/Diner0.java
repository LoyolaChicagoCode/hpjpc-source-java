package info.jhpc.textbook.chapter03;

import info.jhpc.thread.*;

// begin-class-Diner0
class Diner0 extends Thread {
    private char state='t';
    private Fork L,R;

    public Diner0(Fork left, Fork right) {
        super();
        L=left;
        R=right;
    }

    protected void think() throws InterruptedException {
        sleep((long)(Math.random()*7.0));
    }

    protected void eat() throws InterruptedException {
        sleep((long)(Math.random()*7.0));
    }

    public char getDinerState() {
    	return state;
    }

    public void run() {
        int i;

        try {
            for (i=0; i<1000; i++) {
                state = 't';
                think();
                state=L.id;
                sleep(1);
                L.pickup();
                state=R.id;
                sleep(1);
                R.pickup();
                state='e';
                eat();
                L.putdown();
                R.putdown();
            }
            state='d';
        } catch (InterruptedException e) {}
    }
}
// end-class-Diner0
