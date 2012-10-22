package org.nikki.threadedsql;

/**
 * The interface that all DatabaseConfigurations implement to provide a new connection
 * 
 * @author Nikki
 *
 */
public interface DatabaseConfiguration {

	/**
	 * Create a new database connection
	 * 
	 * @return The new connection
	 */
	public DatabaseConnection newConnection();

}