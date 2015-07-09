package lemail.api;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import lemail.model.Inbox;
import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.AutoMail;
import lemail.utils.Condition;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lilelr on 2015/7/3.
 */
public class Dispatcher {
    public Integer page;

    public String getAll() {
        try {
            check();
            AutoMail.getInstance().Update();
            if (page == null)
                page = 0;
            Action.echojson(0, "success", getList("from Inbox", page * 10, 10, "order by date desc"));
        } catch (ApiException ex) {
            return Action.error(ex.getId(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        }
        return null;
    }

    public Integer id;

    public String showDetail() {
        Session s = DBSession.getSession();
        try {
            check();
            Inbox inbox = (Inbox) DBSession.find_first(Inbox.class, Restrictions.eq("id", id));
            if (inbox == null)
                return Action.error(404, "对应邮件不存在");
            if (inbox.getState() == 0 || inbox.getState() == 5)
                inbox.setState(1);
            s.beginTransaction();
            s.update(inbox);
            s.getTransaction().commit();
            Action.echojson(0, "success", inbox.toJson());
        } catch (ApiException ex) {
            return Action.error(ex.getId(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
        return null;
    }

    public Integer handler;
    public String readers;
    public Boolean review;

    public String dispatch() {
        Session s = DBSession.getSession();
        try {
            check();
            Inbox inbox = (Inbox) DBSession.find_first(Inbox.class, Restrictions.eq("id", id));
            if (inbox != null) {
                if (handler != null) {
                    if (inbox.getState() == 1) {
                        User handle = (User) DBSession.find_first(User.class, Restrictions.eq("id", handler));
                        inbox.setHandler(handle);
                        inbox.setReview(review);
                        inbox.setState(2);
                    } else {
                        Action.error(403, "邮件已分发");
                    }
                }
                if (readers != null) {
                    String[] rs = readers.split("\\|");
                    for (String r : rs) {
                        if (!r.isEmpty()) {
                            User u = (User) DBSession
                                    .find_first(User.class,
                                            Restrictions.eq("id", Integer.parseInt(r)));
                            inbox.getReaders().add(u);
                        }
                    }
                }
                s.beginTransaction();
                s.update(inbox);
                s.getTransaction().commit();
                Action.echojson(0, "success");
            } else {
                Action.error(404, "操作目标不存在");
            }
        } catch (ApiException ex) {
            return Action.error(ex.getId(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
        return null;
    }

    public String getHandlers() {
        Session s = DBSession.getSession();
        try {
            check();
            List<User> us = DBSession.executeSql("from User",0,Integer.MAX_VALUE,null,
                    new Condition("role", "role like :role", "%H%"));
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (User handler : us) {
                sb.append(handler.toSimpleJson());
                sb.append(',');
            }
            if (sb.length() > 1) {
                sb.setCharAt(sb.length() - 1, ']');
            } else {
                sb.append("]");
            }
            Action.echojson(0, "success", sb.toString());
        } catch (ApiException ex) {
            return Action.error(ex.getId(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        } finally {
            s.close();
        }
        return null;
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

    private void check() throws ApiException {
        Integer uid = (Integer) Action.getSession("uid");
        String role = (String) Action.getSession("role");
        if (uid == null)
            throw new ApiException(401, "用户未登录");
        if (!role.contains("D"))
            throw new ApiException(403, "权限不足");
    }
}
