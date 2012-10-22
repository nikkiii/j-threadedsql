package org.nikki.threadedsql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nikki.threadedsql.callback.ThreadedSQLCallback;
import org.nikki.threadedsql.mysql.MySQLDatabaseConfiguration;

/**
 * A simple Threaded SQL Example based off MySQL's employee test database
 * 
 * @author Nikki
 *
 */
public class Example {
	
	public static void main(String[] args) throws SQLException {
		//Configure the database
		MySQLDatabaseConfiguration config = new MySQLDatabaseConfiguration();
		config.setHost("localhost");
		config.setPort(3306);
		config.setUsername("test");
		config.setPassword("password");
		config.setDatabase("employees");
		
		//Initialize the instance
		final ThreadedSQL sql = new ThreadedSQL(config, 4);
		
		//Perform a standard query
		DatabaseConnection connection = sql.getConnectionPool().nextFree();
		Statement statement = connection.getConnection().createStatement();
		statement.execute("SELECT * FROM departments");
		ResultSet result = statement.getResultSet();
		while(result.next()) {
			System.out.println("Department [number="+result.getString(1)+", name="+result.getString(2)+"]");
		}
		result.close();
		
		//Perform a standard threaded query
		sql.executeQuery("SELECT * FROM departments", new ThreadedSQLCallback() {

			@Override
			public void queryComplete(ResultSet result) throws SQLException {
				while(result.next()) {
					System.out.println("Department [number="+result.getString(1)+", name="+result.getString(2)+"]");
				}
			}

			@Override
			public void queryError(SQLException e) {
				//This could be error handling code, or a simple stack trace
				e.printStackTrace();
			}
		
		});
		
		//Perform a PreparedStatement query
		//Note: You CAN reuse this object, but only AFTER queryComplete is called, since it might be called after you modify the data.
		final PreparedStatement prep = sql.prepareStatement("SELECT * FROM departments WHERE dept_name = ?");
		prep.setString(1, "Sales");
		
		//Execute the PreparedStatement
		sql.executeQuery(prep, new ThreadedSQLCallback() {

			@Override
			public void queryComplete(ResultSet result) throws SQLException {
				while(result.next()) {
					System.out.println("Prepared Department [number="+result.getString(1)+", name="+result.getString(2)+"]");
				}
			}

			@Override
			public void queryError(SQLException e) {
				//This could be error handling code, or a simple stack trace
				e.printStackTrace();
			}
			
		});
	}
	
}