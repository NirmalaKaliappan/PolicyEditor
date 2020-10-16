package logic;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import gui.DatabaseWindow;
import gui.MainWindow;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class LoadDatabase {
    public Connection conn = null;
    private String framework = "embedded";
    private String protocol = "jdbc:derby:";
    public List<Table> tables = new ArrayList<Table>();
    String databaseName = "JAC_DATA";


    public LoadDatabase(String[] args) {
        parseArguments(args);
        try {
            Properties props = new Properties(); // connection properties

            props.put("user", "JAC_DATA");
            props.put("password", "user1");
            String dbName = databaseName;

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
                    table.addColumn(rs_cols.getString(4), rs_cols.getString(6), rs_cols.getString(5));
                    System.out.println("Adding column: " + rs_cols.getString(4) + " - " + rs_cols.getString(6) + " - " + rs_cols.getString(5));
                }
                rs_cols.close();
                this.tables.add(table);
            }
            rs_tables.close();
        } catch (SQLException sqle) {

            System.out.println(sqle);
            printSQLException(sqle);
        }
    }

    public static void printSQLException(SQLException e) {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
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
            s = LoadDatabase.this.conn.createStatement();
            rs = s.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            for (int ir = 1; ir <= colCount; ir++) {
                table.addColumn(rsmd.getColumnName(ir), Integer.toString(rsmd.getColumnType(ir)), Integer.toString(rsmd.getColumnDisplaySize(ir)));
            }

            while (rs.next()) {
                String[] values = new String[colCount];
                for (int ir = 1; ir <= colCount; ir++) {
                    String res = rs.getString(ir);
                    values[ir - 1] = res;
                    System.out.println("Value: " + values[ir - 1]);
                }


                table.addRow(values);
                rowCount++;
            }
            this.conn.commit();
            s.close();
        } catch (SQLException sqle) {
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

    private void parseArguments(String[] args) {

        framework = "derbyclient";
        protocol = "jdbc:derby:";
        databaseName = args[0];

    }

    public void sqlEditorQuery(String run_command, JFrame frmPolicyEditor, JLabel panel,String violationMessage) throws SQLException {
        Properties props = new Properties(); // connection properties

        props.put("user", "JAC_DATA");
        props.put("password", "user1");

        String database = databaseName;
        this.conn = DriverManager.getConnection(protocol + database
                + ";create=false", props);

        Statement rs = this.conn.createStatement();
        String trimmed_string = run_command.trim();
        String[] splits = trimmed_string.split(Pattern.quote(" "));
        String split = splits[0].replaceAll("\\s", " ");
        String parseString = split;

        Statement st = null;
        ResultSet result = null;
        //*** to avoid extra internalframes in single tab***//

        Component[] comp = frmPolicyEditor.getContentPane().getComponents();
        for (int i = 0; i <= comp.length; i++) {
            if (i > 1)
                frmPolicyEditor.getContentPane().remove(comp[i - 1]);
        }
        JInternalFrame internalFrame_sqlEditor = new JInternalFrame("SQL RESULT", true, true, true, true);
        frmPolicyEditor.add(internalFrame_sqlEditor);
        JLabel lbl_result_sqlEditor = new JLabel();
        internalFrame_sqlEditor.add(lbl_result_sqlEditor);
        try {
            if (parseString.equalsIgnoreCase("INSERT")) {
                //PreparedStatement ps = conn.prepareStatement("Insert into AUTOSAVE values(?,?,?,?,?,?,?)");
                PreparedStatement ps = conn.prepareStatement(run_command);
           /* ps.setString(1, "db");
            ps.setString(2, "dbdatabase");
            ps.setString(3, "20201202");
            ps.setString(4, "sampledata");
            ps.setString(5, "violationSample");
            ps.setString(6, "normal");
            ps.setString(7, "progress");*/
                int insert_result = ps.executeUpdate();
                panel.setText("added" + insert_result);
                if (insert_result == 1)
                    lbl_result_sqlEditor.setText("Inserted");
                else
                    lbl_result_sqlEditor.setText("insertion Problem");
                internalFrame_sqlEditor.setVisible(true);
            } else if (parseString.equalsIgnoreCase("CREATE")) {
                st = conn.createStatement();
           /* String sql = "CREATE TABLE Autosave("
                    + " appendixname VARCHAR(255), "
                    + " policytitle VARCHAR(255), "
                    + " cpversion VARCHAR(255), "
                    + " description VARCHAR(255), "
                    + " viomessage VARCHAR(255), "
                    + " priority VARCHAR(255), "
                    + " cpstatus VARCHAR(255))";*/
                st.executeUpdate(run_command);
                panel.setText("table created");
                internalFrame_sqlEditor.add(panel);
                internalFrame_sqlEditor.setVisible(true);

            } else if (parseString.equalsIgnoreCase("drop") || (parseString.equalsIgnoreCase("alter"))) {
                st = conn.createStatement();
                st.executeUpdate(run_command);
                System.out.println("table deleted");
                panel.setText("table " + parseString + "ed");
                internalFrame_sqlEditor.add(panel);
                internalFrame_sqlEditor.setVisible(true);

            } else if (parseString.equalsIgnoreCase("UPDATE")) {

                st = conn.createStatement();
                int update_result = st.executeUpdate(run_command);
                if (update_result == 1) {
                    panel.setText("updated");
                } else {
                    panel.setText("update status failed");
                }

                internalFrame_sqlEditor.setVisible(true);
                internalFrame_sqlEditor.add(panel);

            } else if (parseString.equalsIgnoreCase("SELECT")) {
                st = conn.createStatement();
                result = st.executeQuery(run_command);
                ResultSetMetaData rsmd = result.getMetaData();
                Table table = new Table();
                int rowCount = 0;
                int colCount = rsmd.getColumnCount();
                for (int ir = 1; ir <= colCount; ir++) {
                    table.addColumn(rsmd.getColumnName(ir), Integer.toString(rsmd.getColumnType(ir)), Integer.toString(rsmd.getColumnDisplaySize(ir)));
                }
                while (result.next()) {
                    String[] values = new String[colCount];
                    for (int ir = 1; ir <= colCount; ir++) {
                        String res = result.getString(ir);
                        values[ir - 1] = res;
                        System.out.println("Value: " + values[ir - 1]);
                    }
                    table.addRow(values);
                    rowCount++;
                }
                conn.commit();
                DatabaseWindow dw = new gui.DatabaseWindow();
                JPanel result_panel = dw.getPanel(table,violationMessage);
                internalFrame_sqlEditor.add(result_panel);
                internalFrame_sqlEditor.setVisible(true);
            } else {
                System.out.println("given script is not correct sql script");
                JOptionPane.showMessageDialog(
                        frmPolicyEditor,
                        "error in sql script");
            }

            internalFrame_sqlEditor.addInternalFrameListener(new InternalFrameAdapter() {
                @Override
                public void internalFrameClosing(InternalFrameEvent e) {
                    internalFrame_sqlEditor.dispose();
                }

            });

            rs.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    frmPolicyEditor,
                    "error in sql script");
        }
    }
}
