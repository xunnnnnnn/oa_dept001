package xun.filter;

import jdk.nashorn.internal.ir.CallNode;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        //强转类型
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;

        //获取Servlet路径
        String servletPath = request.getServletPath();

        HttpSession session = request.getSession(false);
        if ("/index.jsp".equals(servletPath)  || "/welcome".equals(servletPath) ||
            "/user/login".equals(servletPath) || "/user/exit".equals(servletPath) ||
        (session != null && session.getAttribute("user") != null)){
            chain.doFilter(request,response);

        }else {
            //验证失败,跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }

    }
}
