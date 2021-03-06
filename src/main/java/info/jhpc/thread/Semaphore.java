/*
Copyright (c) 2000, Thomas W. Christopher and George K. Thiruvathukal

Java and High Performance Computing (JHPC) Organzization
Tools of Computing LLC

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

The names Java and High-Performance Computing (JHPC) Organization,
Tools of Computing LLC, and/or the names of its contributors may not
be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This license is based on version 2 of the BSD license. For more
information on Open Source licenses, please visit
http://opensource.org.
 */

package info.jhpc.thread;

/**
 * A counting semaphore.
 *
 * @author Thomas W. Christopher (Tools of Computing LLC)
 * @version 0.2 Beta
 */

// begin-class-Semaphore

public class Semaphore {
    /**
     * The current count, which must be non-negative.
     */
    protected int count;

    /**
     * Create a counting Semaphore with a specified initial count.
     *
     * @param initCount The initial value of count.
     * @throws com.toolsofcomputing.thread.NegativeSemaphoreException if initCount &lt; 0.
     */
    public Semaphore(int initCount) throws NegativeSemaphoreException {
        if (initCount < 0)
            throw new NegativeSemaphoreException();
        count = initCount;
    }

    /**
     * Create a counting Semaphore with an initial count of zero.
     */
    public Semaphore() {
        count = 0;
    }

    /**
     * Subtract one from the count. Since count must be non-negative, wait until
     * count is positive before decrementing it.
     *
     * @throws InterruptedException if thread is interrupted while waiting.
     */
    public synchronized void down() throws InterruptedException {
        while (count == 0)
            wait();
        count--;
    }

    /**
     * Add one to the count. Wake up a thread waiting to "down" the semaphore, if
     * any.
     */
    public synchronized void up() {
        count++;
        notify();
    }
}

// end-class-Semaphore

