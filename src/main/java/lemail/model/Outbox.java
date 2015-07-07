package lemail.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 发件箱的模型类
 * Created by sxf on 15-6-28.
 */

@Entity
@Table(name = "`outbox`")
public class Outbox implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Integer id;
    @Column(name = "`subject`")
    private String subject;
    @Column(name = "`content`", columnDefinition = "MEDIUMTEXT")
    private String content;
    @Column(name = "`attachment`", columnDefinition = "TEXT")
    private String attachment;
    @Column(name = "`date`")
    private Date date;
    @Column(name = "`state`")
    private Integer state;
    @Column(name = "`to`")
    private String to;
    @Column(name = "`tag`")
    private String tag;
    @Column(name = "`sender_id`")
    private Integer sender_id;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "checker")
    private User checker;

    public Outbox(String subject, String content, Date date, String to, int sender_id) {
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.state = 0;
        this.to = to;
        this.sender_id = sender_id;
    }

    public Outbox() {

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public User getChecker() {
        return checker;
    }

    public void setChecker(User checker) {
        this.checker = checker;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public String toJson() {
        String str;
        String tmp_tag;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String tmp_attach = "null";
        if (tag == null) {
            tmp_tag = "null";
        } else {
            tmp_tag = "\"" + tag + "\"";
        }
        if (attachment != null) {
            tmp_attach = "\"" + attachment + "\"";
        }
        str = String.format("{\"id\":%d, \"subject\":\"%s\", \"content\":\"%s\"," +
                        "\"state\":%d, \"date\":\"%s\", \"attachment\":%s, \"to\":\"%s\"," +
                        "\"tag\":%s,\"checker\":%s}",
                id, subject, content.replaceAll("\\r?\\n", "\\\\n").replaceAll("\"", "\\\\\""), state, format.format(date), tmp_attach, to, tmp_tag, formatChecker());
        return str;
    }

    public String toJsonNoData() {
        String str;
        String tmp_tag;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        if (tag == null) {
            tmp_tag = "null";
        } else {
            tmp_tag = "\"" + tag + "\"";
        }
        str = String.format("{\"id\":%d, \"subject\":\"%s\", " +
                        "\"state\":%d, \"date\":\"%s\", \"to\":\"%s\"," +
                        "\"tag\":%s,\"checker\":%s}",
                id, subject, state, format.format(date), to, tmp_tag, formatChecker());
        return str;
    }

    private String formatChecker() {
        if (checker != null)
            return checker.toSimpleJson();
        else
            return "null";
    }
}
