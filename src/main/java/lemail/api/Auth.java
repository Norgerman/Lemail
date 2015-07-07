package lemail.api;

import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;


/**
 * 登录管理
 * Created by sxf on 15-6-28.
 */
public class Auth {

    public String username;
    public String password;

    /**
     * 登录方法
     */
    public String login() {
        User u = (User) DBSession.find_first(User.class,
                Restrictions.eq("username", username));
        if (u == null) {
            return Action.error(1000, "找不到用户");
        }
        if (u.check_passwd(password)) {
            Action.setSession("uid", u.getId());
            Action.setSession("role", u.getRole());
            Action.echojson(0, "success", u.toJson());
            return null;
        }
        return Action.error(1001, "密码错误");
    }

    /**
     * 登出
     */
    public String logout() {
        Action.setSession("uid", null);
        Action.setSession("role", null);
        Action.echojson(0, "success");
        return null;
    }

    /**
     * 获取当前登录用户的详细信息
     */
    public String getUser() {
        Integer uid = (Integer) Action.getSession("uid");
        if (uid == null) return Action.error(401, "用户未登录");
        User u = (User) DBSession.find_first(User.class,
                Restrictions.eq("id", uid));
        Action.echojson(0, "success", u.toJson());
        return null;
    }

    public String new_password;
    public String old_password;
    public String name;

    public String change() {
        Integer uid = (Integer) Action.getSession("uid");
        if (uid == null) return Action.error(401, "用户未登录");
        User u = (User) DBSession.find_first(User.class, Restrictions.eq("id", uid));
        Boolean changed = false;
        if (name != null) {
            u.setName(name);
            changed = true;
        }
        if (new_password != null) {
            if (u.check_passwd(old_password)) {
                u.setPassword(new_password);
                changed = true;
            } else
                return Action.error(403, "原密码不匹配");
        }
        if (changed) {
            Session s = DBSession.getSession();
            try {
                s.beginTransaction();
                s.update(u);
                s.getTransaction().commit();
            } catch (Exception ex) {
                return Action.error(-1, "未知错误");
            } finally {
                s.close();
            }
        }
        Action.echojson(0, "success");
        return null;
    }
}
