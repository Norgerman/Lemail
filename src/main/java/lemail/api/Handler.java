package lemail.api;

import lemail.model.Inbox;
import lemail.model.Outbox;
import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.AutoMail;
import lemail.utils.Condition;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 处理者接口
 * Created by sxf on 15-7-3.
 */
public class Handler {

    public Integer page;

    /**
     * 获取用户的全部消息
     */
    public String getMessage() {

        return null;
    }


    public Integer id;

    public String getDetail() {
        try {
            checkUser();
            int uid = (Integer) Action.getSession("uid");
            Inbox mail = (Inbox) DBSession.find_first(
                    Inbox.class, Restrictions.eq("id", id));
            if (mail.getHandler() != null && mail.getHandler().getId() == uid &&
                    (mail.getState() == 2 || mail.getState() == 6)) {
                mail.setState(3);
                Session s = DBSession.getSession();
                s.beginTransaction();
                s.update(mail);
                s.getTransaction().commit();
            }
            Action.echojson(0, "success", mail.toJson(uid));
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        }
    }

    /**
     * 获取用户的未处理全部邮件
     */
    public String getNotHandle() {
        try {
            checkUser();
            int uid = (Integer) Action.getSession("uid");
            if (page == null)
                page = 0;
            Condition[] conditions = {new Condition("belong", "handler.id = :belong", uid),
                    new Condition("states", "state in (:states)", Arrays.asList(2, 3, 6))};
            Action.echojson(0, "success", formatInboxList(getList("from Inbox", page * 10, 10,
                    "order by date desc", conditions), 0, -1, conditions));
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        }
    }

    public String getAll() {
        try {
            checkUser();
            int uid = (Integer) Action.getSession("uid");
            User u = getUser();
            if (page == null)
                page = 0;
            Condition[] conditions = {new Condition("belong", "handler.id = :belong", uid)};
            List<Inbox> in = getList("from Inbox", page * 10, 10,
                    "order by date desc", conditions);
            in.addAll(u.getMails());
            Action.echojson(0, "success", formatInboxList(in, u.getMails().size(), uid, conditions));
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        }
    }

    public String getOutboxDetail() {
        try {
            checkUser();
            Outbox mail = (Outbox) DBSession.find_first(
                    Outbox.class, Restrictions.eq("id", id));
            if (mail == null)
                return Action.error(404, "对应邮件不存在");
            Action.echojson(0, "success", mail.toJson());
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        }
    }

    public String getOutbox() {
        try {
            checkUser();
            if (page == null)
                page = 0;
            int uid = (Integer) Action.getSession("uid");
            Action.echojson(0, "success", getOutboxList("from Outbox", page * 10, 10, "order by date desc", new Condition("sender", "sender.id = :sender", uid)));
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        }
    }

    public Boolean needreply;
    public String subject;
    public String content;
    public String to;
    public Integer checker_id;

    public String handleMail() {
        Session s = DBSession.getSession();
        try {
            checkUser();
            Inbox in = (Inbox) DBSession.find_first(Inbox.class, Restrictions.eq("id", id));
            if (in == null)
                return Action.error(404, "对应邮件不存在");
            if (needreply != null && !needreply && (in.isReview() == null || !in.isReview())) {
                s.beginTransaction();
                in.setState(7);
                s.update(in);
                s.getTransaction().commit();
            } else {
                Outbox o = new Outbox(subject, content, new Date(), to, getUser());
                if (in.isReview() != null && in.isReview()) {
                    User u = getUser();
                    if (checker_id == null)
                        o.setChecker(u.getChecker());
                    else
                        o.setChecker((User) DBSession.find_first(
                                User.class, Restrictions.eq("id", checker_id)));
                    in.setState(4);
                    o.setState(4);
                    o.setReply(in);
                } else {
                    in.setState(7);
                    o.setState(7);
                    o.setReply(in);
                    AutoMail.getInstance().post(subject, content, to.split(","));
                }
                o.setReply(in);
                s.beginTransaction();
                s.save(o);
                s.update(in);
                s.getTransaction().commit();
            }
            Action.echojson(0, "success");
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
    }

    public String postMail() {
        Session s = DBSession.getSession();
        try {
            checkUser();
            Outbox o = new Outbox(subject, content, new Date(), to, getUser());
            o.setState(7);
            AutoMail.getInstance().post(subject, content, to.split(","));
            s.beginTransaction();
            s.save(o);
            s.getTransaction().commit();
            Action.echojson(0, "success");
            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
    }

    public String reviseMail() {
        Session s = DBSession.getSession();
        try {
            checkUser();
            Outbox o = (Outbox) DBSession.find_first(Outbox.class, Restrictions.eq("id", id));
            if (o == null)
                return Action.error(404, "对应邮件不存在");
            if (o.getState() != 3)
                return Action.error(403, "此邮件不需要修改");
            o.setState(4);
            o.getReply().setState(4);
            o.setSubject(subject);
            o.setContent(content);
            s.beginTransaction();
            s.update(o);
            s.update(o.getReply());
            s.getTransaction().commit();
            return Action.success();
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
    }

    public String getChecker() {
        try {
            checkUser();
            page = 0;
            Action.echojson(0, "success", getUserList("from User", page * 10, Integer.MAX_VALUE, null, true, new Condition("role", "role like :role", "%R%")));
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知错误");
        }
        return null;
    }

    private String getUserList(String sql, int offset, int max, String order, boolean simple, Condition... conditions) {
        List<User> users = DBSession.executeSql(sql, offset, max, order, conditions);
        int count = DBSession.count("User", conditions);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"list\":[");
        for (User item : users) {
            if (!simple)
                sb.append(item.toJson());
            else
                sb.append(item.toSimpleJson());
            sb.append(',');
        }
        if (sb.length() > 9) {
            sb.setCharAt(sb.length() - 1, ']');
            sb.append(",");
        } else {
            sb.append("],");
        }
        sb.append("\"page\":");
        sb.append(page + 1);
        sb.append(String.format(",\"sum\":%d", count % 10 == 0 ? count / 10 : count / 10 + 1));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 获取用户
     */
    private User getUser() {
        Integer uid = (Integer) Action.getSession("uid");
        return (User) DBSession.find_first(
                User.class, Restrictions.eq("id", uid));
    }

    /**
     * 获取用户并检查
     *
     * @throws ApiException 报告用户未登录或无权限错误
     */
    private void checkUser() throws ApiException {
        Integer uid = (Integer) Action.getSession("uid");
        String role = (String) Action.getSession("role");
        if (uid == null) throw new ApiException(401, "用户未登录");
        if (!role.contains("H")) throw new ApiException(403, "用户缺少处理者权限");
    }

    private List<Inbox> getList(String sql, int offset, int max, String order, Condition... conditions) {
        return DBSession.executeSql(sql, offset, max, order, conditions);
    }

    private String formatInboxList(List<Inbox> inboxMails, int addition, int uid, Condition... conditions) {
        int count = DBSession.count("Inbox", conditions) + addition;
        StringBuilder sb = new StringBuilder();
        sb.append("{\"list\":[");
        inboxMails.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        for (Inbox itememail : inboxMails) {
            sb.append(itememail.toJsonNoData(uid));
            sb.append(',');
        }
        if (sb.length() > 9) {
            sb.setCharAt(sb.length() - 1, ']');
            sb.append(",");
        } else {
            sb.append("],");
        }
        sb.append("\"page\":");
        sb.append(page + 1);
        sb.append(String.format(",\"sum\":%d", count % 10 == 0 ? count / 10 : count / 10 + 1));
        sb.append("}");
        return sb.toString();
    }

    private String getOutboxList(String sql, int offset, int max, String order, Condition... conditions) {
        List<Outbox> outboxMails = DBSession.executeSql(sql, offset, max, order, conditions);
        int count = DBSession.count("Outbox", conditions);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"list\":[");
        for (Outbox itememail : outboxMails) {
            sb.append(itememail.toJsonNoData());
            sb.append(',');
        }
        if (sb.length() > 9) {
            sb.setCharAt(sb.length() - 1, ']');
            sb.append(",");
        } else {
            sb.append("],");
        }
        sb.append("\"page\":");
        sb.append(page + 1);
        sb.append(String.format(",\"sum\":%d", count % 10 == 0 ? count / 10 : count / 10 + 1));
        sb.append("}");
        return sb.toString();
    }
}
