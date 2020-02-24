package info.jhpc.textbook.message;

import info.jhpc.message.Message;
import info.jhpc.message.MessageClient;

public class KeyValClient {

    public static Message kvServicePut(MessageClient conn, String key, String value) {
        Message m = new Message();
        m.setType(KeyValService.KV_PUT);
        m.setParam("key", key);
        m.setParam("value", value);
        return conn.call(m);

    }

    public static Message kvServiceGet(MessageClient conn, String key) {
        Message m = new Message();
        m.setType(KeyValService.KV_GET);
        m.setParam("key", key);
        return conn.call(m);
    }

    public static void checkForError(Message m) {
        String errorMessage = m.getParam("error");
        if (errorMessage != null)
            System.out.println(errorMessage);
        else
            System.out.println("No error found in Message");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: KeyValClient host port");
        }
        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            port = KeyValService.KV_SERVICE_PORT;
        }

        MessageClient conn;
        try {
            conn = new MessageClient(host, port);
        } catch (Exception e) {
            System.err.println("Could not contact KeyValService @ " + host + ":"
                    + port);
            return;
        }

        Message m;
        m = kvServicePut(conn, "a", "25");
        checkForError(m);
        m = kvServicePut(conn, "b", "25");
        checkForError(m);
        m = kvServiceGet(conn, "a");
        System.out.println("a = " + m.getParam("value"));
        m = kvServiceGet(conn, "b");
        System.out.println("b = " + m.getParam("value"));
        // should be null for c
        m = kvServiceGet(conn, "c");
        System.out.println("c = " + m.getParam("value"));
        conn.disconnect();
    }

}
