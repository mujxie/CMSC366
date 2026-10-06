import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			
			con = DriverManager.getConnection("jdbc:postgresql://csci366.millersville.edu/a01pokemon_jxie", "jxie", "123456");
			
			if(con == null) {
				System.out.println("NOT working");
			}else {
				System.out.println("working");
			}
			
//			String sql = "INSERT INTO test VALUES(88, 'xie')";
//			
//			Statement stmt = con.createStatement();
//			stmt.executeUpdate(sql);
			
			
			
		}catch(Exception e) {
			System.out.println(e);
		}

	}

}
