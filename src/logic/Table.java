package logic;

import org.apache.derby.impl.jdbc.LOBInputStream;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Table {
    public String name;
    public List<Column> columns = new ArrayList<Column>();
    public class Column {
        public String name;
        public String type;
        public String length;
    }
    public List<String[]> values = new ArrayList<String[]>();
    public void reset() {
        this.values = new ArrayList<String[]>();
        this.columns = new ArrayList<Column>();
    }
    public void addColumn(String name, String type, String length) {
        Column col = new Column();
        col.name = name;
        col.type = type;
        col.length = length;
        this.columns.add(col);
    }
    public boolean addRow(String[] values) {
        if (values != null) {
            if (values.length != this.columns.size()) {
                System.out.println("Column count doesnt match." + values[0].length() + " and " + this.columns.size());
                return false;
            }
        } else {
            System.out.println("No values found in the table.");
            return false;
        }
        this.values.add(values);
        return true;
    }
    // lets load all values for specific table
    public int loadValues(Database database) {
        reset();
        Statement s;
        ResultSet rs = null;
        int rowCount = 0;
        try {
            s = database.conn.createStatement();
            rs = s.executeQuery("SELECT * FROM " + this.name);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            for (int ir = 1; ir <= colCount; ir++) {
                this.addColumn(rsmd.getColumnName(ir), Integer.toString(rsmd.getColumnType(ir)), Integer.toString(rsmd.getColumnDisplaySize(ir)));
            }

            while(rs.next()) {
                String[] values = new String[colCount];
                for (int ir = 1; ir <= colCount; ir++) {
                    String res = rs.getString(ir);
                    values[ir-1] = res;
                    System.out.println("Value: " + values[ir-1]);
                }
                this.addRow(values);
                rowCount++;
            }
            s.close();
        }
        catch (SQLException sqle)
        {
            database.printSQLException(sqle);
        }
        return rowCount;
    }
    public String toString() {
        return this.name;
    }

    public int loadOtherTableValues(LoadDatabase database) {
        reset();
        Statement s;
        ResultSet rs = null;
        int rowCount = 0;
        try {
            s = database.conn.createStatement();
            rs = s.executeQuery("SELECT * FROM " + this.name);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            for (int ir = 1; ir <= colCount; ir++) {
                this.addColumn(rsmd.getColumnName(ir), Integer.toString(rsmd.getColumnType(ir)), Integer.toString(rsmd.getColumnDisplaySize(ir)));
            }

            while(rs.next()) {
                String[] values = new String[colCount];
                for (int ir = 1; ir <= colCount; ir++) {
                    String res = rs.getString(ir);
                    values[ir-1] = res;
                    System.out.println("Value: " + values[ir-1]);
                }
                this.addRow(values);
                rowCount++;
            }
            s.close();
        }
        catch (SQLException sqle)
        {
            database.printSQLException(sqle);
        }
        return rowCount;
    }
}