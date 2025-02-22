package hudson.plugins.redmine.dao;

import hudson.plugins.redmine.RedmineAuthenticationException;
import hudson.plugins.redmine.RedmineUserData;
import hudson.plugins.redmine.util.Constants;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

/**
 * @author Yasuyuki Saito
 */
public class MySQLAuthDao extends AbstractAuthDao {

    @Override
    public void open(String dbServer, String port, String databaseName, String dbUserName, String dbPassword)
            throws RedmineAuthenticationException {
        try {
            String connectionString = String.format(Constants.CONNECTION_STRING_FORMAT_MYSQL, dbServer, port, databaseName);

            Class.forName(Constants.JDBC_DRIVER_NAME_MYSQL).newInstance();
            conn = DriverManager.getConnection(connectionString, dbUserName, dbPassword);
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Connection Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Connection Error", e);
        }
    }

    @Override
    public boolean isTable(String table) throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = "SHOW TABLES";
            state = conn.prepareStatement(query);
            results = state.executeQuery();
            
            while (results.next()) {
                if (results.getString(1).equals(table)) {
                    return true;
                }
            }

            return false;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Table Check Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Table Check Error", e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
            if (state != null) {
                try {
                    state.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public boolean isField(String table, String field) throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = String.format("SHOW FIELDS FROM %s", table);
            state = conn.prepareStatement(query);
            results = state.executeQuery();
            
            while (results.next()) {
                if (results.getString(1).equals(field)) {
                    return true;
                }
            }

            return false;
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Field Check Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Field Check Error", e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
            if (state != null) {
                try {
                    state.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public RedmineUserData getRedmineUserData(String loginTable, String userField, String passField, String saltField, String username)
            throws RedmineAuthenticationException {
        PreparedStatement state = null;
        ResultSet results = null;

        try {
            String query = String.format("SELECT * FROM %s WHERE %s = ?", loginTable, userField);

            state = conn.prepareStatement(query);
            state.setString(1, username);

            results = state.executeQuery();

            if (results.next()) {
                RedmineUserData userData = new RedmineUserData();
                userData.setUsername(results.getString(userField));
                userData.setPassword(results.getString(passField));

                if (!StringUtils.isBlank(saltField)) {
                    userData.setSalt(results.getString(saltField));
                }

                return userData;
            } else {
                return null;
            }
        } catch (RedmineAuthenticationException e) {
            throw e;
        } catch (SQLException e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } catch (Exception e) {
            throw new RedmineAuthenticationException("RedmineSecurity: Query Error", e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
            if (state != null) {
                try {
                    state.close();
                } catch (Exception e) {
                }
            }
        }
    }

}
