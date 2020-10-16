package gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import logic.Database;
import logic.Table;


public class DatabaseWindow {
	public JPanel getPanel(Table table, String violation) {
		
		JPanel panel_database = new JPanel();
		panel_database.setLayout(new BorderLayout(0, 0));
		
		JTable table_data = new JTable();

		table_data.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		JScrollPane scrollPane_table = new JScrollPane(table_data);

		scrollPane_table.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table_data.setFillsViewportHeight(true);
		panel_database.add(scrollPane_table, BorderLayout.CENTER);
		loadGUITable(table, violation, table_data);		
		return panel_database;
	}
	public static class DBTableModel extends AbstractTableModel {
		private String[] columnNames;
		private Object[][] data;
		
		public DBTableModel(String[] columnNames, String[][] data) {
	    	this.columnNames = columnNames;
	    	this.data = data;
	    }			   

	    public int getColumnCount() {
	        return columnNames.length;
	    }

	    public int getRowCount() {
	        return data.length;
	    }

	    public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) {
	        return data[row][col];
	    }			    
	}
	public static String addSlashes(String s) {
		if (s != null) {
	        s = s.replaceAll("\\\\", "\\\\\\\\");
	        s = s.replaceAll("\\n", "\\\\n");
	        s = s.replaceAll("\\r", "\\\\r");
	        s = s.replaceAll("\\00", "\\\\0");
	        s = s.replaceAll("'", "\\\\'");
	        return s;
		} else { return ""; }
        
    }
	public void loadGUITable(Table table, String violation, JTable table_data) {
		//System.out.println("Loading database table");
		String[] columnNames = new String[table.columns.size()+1];
		columnNames[table.columns.size()] = "Violation Message";
		String[][] data = new String[table.values.size()][table.columns.size()+1];
        for (int cols = 0; cols < table.columns.size(); cols++) {
        	columnNames[cols] = table.columns.get(cols).name;
        	//System.out.println("Printing table name: " + columnNames[cols]);        	
        }
        
        for (int rows = 0; rows < table.values.size(); rows++) {
        	Pattern pattern = Pattern.compile("\\{(\\d+)\\}");
        	Matcher matcher = pattern.matcher(violation);        	
        	for (int rcol = 0; rcol < table.columns.size(); rcol++) {
        		data[rows][rcol] = table.values.get(rows)[rcol];
        		//System.out.println("Saving data: " + data[rows][rcol]);
        	}
        	String new_violation = "";
        	new_violation = violation;
        	while (matcher.find()) {
        		if (!matcher.group(1).equals("")) {
        			System.out.println("Replacing " + "{" + matcher.group(1) + "}" + " with " + data[rows][Integer.parseInt(matcher.group(1))]);
        			new_violation = new_violation.replaceFirst("\\{" + matcher.group(1) + "\\}", Matcher.quoteReplacement(data[rows][Integer.parseInt(matcher.group(1))]));        		
        			
        		}
            }
	        	
	        data[rows][table.columns.size()] = new_violation;
	        System.out.println("Violation: " + new_violation);
        	
        }			        
        DBTableModel model = new DBTableModel(columnNames, data);			  
        table_data.setModel(model);
        TableColumnModel columnModel = table_data.getColumnModel();
        if (columnModel.getColumnCount() > 20) {
        	table_data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
        	table_data.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
        for (int colm = 0; colm < columnModel.getColumnCount(); colm++) {
        	columnModel.getColumn(colm).setPreferredWidth(80);
        }
	}
	
}
