package lemail.api;

import lemail.model.Outbox;
import lemail.utils.Action;
import lemail.utils.AutoMail;
import lemail.utils.Condition;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by XYN on 7/3/2015.
 */
public class Reviewer {

    public Integer page;

    public String getNotCheck() {
        try {
            check();
            int uid = (int) Action.getSession("uid");
            if (page == null)
                page = 0;
            Action.echojson(0, "success", getOutboxList("from Outbox", page * 10, page, "order by date desc",
                    new Condition("checker", "checker.id = :checker", uid),
                    new Condition("state", "state = :state", 4)));
        } catch (ApiException ex) {
            Action.error(ex.getId(), ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Action.error(-1, "未知异常");
            ex.printStackTrace();
        }
        return null;
    }

    public String getChecked() {
        try {
            check();
            int uid = (int) Action.getSession("uid");
            if (page == null)
                page = 0;
            Action.echojson(0, "success", getOutboxList("from Outbox", page * 10, page, "order by date desc",
                    new Condition("checker", "checker.id = :checker", uid),
                    new Condition("state", "state in (:state)", Arrays.asList(3, 7))));
        } catch (ApiException ex) {
            Action.error(ex.getId(), ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Action.error(0, "未知异常");
            ex.printStackTrace();
        }
        return null;
    }

    public Integer id;

    public String getDetail() {
        try {
            check();
            Outbox out = (Outbox) DBSession.find_first(Outbox.class, Restrictions.eq("id", id));
            if (out == null)
                return Action.error(404, "对应邮件不存在");
            Action.echojson(0, "success", out.toJson());
        } catch (ApiException ex) {
            Action.error(ex.getId(), ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Action.error(0, "未知异常");
            ex.printStackTrace();
        }
        return null;
    }

    public Boolean pass;

    public String checkMail() {
        try {
            check();
            Outbox out = (Outbox) DBSession.find_first(Outbox.class, Restrictions.eq("id", id));
            if (out == null)
                return Action.error(404, "对应邮件不存在");
            if (out.getState() != 4)
                return Action.error(403, "邮件已审核");
            if (pass) {
                out.setState(7);
                out.getReply().setState(7);
                Session s = DBSession.getSession();
                try {
                    s.beginTransaction();
                    s.update(out);
                    s.update(out.getReply());
                    s.getTransaction().commit();
                    AutoMail.getInstance().post(out.getSubject(), out.getContent(), out.getTo());
                } finally {
                    s.close();
                }
            } else {
                out.setState(3);
                out.getReply().setState(3);
                Session s = DBSession.getSession();
                try {
                    s.beginTransaction();
                    s.update(out);
                    s.update(out.getReply());
                    s.getTransaction().commit();
                } finally {
                    s.close();
                }
            }
            Action.success();
        } catch (ApiException ex) {
            Action.error(ex.getId(), ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Action.error(0, "未知异常");
            ex.printStackTrace();
        }
        return null;
    }

    private void check() throws ApiException {
        Integer uid = (Integer) Action.getSession("uid");
        String role = (String) Action.getSession("role");
        if (uid == null)
            throw new ApiException(401, "用户未登录");
        if (!role.contains("R"))
            throw new ApiException(403, "权限不足");
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
