package lemail.api;

import lemail.model.Inbox;
import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.Condition;
import lemail.utils.DBSession;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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


    public String id;

    public String getInboxMail() {
        try {
            checkUser();
            Inbox mail = (Inbox) DBSession.find_first(
                    Inbox.class, Restrictions.eq("id", id));
            Action.echojson(0, "success", mail.toJson());
            return null;
        } catch (HandlerException e) {
            e.printStackTrace();
            return Action.error(e.id, e.getMessage());
        }
    }

    /**
     * 获取用户的全部邮件
     */
    public String getAll() {
        try {
            checkUser();
            int uid = (Integer) Action.getSession("uid");
            if (page == null)
                page = 0;
            Action.echojson(0, "success", getList("from Inbox", page * 10, 10, "order by date desc", new Condition("belong", "handler.id = :belong", uid)));
            return null;
        } catch (HandlerException e) {
            e.printStackTrace();
            return Action.error(e.id, e.getMessage());
        }
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
     * @throws HandlerException 报告用户未登录或无权限错误
     */
    private void checkUser() throws HandlerException {
        Integer uid = (Integer) Action.getSession("uid");
        String role = (String) Action.getSession("role");
        if (uid == null) throw new HandlerException(401, "用户未登录");
        if (!role.contains("H")) throw new HandlerException(500, "用户缺少处理者权限");
    }

    private String getList(String sql, int offset, int max, String order, Condition... conditions) {
        List<Inbox> inboxMails = DBSession.executeSql(sql, offset, max, order, conditions);
        int count = DBSession.count("Inbox", conditions);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"list\":[");
        for (Inbox itememail : inboxMails) {
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

    private class HandlerException extends Exception {
        int id;

        public HandlerException(int id, String message) {
            super(message);
            this.id = id;
        }
    }
}
