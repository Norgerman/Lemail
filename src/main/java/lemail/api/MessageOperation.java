package lemail.api;

import lemail.model.Message;
import lemail.model.Outbox;
import lemail.model.User;
import lemail.utils.Action;
import lemail.utils.DBSession;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;


/**
 * Created by yuxiao on 15/7/7.
 */
public class MessageOperation {

    public Integer from;
    public Integer to;
    public String content;
    public Integer mail_checked_id;

    public String send() {
        Session s = DBSession.getSession();
        try {
            check();
            Integer uid = (Integer) Action.getSession("uid");
            User fromUser = (User) DBSession.find_first(User.class, Restrictions.eq("id", uid));
            if (to != null) {
                User toUser = (User) DBSession.find_first(User.class, Restrictions.eq("id", to));
                if (toUser != null) {
                        if (content.isEmpty()) {
                            Action.error(404, "内容为空");
                        } else {
                            Message msg = new Message(fromUser, to, new Date(), content);
                            if(mail_checked_id!=null){
                                Outbox checkMail = (Outbox) DBSession.find_first(Outbox.class, Restrictions.eq("id", mail_checked_id));
                                if(checkMail!=null){
                                    msg.setMailCheckedId(mail_checked_id);
                                }else {
                                      Action.error(403, "要绑定的邮件不存在");
                                }
                            }
                            s.beginTransaction();
                            s.save(msg);
                            s.getTransaction().commit();
                            Action.echojson(0, "success");
                        }
                } else {
                    Action.error(402, "接受者不存在");
                }
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

    public String getmsgs(){
//        Session s = DBSession.getSession();
        try{
            check();
            Integer uid = (Integer) Action.getSession("uid");
            List<Message> msgList = DBSession.find_list(Message.class, Order.desc("date"),Restrictions.eq("to", uid));
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Message msg : msgList) {
                sb.append(msg.toJson());
                sb.append(',');
            }
            if (sb.length() > 1) {
                sb.setCharAt(sb.length() - 1, ']');
            } else {
                sb.append("]");
            }
            Action.echojson(0, "success", sb.toString());
        }catch (ApiException ex){
            return Action.error(ex.getId(), ex.getMessage());
        }catch (Exception ex){
            ex.printStackTrace();
            Action.error(-1, "未知异常");
        }
//        finally {
//            s.close();
//        }
        return null;
    }

    private void check() throws ApiException {
        Integer uid = (Integer) Action.getSession("uid");
        if (uid == null)
            throw new ApiException(401, "用户未登录");

    }
}
