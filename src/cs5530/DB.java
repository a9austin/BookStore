package cs5530;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.sql.ResultSetMetaData;


public class DB {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	private String address; 
  private String user;
	private String password;
	
	public DB()
	{
		address = "jdbc:mysql://georgia.eng.utah.edu/cs5530db01";
		user = "cs5530u01";
		password = "qf13a8js";
	}
	
	public int DB_Update(String query, boolean has_return_id) throws Exception
	{
		int result;
		try{
			// Connect to the database
			PreparedStatement st;
            Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(address, user, password);

			statement = connect.createStatement();
			if (has_return_id){
				st = connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				st.executeUpdate();
				ResultSet keys = st.getGeneratedKeys();
				keys.next();
				return keys.getInt(1);
				//result = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

			} else{
				result = statement.executeUpdate(query);
			}
			
		}
		catch (Exception e){
			throw e;
		} finally {
			DB_Close();
		}
		
		return result;
	}
	
		public String DB_Select(String query) throws Exception
		{
			String result = "";
			try{
				// Connect to the database
	            Class.forName("com.mysql.jdbc.Driver");
				connect = DriverManager.getConnection(address, user, password);

				statement = connect.createStatement();
				
				resultSet = statement.executeQuery(query);
				
				ResultSetMetaData rmd = resultSet.getMetaData();
				int col_count = rmd.getColumnCount();
				
				while (resultSet.next()) 
				{	
					for (int j = 1; j <= col_count; j++)
					{
						int type = rmd.getColumnType(j);
						if (type == Types.VARCHAR || type == Types.CHAR || type == Types.DATE || type == Types.TIMESTAMP){
			        		result += resultSet.getString(j);
			        	}
			        	else if (type == Types.INTEGER || type == Types.DECIMAL || type == Types.TINYINT){
			        		result += resultSet.getInt(j);
			        	}
			        	else if (type == Types.DOUBLE){
			        		result += resultSet.getDouble(j);
			        	}
			        	result += "|";
					}
				    result = result.substring(0, result.length()-1);
				    result += "#"; // Column Divder
				}
				if (!result.isEmpty()){
					result = result.substring(0, result.length()-1);
				}
			}
			catch (Exception e){
				throw e;
			} finally {
				DB_Close();
			}
			
			return result;
		}
	
	private void DB_Close(){
		try 
		{
	      if (resultSet != null) {
	        resultSet.close();
	      }

	      if (statement != null) {
	        statement.close();
	      }

	      if (connect != null) {
	        connect.close();
	      }
	    } 
		catch (Exception e) {

	    }
	}
	
}
