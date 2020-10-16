package logic;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import gui.MainWindow;

public class Database {
	public Connection conn = null;
	private String framework = "embedded";
    private String protocol = "jdbc:derby:";
    public List<Table> tables= new ArrayList<Table>();


    public Database(String[] args) {
    	parseArguments(args);        
        try
        {
        	Properties props = new Properties(); // connection properties
            
            props.put("user", "JAC_DATA");
            props.put("password", "user1");
            
            String dbName = "JAC_DATA";
            this.conn = DriverManager.getConnection(protocol + dbName
                    + ";create=false", props);
            System.out.println("Connected to database " + dbName);
            
            this.conn.setAutoCommit(false);
            DatabaseMetaData dbmd = this.conn.getMetaData();
            ResultSet rs_tables = dbmd.getTables(null, "JAC_DATA", null, null);
            while (rs_tables.next()) {
                String strTableName = rs_tables.getString("TABLE_NAME");
                Table table = new Table();
                table.name = strTableName;
                ResultSet rs_cols = dbmd.getColumns(null, null, strTableName, null);
                while (rs_cols.next()) {
                    // 1: none
                    // 2: schema
                    // 3: table name
                    // 4: column name
                    // 5: length
                    // 6: data type (CHAR, VARCHAR, TIMESTAMP, ...)
                    table.addColumn(rs_cols.getString(4), rs_cols.getString(6), rs_cols.getString(5));
                    System.out.println("Adding column: " + rs_cols.getString(4) + " - " + rs_cols.getString(6) + " - " + rs_cols.getString(5));
                }
                rs_cols.close();                
                this.tables.add(table);
            }
            rs_tables.close();
            
            
	        
	    }
	    catch (SQLException sqle)
	    {
	        printSQLException(sqle);
	    }
    }
    
    public Table runQuery(String sql) {
    	sql = replaceParameters(sql);
    	System.out.println("Running query: " + sql);
    	Statement s;
        ResultSet rs = null;
        int rowCount = 0;
        Table table = new Table();
        try {
        	table.name = "temp";
	    	s = Database.this.conn.createStatement();
	        rs = s.executeQuery(sql);
	        ResultSetMetaData rsmd = rs.getMetaData();
	        int colCount = rsmd.getColumnCount();   
	        for (int ir = 1; ir <= colCount; ir++) {
	        	table.addColumn(rsmd.getColumnName(ir), Integer.toString(rsmd.getColumnType(ir)), Integer.toString(rsmd.getColumnDisplaySize(ir)));
	        }	       
            
	        while(rs.next()) {
	        	String[] values = new String[colCount];
	        	for (int ir = 1; ir <= colCount; ir++) {
	        		String res = rs.getString(ir);
	        		values[ir-1] = res;
	        		System.out.println("Value: " + values[ir-1]);
	        	}
	        	
	        	
	        	table.addRow(values);
	        	rowCount++;
	        }
	        Database.this.conn.commit();
	        s.close();
	    }
	    catch (SQLException sqle)
	    {
	        printSQLException(sqle);
	    }
        
        return table;
    }   
    public String replaceParameters(String str) {
    	String valString;
    	for (int i = 0; i < MainWindow.pol.polParameters.size(); i++) {
    		Policy.PolParam param = MainWindow.pol.polParameters.get(i);
    		if (param.paramType.equals("string_list") || param.paramType.equals("integer_list")) {
    			valString = "'" + String.join("', '", param.values) + "'";	
    		} else {
    			valString = param.values.get(0);
    		}
    		if (valString.length() > 0) { 
    			str = str.replace(param.paramName, valString);
    		}    		
    	}
    	return str;
    }
    private void closeDb() {
    	try {
            if (this.conn != null) {
            	this.conn.close();
            	this.conn = null;
            }
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }
    	try
        {
            // the shutdown=true attribute shuts down Derby
            DriverManager.getConnection("jdbc:derby:;shutdown=true");       
        }
        catch (SQLException se)
        {
            if (( (se.getErrorCode() == 50000)
                    && ("XJ015".equals(se.getSQLState()) ))) {
                System.out.println("Derby shut down normally");                    
            } else {               
                System.err.println("Derby did not shut down normally");
                printSQLException(se);
            }
        }
    }
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }
    private void parseArguments(String[] args)
    {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("derbyclient"))
            {
                framework = "derbyclient";
                protocol = "jdbc:derby://localhost:1527/";
            }
        }
    }

}
