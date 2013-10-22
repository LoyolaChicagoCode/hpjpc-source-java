package info.jhpc.textbook.message;

import info.jhpc.message.Message;
import info.jhpc.message.MessageServer;
import info.jhpc.message.MessageService;

import java.util.Hashtable;

public class KeyValService implements MessageService {
   public static final int KV_PUT = 100;
   public static final int KV_GET = 101;
   public static final int KV_SERVICE_PORT = 1999;
   public Hashtable<String, String> kvStore = new Hashtable<String, String>();

   public Message process(Message m) {
      if (m.getType() == KV_GET)
         return doGet(m);
      else if (m.getType() == KV_PUT)
         return doPut(m);
      return m;
   }

   private Message doPut(Message m) {
      try {
         String key = m.getParam("key");
         String value = m.getParam("value");
         kvStore.put(key, value);
      } catch (Exception e) {
         m.setParam("error",
               "must specify key and value parameters (one ore both missing)");
      }
      return m;
   }

   private Message doGet(Message m) {
      try {
         String key = m.getParam("key");
         String value = kvStore.get(key);
         m.setParam("value", value);
      } catch (Exception e) {
         m.setParam("error",
               "must specify key parameter");
      }
      return m;
   }

   public static void main(String args[]) {
      KeyValService ds = new KeyValService();
      MessageServer ms;
      try {
         ms = new MessageServer(KV_SERVICE_PORT);
      } catch (Exception e) {
         System.err.println("Could not start service " + e);
         return;
      }
      Thread msThread = new Thread(ms);
      ms.subscribe(KV_PUT, ds);
      ms.subscribe(KV_GET, ds);
      msThread.start();
   }
}
