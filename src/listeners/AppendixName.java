package listeners;


public class AppendixName {
       public String appendixName(String appendixName) {
        if (appendixName.equals("AK")) return "Appache-win";
        else if (appendixName.equals("K")) return "DB2-UNIX";
        else if (appendixName.equals("DB_AM")) return "IIS-WIN";
        else if (appendixName.equals("AN")) return "MQM-AIX";
        else if (appendixName.equals("DB07")) return "MSSQL-WIN";
        else if (appendixName.equals("AG")) return "ORACLE-UNIX";
        else if (appendixName.equals("DB10")) return "TOMCAT-WIN";
        else if (appendixName.equals("D")) return "IOS-OS";
        else if (appendixName.equals("V")) return "TSM-VIO";
        else if (appendixName.equals("DB05")) return "MQM-AIX";
        else if (appendixName.equals("AEA")) return "ASA-OS";
        else if (appendixName.equals("DN")) return "NXS-OS";
        else if (appendixName.equals("AP")) return "ESXiV";
        else return "appendix";
    }
}
