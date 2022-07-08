package xun.servlet;

import utils.DBUtil;
import xun.bean.Dept;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/dept/list","/dept/detail","/dept/delete","/dept/save","/dept/modify"})
public class DeptServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //获取Servlet路径
        String servletPath = request.getServletPath();

        if ("/dept/list".equals(servletPath)){
            doList(request,response);
        }else if ("/dept/detail".equals(servletPath)){
            doDetail(request,response);
        }else if ("/dept/delete".equals(servletPath)){
            doDel(request,response);
        }else if ("/dept/save".equals(servletPath)){
            doSave(request,response);
        }else if ("/dept/modify".equals(servletPath)) {
                doModify(request, response);
            }
        }
    //部门修改
    private void doModify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //解决请求体的中文乱码问题
        request.setCharacterEncoding("UTF-8");

        //获取表单中的数据
        String deptno = request.getParameter("deptno");
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        //连接数据库执行更新语句
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "update dept set dname = ? , loc = ? where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,dname);
            ps.setString(2,loc);
            ps.setString(3,deptno);

            count = ps.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally{
            DBUtil.close(conn,ps,null);
        }
        if (count == 1){
            //数据提交成功
            response.sendRedirect(request.getContextPath()+"/dept/list");
        }
    }

    //部门保存
    private void doSave(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //获取部门信息
        //注意乱码问题
        request.setCharacterEncoding("UTF-8");

        String deptno = request.getParameter("deptno");
        String dname = request.getParameter("dname");
        String loc = request.getParameter("loc");

        //连接数据库执行insert语句
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "insert into dept(deptno,dname,loc) values (?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1,deptno);
            ps.setString(2,dname);
            ps.setString(3,loc);
            count = ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally{
            DBUtil.close(conn,ps,null);
        }
        if (count == 1) {
            //保存成功跳转到列表页面
            response.sendRedirect(request.getContextPath()+"/dept/list");
        }
    }

    //删除功能
    private void doDel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String deptno = request.getParameter("deptno");

        //连接数据库
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "delete from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,deptno);
            count = ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,null);
        }
        if (count == 1){
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/dept/list");
        }
    }

    //个人详细信息
    private void doDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dno = request.getParameter("dno");

        //创建包装类
        Dept dept = new Dept();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "select dname,loc from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,dno);
            rs = ps.executeQuery();
            if (rs.next()) {
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");

                dept.setDeptno(dno);
                dept.setDname(dname);
                dept.setLoc(loc);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,rs);
        }
        request.setAttribute("dept",dept);

       /* request.getRequestDispatcher("/detail.jsp").forward(request,response);*/

        String f = request.getParameter("f");
        if ("edit".equals(f)){
            request.getRequestDispatcher("/edit.jsp").forward(request,response);
        }else if ("detail".equals(f)){
            request.getRequestDispatcher("/detail.jsp").forward(request,response);
        }

    }

    //详细页面列表
    private void doList(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        List<Dept> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "select deptno,dname,loc from dept";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            //获取结果集
            while(rs.next()){
                String deptno = rs.getString("deptno");
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");

                //将以上的零散的数据类型封装成java类型
                Dept dept =new Dept();
                dept.setDeptno(deptno);
                dept.setDname(dname);
                dept.setLoc(loc);

                list.add(dept);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DBUtil.close(conn,ps,rs);
        }
        //将一个集合放到请求域当中
        request.setAttribute("deptList",list);

        //请求,不要重定向
        request.getRequestDispatcher("/list.jsp").forward(request,response);
    }
}
