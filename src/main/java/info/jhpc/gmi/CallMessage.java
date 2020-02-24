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

package info.jhpc.gmi;

import java.io.Serializable;

public abstract class CallMessage implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1984358643551609711L;

    /**
     * The name of the remote object. This must correspond to the name of an
     * object that was bound and registered.
     */
    protected String target;

    private String ticket;

    /**
     * Constructs a Call message with an invocation target in mind.
     *
     * @param target the name of a remote object.
     */

    public CallMessage(String target) {
        this.target = target;
    }

    /**
     * get the tag for a remote call
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * establish a tag for use in a remote call.
     *
     * @param ticket the tag
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * Get the invocation target. This method cannot be overridden as GMI depends
     * on it to determine the name of the object to be called.
     *
     * @return the invocation target.
     */
    public final String getTarget() {
        return target;
    }

    /**
     * Change the invocation target. This allows a call message to be reused to
     * make multiple calls to different objects.
     *
     * @param target the name of a remote object.
     */
    public void setTarget(String target) {
        this.target = target;
    }

}
