package lemail.model;

import javax.persistence.*;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 用户的模型类
 * Created by sxf on 15-6-28.
 */
@Entity
@Table(name = "`user`")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Integer id;
    @Column(name = "`name`")
    private String name;
    @Column(name = "`role`")
    private String role;
    @Column(name = "`username`")
    private String username;
    @Column(name = "`password`")
    private String password;
    @Column(name = "`department_id`")
    private Integer department_id;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "default_checker")
    private User checker;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "readers")
    private Set<Inbox> mails = new LinkedHashSet<Inbox>();

    /**
     * 创建一个新用户，必须有这些信息
     *
     * @param username     用户名
     * @param password     密码
     * @param name         显示用姓名
     * @param role         角色信息，存入几个字符，M-manager管理员，D-dispatcher分发者，H-handler处理者，R-reviewer审阅者
     * @param departmentId 部门所属id，数据库中存放的是外键id，这里映射成了对象
     */

    public User(String username, String password, String name, String role, int departmentId) {
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = encode(password);
        this.department_id = departmentId;
    }

    public User() {
    }

    public boolean check_passwd(String passwd) {
        return this.password.equals(encode(passwd));
    }

    private static String encode(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = encode(password);
    }

    public Integer getDepartmentId() {
        return department_id;
    }

    public void setDepartmentId(Integer departmentId) {
        this.department_id = departmentId;
    }

    public User getChecker() {
        return checker;
    }

    public void setChecker(User checker) {
        this.checker = checker;
    }

    public Set<Inbox> getMails() {
        return mails;
    }

    public String toJson() {
        String str;
        int[] roles = parseRole();
        str = String.format("{\"id\":%d, \"username\":\"%s\", \"name\":\"%s\"," +
                        "\"roles\":{\"manager\":%d, \"dispatcher\":%d, \"handler\":%d, \"reviewer\":%d}," +
                        "\"checker\":%s}",
                id, username, name,
                roles[0], roles[1], roles[2], roles[3],
                formatChecker());
        return str;
    }


    public String toSimpleJson() {
        return String.format("{\"id\":%d,\"name\":\"%s\"}", id, name);
    }

    public boolean checkRole(String name) {
        return role.contains(name);
    }

    private int[] parseRole() {
        int[] roles = new int[4];
        for (int i = 0; i < 4; i++) {
            roles[i] = 0;
        }
        if (role.contains("M")) {
            roles[0] = 1;
        }
        if (role.contains("D")) {
            roles[1] = 1;
        }
        if (role.contains("H")) {
            roles[2] = 1;
        }
        if (role.contains("R")) {
            roles[3] = 1;
        }
        return roles;
    }

    private String formatChecker() {
        String str;
        if (checker == null)
            str = "null";
        else {
            str = checker.toSimpleJson();
        }
        return str;
    }
}
