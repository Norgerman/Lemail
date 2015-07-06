package lemail.model;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 收件箱
 * Created by sxf on 15-6-28.
 */
@Entity
@Table(name = "`inbox`")
public class Inbox implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Integer id;
    @Column(name = "`subject`")
    private String subject;
    @Column(name = "`content`", columnDefinition = "LONGTEXT")
    private String content;
    @Column(name = "`date`")
    private Date date;
    @Column(name = "`attachment`", columnDefinition = "LONGTEXT")
    private String attachment;
    @Column(name = "`state`")
    private Integer state;
    @Column(name = "`from`")
    private String from;
    @Column(name = "`review`", columnDefinition = "TINYINT(1)")
    private Boolean review;
    @Column(name = "`tag`")
    private String tag;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "belong_user_id")
    private User handler;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "`user_inbox`",
            joinColumns = {@JoinColumn(name = "`inbox_id`")},
            inverseJoinColumns = {@JoinColumn(name = "`user_id`")})
    private Set<User> readers = new LinkedHashSet<User>();

    /**
     * 收件箱中的每一封邮件
     *
     * @param subject 主题
     * @param content 内容
     * @param date    时间
     * @param from    发件人
     */
    public Inbox(String subject, String content, Date date, String from) {
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.state = 0;
        this.from = from;
    }

    public Inbox() {

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean isReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public User getHandler() {
        return handler;
    }

    public void setHandler(User handler) {
        this.handler = handler;
    }

    public Set<User> getReaders() {
        return readers;
    }

    public String toJson() {
        String str;
        String tmp_tag;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String tmp_review = "null";
        String tmp_attach = "null";
        if (review != null && review) {
            tmp_review = "true";
        } else if (review != null && !review) {
            tmp_review = "false";
        }
        if (tag == null) {
            tmp_tag = "null";
        } else {
            tmp_tag = "\"" + tag + "\"";
        }
        if (attachment != null) {
            tmp_attach = "\"" + attachment + "\"";
        }
        str = String.format("{\"id\":%d, \"subject\":\"%s\", \"content\":\"%s\"," +
                        "\"state\":%d, \"date\":\"%s\", \"attachment\":%s, \"from\":\"%s\"," +
                        "\"review\":%s,\"tag\":%s,\"belong\":%s,\"readers\":%s}",
                id, subject, content.replaceAll("\\r?\\n", "\\\\n").replaceAll("\"","\\\\\""), state, format.format(date), tmp_attach, from, tmp_review, tmp_tag, formatHandler(), formatReaders());
        return str;
    }

    public String toJsonNoData() {
        String str;
        String tmp_tag;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String tmp_review = "null";
        if (review != null && review) {
            tmp_review = "true";
        } else if (review != null && !review) {
            tmp_review = "false";
        }
        if (tag == null) {
            tmp_tag = "null";
        } else {
            tmp_tag = "\"" + tag + "\"";
        }
        str = String.format("{\"id\":%d, \"subject\":\"%s\", " +
                        "\"state\":%d, \"date\":\"%s\", \"from\":\"%s\"," +
                        "\"review\":%s,\"tag\":%s,\"belong\":%s}",
                id, subject, state, format.format(date), from, tmp_review, tmp_tag, formatHandler());
        return str;
    }

    private String formatHandler() {
        String str;
        if (handler == null)
            str = "null";
        else {
            str = handler.toSimpleJson();
        }
        return str;
    }

    private String formatReaders() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (User reader : readers) {
            sb.append(reader.toSimpleJson());
            sb.append(',');
        }
        if (sb.length() > 1) {
            sb.setCharAt(sb.length() - 1, ']');
        } else {
            sb.append("]");
        }
        return sb.toString();
    }
}
