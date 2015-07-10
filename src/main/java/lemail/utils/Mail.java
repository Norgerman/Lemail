package lemail.utils;

import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 邮件的基础操作类
 * Created by sxf on 15-6-29.
 */
public class Mail {

    private String username;
    private String password;
    private String hostname;
    private String hostname_send;
    private Authenticator auth;
    Session session;
    Store store;
    public Mail(String _username, String _password, String _hostname, String _hostname_send) {
        this.username = _username;
        this.password = _password;
        this.hostname = _hostname;
        this.hostname_send = _hostname_send;

        auth = new MyAuthenticator(username,password);
    }

    public void GetMail() {

    }

    private class MyAuthenticator extends Authenticator {
        String userName = null;
        String password = null;

        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }

    /**
     * 发送一封邮件
     * @param subject 主题
     * @param content 内容
     * @param to      收件人列表
     * @throws MessagingException 异常情况
     */
    public void PostMail(String subject, String content, String... to)
            throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", hostname);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(true);
        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(username);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            addressTo[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(content, "text/html;charset=UTF-8");
        Transport.send(msg);
    }



    // 得到所有的邮件
    public Message[] getBox(String boxname) throws Exception {
        Properties props=System.getProperties();
        String protocol = hostname_send.split("\\.")[0];
        props.put("mail.store.protocol", protocol);
        props.put("mail.imap.host", hostname_send);
        session=Session.getInstance(props,null);
        // 打印出错误信息
        session.setDebug(true);

        store = session.getStore(protocol);
        store.connect(hostname_send, username, password);
        Folder folder = store.getFolder(boxname);
        if( folder==null )
            throw new Exception("No default folder");
        folder.open(Folder.READ_WRITE);
        Message[] msgs =folder.getMessages();
        List<Message> lists = new ArrayList<>();
        for (Message m : msgs) {
            if (!m.getFlags().contains(Flags.Flag.SEEN)) {
                lists.add(m);
            }
        }
        return lists.toArray(new Message[lists.size()]);
    }



}