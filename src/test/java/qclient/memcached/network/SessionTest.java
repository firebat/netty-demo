package qclient.memcached.network;

public class SessionTest {


    public static void main(String[] args) {
        Session session = new Session("127.0.0.1", 11211);

        while (true) {

            try {
                System.out.println("sent");

                session.set("time", String.valueOf(System.currentTimeMillis())).get();
                System.out.println(session.get("time").get());
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
