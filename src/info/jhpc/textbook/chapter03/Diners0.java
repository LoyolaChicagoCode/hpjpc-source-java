package info.jhpc.textbook.chapter03;

import info.jhpc.thread.*;

// begin-class-Diners0
class Diners0 {
   static Fork[] fork = new Fork[5];
   static Diner0[] diner = new Diner0[5];

   public static void main(String[] args) {
      int i, j = 0;
      boolean goOn;

      for (i = 0; i < 5; i++) {
         fork[i] = new Fork(i);
      }

      for (i = 0; i < 5; i++) {
         diner[i] = new Diner0(fork[i], fork[(i + 1) % 5]);
      }

      for (i = 0; i < 5; i++) {
         diner[i].start();
      }

      int newPrio = Thread.currentThread().getPriority() + 1;

      Thread.currentThread().setPriority(newPrio);

      goOn = true;

      while (goOn) {
         for (i = 0; i < 5; i++) {
            System.out.print(diner[i].getDinerState());
         }

         if (++j % 5 == 0)
            System.out.println();
         else
            System.out.print(' ');

         goOn = false;

         for (i = 0; i < 5; i++) {
            goOn |= diner[i].getDinerState() != 'd';
         }

         try {
            Thread.sleep(51);
         } catch (InterruptedException e) {
            return;
         }
      }
   }
}
// end-class-Diners0
