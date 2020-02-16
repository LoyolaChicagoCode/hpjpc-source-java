package info.jhpc.textbook.chapter03;

import info.jhpc.thread.*;

// begin-class-Fork
class Fork {
   public char id;

   private Lock lock = new Lock();

   public void pickup() throws InterruptedException {
      lock.lock();
   }

   public void putdown() throws InterruptedException {
      lock.unlock();
   }

   public Fork(int value) {
      this.id = new Integer(value).toString().charAt(0);
   }
}
// end-class-Fork
