package lemail.utils;

import lemail.model.Inbox;
import org.hibernate.*;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * 这是一个自动拉取邮件，并自动存储配置的类
 * Created by sxf on 15-7-3.
 */
public class AutoMail {

    private Mail mail = null;
    private static AutoMail autoMail = new AutoMail();

    private Date last;

    // 需要保障这三个字段中都不存在回车
    private String username = "";
    private String password = "";
    private String hostname = "";
    private String hostname_send = "";
    // 路径配置项
    private final String path = "save.prop";
    private final String attachment_path = "./attachment/";

    public AutoMail() {
        if (!load()) return;
        mail = new Mail(username, password, hostname, hostname_send);
    }

    public void setProp(String username, String password, String hostname, String hostname_send) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.hostname_send = hostname_send;
        mail = new Mail(username, password, hostname, hostname_send);
        save();
        try {
            Update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心更新方法，每次调用，都会向邮件客户端拉取邮件
     * 如果获取不到邮件，会抛出异常
     *
     * @throws Exception 无法获得邮件的信息
     */
    public void Update() throws Exception {
        Date now = new Date();
        if(last != null) {
            if (now.getTime() - last.getTime() < 600 * 1000) return;
            last = now;
        }
        else {
            last = new Date();
        }

        mail = new Mail(username, password, hostname, hostname_send);
        Message[] msgs = mail.getBox("INBOX");
        for (Message msg : msgs) {
            try {
                String content;
                StringBuilder sb = null;
                if (msg.getContentType().contains("multipart")) {
                    sb = new StringBuilder();
                    content = multipart(msg, sb);
                } else {
                    content = msg.getContent().toString();
                }
                Inbox in_msg = new Inbox(
                        msg.getSubject(),
                        content,
                        new Date(),
                        msg.getFrom()[0].toString()
                );
                if (sb != null)
                    in_msg.setAttachment(sb.toString());
                org.hibernate.Session s = DBSession.getSession();
                Transaction t = s.getTransaction();
                t.begin();
                s.save(in_msg);
                t.commit();
                msg.getFlags().add(Flags.Flag.SEEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void post(String subject, String content, String... to) throws MessagingException {
        mail.PostMail(subject, content, to);
    }

    private String multipart(Message msg, StringBuilder sb) throws IOException, MessagingException {
        Multipart multipart = (Multipart) msg.getContent();
        String content = null;
        int count = multipart.getCount();    // 部件个数
        for (int i = 0; i < count; i++) {
            BodyPart part = multipart.getBodyPart(i);
            // 单个部件类型
            String type = part.getContentType().split(";")[0];
            System.out.println(type);
            type = type.toLowerCase();
            if (type.contains("multipart")
                    || type.contains("text")) {
                content = part.getContent().toString();
            } else {
                // 文件名解码
                sb.append(MimeUtility.decodeText(part.getFileName())).append('|');
                UUID uuid = UUID.randomUUID(); // 创建uuid
                File f;
                while ((f = new File(attachment_path + uuid)).exists()) {
                    uuid = UUID.randomUUID();
                }
                sb.append(uuid).append('|');
                DataInputStream in = new DataInputStream(part.getInputStream());
                FileOutputStream out = new FileOutputStream(f);
                byte[] buffer = new byte[2048];
                int k;
                while ((k = in.read(buffer)) > 0) {
                    out.write(buffer, 0, k);
                }
                in.close();
                out.close();
            }
        }
        return content;
    }

    private void save() {
        File file = new File(path);
        try {
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file, false);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(username);
            writer.write("\n");
            writer.write(password);
            writer.write("\n");
            writer.write(hostname);
            writer.write("\n");
            writer.write(hostname_send);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean load() {
        File file = new File(path);
        if (!file.exists()) return false;
        try {
            FileInputStream in = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            username = br.readLine();
            password = br.readLine();
            hostname = br.readLine();
            hostname_send = br.readLine();
            br.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static AutoMail getInstance() {
        return autoMail;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public String getHostname_send() {
        return hostname_send;
    }
}
