package utils;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * JDBC工具类
 */
public class DBUtil {
    //静态变量：在类加载时执行
    //并且是有顺序的，自上而下的顺序
    //属性资源文件绑定
    private static ResourceBundle bundle = ResourceBundle.getBundle("resources.jdbc");
    //根据属性配置文件key
    private static String driver = bundle.getString("driver");
    private static String url = bundle.getString("url");
    private static String user = bundle.getString("user");
    private static String password = bundle.getString("password");
    static{
        try{
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取数据库连接对象
     * @return conn 连接对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        //连接数据库
        Connection conn = DriverManager.getConnection(url,user,password);
        return conn;
    }

    /**
     *
     * @param conn 连接对象
     * @param ps 数据库操作对象
     * @param rs 处理结果集对象
     */
    public static void close(Connection conn, Statement ps, ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (ps != null){
            try {
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
