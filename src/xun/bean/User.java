package xun.bean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class User implements HttpSessionBindingListener {
    private String username;
    private String pwd;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        //用户登录了,User类型的对象在session中存储了
        ServletContext application = event.getSession().getServletContext();

        //获取在线人数
        Object onlinecount = application.getAttribute("onlinecount");
        if (onlinecount == null){
            application.setAttribute("onlinecount",1);
        }else {
            int count = (Integer) onlinecount;
            count++;
            application.setAttribute("onlinecount",count);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        //用户退出了,User类型的对象在session中删除了
        ServletContext application = event.getSession().getServletContext();

        int onlinecount = (Integer)application.getAttribute("onlinecount");
        onlinecount--;
        application.setAttribute("onlinecount",onlinecount);
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public User(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }
}
