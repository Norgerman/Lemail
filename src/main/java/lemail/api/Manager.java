package lemail.api;

import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.AutoMail;

import lemail.model.Department;
import lemail.utils.Condition;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

/**
 * 管理员接口类
 * Created by XYN on 7/4/2015.
 */
public class Manager {

    public String department() {
        try {
            checkRole();
            List<Department> deps = DBSession.find_list(Department.class);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Department dep : deps) {
                sb.append(dep.toJson());
                sb.append(",");
            }
            if (sb.length() > 1) {
                sb.setCharAt(sb.length() - 1, ']');
            } else {
                sb.append("]");
            }
            Action.echojson(0, "success", sb.toString());
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        }
        return null;
    }


    public String username;
    public String password;
    public String hostname_smtp; // smtp
    public String hostname_imap; // imap

    /**
     * 设置邮箱配置信息
     */
    public String setConf() {
        try {
            checkRole();
            AutoMail.getInstance().setProp(
                    username,
                    password,
                    hostname_smtp,
                    hostname_imap
            );
            return Action.success();
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知错误");
        }
    }

    /**
     * 读取邮箱配置信息接口
     */
    public String getConf() {
        try {
            checkRole();
            AutoMail m = AutoMail.getInstance();
            String username = m.getUsername();
            String password = m.getPassword();
            String hostname_smtp = m.getHostname();
            String hostname_imap = m.getHostname_send();

            Action.echojson(0, "success",
                    String.format(
                            "{\"username\":\"%s\"," +
                                    "\"password\":\"%s\"," +
                                    "\"hostname_smtp\":\"%s\"," +
                                    "\"hostname_imap\":\"%s\"}",
                            username, password, hostname_smtp, hostname_imap));

            return null;
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Action.error(-1, "未知错误");
        }
    }

    /**
     * 注册新用户
     */

    public String name;
    public Integer department_id;
    public String role;
    public Integer default_checker;

    public String signUp() {
        User u = new User(username, password, name, role, department_id);
        if (default_checker != null) {
            u.setChecker((User) DBSession.find_first(User.class, Restrictions.eq("id", default_checker)));
        }
        Session s = DBSession.getSession();
        try {
            checkRole();
            s.beginTransaction();
            s.save(u);
            s.getTransaction().commit();
            Action.echojson(0, "success", u.toJson());
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            s.getTransaction().rollback();
            ex.printStackTrace();
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

    public Integer id;

    public String change() {
        try {
            checkRole();
            boolean changed = false;
            User u = (User) DBSession.find_first(User.class, Restrictions.eq("id", id));
            if (u == null) return Action.error(404, "用户不存在");
            if (password != null) {
                changed = true;
                u.setPassword(password);
            }
            if (name != null) {
                changed = true;
                u.setName(name);
            }
            if (role != null) {
                changed = true;
                u.setRole(role);
            }
            if (department_id != null) {
                changed = true;
                u.setDepartmentId(department_id);
            }
            if (default_checker != null) {
                changed = true;
                u.setChecker((User) DBSession.find_first(User.class, Restrictions.eq("id", default_checker)));
            }
            if (changed) {
                Session s = DBSession.getSession();
                try {
                    s.beginTransaction();
                    s.update(u);
                    s.getTransaction().commit();
                } finally {
                    s.close();
                }
            }
            Action.success();
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知错误");
        }
        return null;
    }

    public String depname;

    public String addDepartment() {
        Session s = DBSession.getSession();
        try {
            checkRole();
            Department dep = new Department(depname);
            s.beginTransaction();
            s.save(dep);
            s.getTransaction().commit();
            Action.success();
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知错误");
        } finally {
            s.close();
        }
        return null;
    }

    public Integer page;

    public String getUser() {
        try {
            checkRole();
            if (page == null)
                page = 0;
            Action.echojson(0, "success", getUserList("from User", page * 10, 10, null, false));
        } catch (ApiException e) {
            e.printStackTrace();
            return Action.error(e.getId(), e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Action.error(-1, "未知错误");
        }
        return null;
    }

    public String getChecker() {
        try {
            checkRole();
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

    private void checkRole() throws ApiException {
        Integer uid = (Integer) Action.getSession("uid");
        String role = (String) Action.getSession("role");
        if (uid == null) throw new ApiException(401, "用户未登录");
        if (role == null || !role.contains("M"))
            throw new ApiException(403, "用户缺少处理者权限");
    }
}
