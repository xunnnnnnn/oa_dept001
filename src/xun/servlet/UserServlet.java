package xun.servlet;

import utils.DBUtil;
import xun.bean.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet({"/user/login","/user/logout"})
public class UserServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if ("/user/login".equals(servletPath)){
            doLogin(request,response);
        }else if ("/user/logout".equals(servletPath)){
            doLogout(request,response);
        }
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null){
            //从session域中删除对象
            session.removeAttribute("user");

            session.invalidate();

            //销毁cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie : cookies){
                    String name = cookie.getName();
                    if ("username".equals(name) || "pwd".equals(name)){
                        cookie.setMaxAge(0);
                        cookie.setPath(request.getContextPath());
                        response.addCookie(cookie);
                    }
                }
            }
            //跳转到登录页面
            response.sendRedirect(request.getContextPath());
        }
    }


    protected void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();

        //获取参数
        String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");


        //标记
        Boolean flag = false;

        //连接数据库
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "select username,password from tbl_user where username = ? and password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,pwd);
            rs = ps.executeQuery();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,rs);
        }
        if (flag){
            //获取session对象
            HttpSession session = request.getSession();
            User user = new User(username,pwd);
            session.setAttribute("user",user);

            //登录成功且实现了十天免登陆功能后
            String f = request.getParameter("f");
            if ("1".equals(f)){
                //创建cookie对象存储登录名
                Cookie cookie = new Cookie("username",username);
                //创建cookie对象存储密码
                Cookie cookie2 = new Cookie("pwd",pwd);

                cookie.setMaxAge(60 * 60 * 24 * 10);
                cookie2.setMaxAge(60 * 60 * 24 * 10);

                //设置cookie path地址
                cookie.setPath(request.getContextPath());
                cookie2.setPath(request.getContextPath());

                //响应cookie给浏览器
                response.addCookie(cookie);
                response.addCookie(cookie2);

            }

            //登录成功
            response.sendRedirect(contextPath + "/dept/list");
        }else {
            //登录失败
            response.sendRedirect(contextPath + "/error.jsp");
        }
    }
}
