import java.sql.Connection;
import java.sql.DriverManager;

public class GetConnection
{

    protected Connection getConn(){
        final String DB_URL = "jdbc:mariadb://localhost:3307/book_library";
        final String USERNAME = "root";
        final String PASSWORD = "password";
        String myDriver = "org.mariadb.jdbc.Driver";
        Connection conn = null;

        try{
            Class.forName(myDriver);

            conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);

        }catch (Exception e){//connection failed

            e.printStackTrace();
        }

        return conn;
    }

}
