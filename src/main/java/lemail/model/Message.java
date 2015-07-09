package lemail.model;

import lemail.utils.DBSession;
import org.hibernate.criterion.Restrictions;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息类, 消息的状态有两种，0-未读， 1-已读
 * Created by sxf on 15-6-28.
 */
@Entity
@Table(name = "`message`")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Integer id;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "`from`")
    private User from;
    @Column(name = "`to`")
    private Integer to;
    @Column(name = "`date`")
    private Date date;
    @Column(name = "`content`", columnDefinition = "LONGTEXT")
    private String content;
    @Column(name = "`state`")
    private Integer state;
    @Column(name = "`mail_checked_id`")
    private Integer mail_checked_id;

    public Message(User from, Integer to, Date date, String content) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.content = content;
        this.state = 0;
    }

    public Message() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getMailCheckedId() {
        return mail_checked_id;
    }

    public void setMailCheckedId(Integer mailCheckedId) {
        this.mail_checked_id = mailCheckedId;
    }

    public String toJson() {
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return String.format("{\"id\":%d,\"to\":%d,\"from\":%s,\"date\":\"%s\"," +
                        "\"content\":\"%s\",\"state\":%d,\"mail\":%s}",
                id, to, from.toSimpleJson(),
                format.format(date), content, state, formatMail());
    }

    public String formatMail() {
        String str = "null";
        if (mail_checked_id != null) {
            Outbox o = (Outbox) DBSession.find_first(Outbox.class, Restrictions.eq("id", mail_checked_id));
            str = String.format("{\"id\":%d,\"subject\":\"%s\"}", o.getId(), o.getSubject());
        }
        return str;
    }
}
