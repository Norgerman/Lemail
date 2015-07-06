package lemail.model;

import junit.framework.TestCase;

import java.util.Date;

public class InboxTest extends TestCase {


    public void testToJson() throws Exception {
        Inbox inbox = new Inbox("afajweiof", "fjaiewofj\nfjiaoewfj\nfjeiwaofj\n",new Date(), "fjiaoe");

        System.out.println(inbox.toJson());
    }
}