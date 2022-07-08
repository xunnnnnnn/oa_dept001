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

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //获取cookie
        //这个cookie[]数组可能为null,如果不是null,数组长度一定大于0
        Cookie[] cookies = request.getCookies();
        String username = null;
        String pwd = null;

        if (cookies != null){
            for (Cookie cookie:cookies){
                String name = cookie.getName();
                if ("username".equals(name)){
                    username = cookie.getValue();
                }else if ("pwd".equals(name)){
                    pwd = cookie.getValue();
                }
            }
        }

        //使用username和pwd变量进行判断
        if (username != null && pwd != null){
            //验证用户名和密码是否一致,如果错误表示登录失败
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            Boolean success = false;

            try {
                conn = DBUtil.getConnection();
                String sql = "select * from tbl_user where username = ? and password = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1,username);
                ps.setString(2,pwd);
                rs = ps.executeQuery();
                if (rs.next()){
                    //登录成功
                    success = true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                DBUtil.close(conn,ps,rs);
            }

            if (success){
                //登录成功,获取session
                HttpSession session = request.getSession();
                User user = new User(username,pwd);
                session.setAttribute("user",user);

                response.sendRedirect(request.getContextPath() + "/dept/list");
            }else{
                //登录失败
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }

        }else {
            //跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
