package PolicyPack;

import com.ibm.jac.CollectorV2;
import logic.Policy;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

public class Collector {

    public String allParams;
    public String toString() {
        return this.colId;
    }
    public List<String> messages = new ArrayList<String>();
    public class Table {
        public String name;

        public List<Column> columns = new ArrayList<Column>();
        public class Column {
            public String name;
            public String type;
        }
        public String toString() {
            return this.name;
        }
    }

    public Collector(String name,String tempPath) {
        this.filePath = tempPath + "/Collectors/" + name + ".jar";
        File f = new File(this.filePath);
        if (f.exists() && !f.isDirectory()) {
            // get collector information (relase, tables etc.)
            URL url = null;
            try {
                url = f.toURI().toURL();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String class_name = name;
            URLClassLoader child = new URLClassLoader(new URL[] {url}, Policy.class.getClassLoader());
            Class classToLoad;
            try {
                System.out.println("Loading class: " + class_name);
                classToLoad = Class.forName(class_name, true, child);
                Object result_release = null;
                String result_description = "";
                String result_os_join = "";
                String result_tables_string = "";
                String result_params_join = "";
                Object instance = classToLoad.newInstance();
                Method method = null;

                try {
                    method = classToLoad.getDeclaredMethod("getReleaseNumber");
                    result_release = method.invoke(instance);
                    this.realVersion = result_release.toString();
                    if ((this.minVersion == null) || (Integer.parseInt(this.minVersion) < Integer.parseInt(this.realVersion))) {
                        this.minVersion = this.realVersion;
                    }
                    method = classToLoad.getDeclaredMethod("getDescription");
                    result_description = method.invoke(instance).toString();

                } catch (NoSuchMethodException me){ result_description = "No getDescription method defined in collector."; }
                this.description = result_description;

                try {
                    method = classToLoad.getDeclaredMethod("getCompatibleOS");
                    String[] result_os = (String[]) method.invoke(instance);
                    result_os_join = String.join("; ", result_os);

                } catch (NoSuchMethodException me){ result_os_join = "No getCompatibleOS method defined in the collector."; }
                this.validOs = result_os_join;

                try {
                    method = classToLoad.getDeclaredMethod("getParameters");
                    Vector<String> result_params = (Vector<String>) method.invoke(instance);
                    result_params_join = String.join("; ", result_params);

                } catch (NoSuchMethodException me){ result_params_join = "No getParameters method defined in the collector."; }
                this.allParams = result_params_join;

                try {
                    method = classToLoad.getDeclaredMethod("getTables");
                    CollectorV2.CollectorTable[] result_tables = (CollectorV2.CollectorTable[]) method.invoke(instance);

                    for (int it = 0; it < result_tables.length; it++) {
                        Table table = new Table();
                        table.name = result_tables[it].getTableName();
                        System.out.println("Loading table: " + table.name);
                        Vector<CollectorV2.CollectorTable.Column> columns = result_tables[it].getColumns();
                        for (int a = 0; a < columns.size(); a++) {
                            Table.Column column = table.new Column();
                            column.name = columns.get(a).getName();
                            column.type = getType(columns.get(a).getType()) + "[" + columns.get(a).getSize() + "]";
                            table.columns.add(column);
                        }
                        this.tables.add(table);
                    }
                } catch (NoSuchMethodException me){ result_tables_string = "No getTables method defined in the collector."; }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            boolean mess = this.messages.add("Error: Collector " + name + " does not exist in Collectors folder. Add the file or remove collector.");
        }
    }

    public String getType(int typeNumber) {
        String result = "";
        switch (typeNumber) {
            case -7:
                result = "BIT";
                break;
            case -6:
                result = "TINYINT";
                break;
            case 5:
                result = "SMALLINT";
                break;
            case 4:
                result = "INTEGER";
                break;
            case -5:
                result = "BIGINT";
                break;
            case 6:
                result = "FLOAT";
                break;
            case 7:
                result = "REAL";
                break;
            case 8:
                result = "DOUBLE";
                break;
            case 2:
                result = "NUMERIC";
                break;
            case 3:
                result = "DECIMAL";
                break;
            case 1:
                result = "CHAR";
                break;
            case 12:
                result = "VARCHAR";
                break;
            case 91:
                result = "DATE";
                break;
            case 92:
                result = "TIME";
                break;
            case 93:
                result = "TIMESTAMP";
                break;
            case -2:
                result = "BINARY";
                break;
            case -3:
                result = "VARBINARY";
                break;
            case -4:
                result = "LONGVARBINARY";
                break;
            case 0:
                result = "NULL";
                break;
            case 1111:
                result = "OTHER";
                break;
            case 2000:
                result = "JAVA_OBJECT";
                break;
            case 2001:
                result = "DISTINCT";
                break;
            case 2002:
                result = "STRUCT";
                break;
            case 2003:
                result = "ARRAY";
                break;
            case 2004:
                result = "BLOB";
                break;
            case 2005:
                result = "CLOB";
                break;
            case 2006:
                result = "REF";
                break;
            case 70:
                result = "DATALINK";
                break;
            case 16:
                result = "BOOLEAN";
                break;
            case -8:
                result = "ROWID";
                break;
            case -15:
                result = "NCHAR";
                break;
            case -9:
                result = "NVARCHAR";
                break;
            case -16:
                result = "LONGNVARCHAR";
                break;
            case 2011:
                result = "NCLOB";
                break;
            case 2009:
                result = "SQLXML";
                break;
            case 2012:
                result = "REF_CURSOR";
                break;
            case 2013:
                result = "TIME_WITH_TIMEZONE";
                break;
            case 2014:
                result = "TIMESTAMP_WITH_TIMEZONE";
                break;
        }
        return result;
    }

    public String colId;
    public String classname;
    public String minVersion;
    public String realVersion;
    public String signature;
    public String validOs;
    public String description;
    public List<Table> tables = new ArrayList<Table>();
    public TreeMap<String, List<String>> parameters = new TreeMap<String, List<String>>();
    public String filePath;
}
