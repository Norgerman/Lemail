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
    public String name;
    public Integer department_id;
    public String role;
    public Integer default_checker;

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
        Action.echojson(0, "success", null);
        return null;
    }

    /**
     * 注册新用户
     */
    public String signup() {
        User u = new User(username, password, name, role, department_id);
        if (default_checker != null) {
            u.setDefaultChecker(default_checker);
        }

        Session s = DBSession.getSession();
        try {
            s.beginTransaction();
            s.save(u);
            s.getTransaction().commit();
            Action.setSession("uid", u.getId());
            Action.setSession("role", u.getRole());
            Action.echojson(0, "success", u.toJson());
        } catch (Exception ex) {
            s.getTransaction().rollback();
            if (ex instanceof ConstraintViolationException) {
                Action.error(1002, "用户已存在");
            } else {
                Action.error(-1, "未知错误");
            }
        } finally {
            s.close();
        }
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

}
