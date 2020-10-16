package logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import PolicyPack.Collector;
import PolicyPack.Rule;
import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
//import com.ibm.*;
import main.FileUtil;

public class Policy {

    public static class PolParam {
        public String paramName;
        public String description;
        public String paramType;
        public List<String> values = new ArrayList<String>();

        public String toString() {
            return this.paramName;
        }
    }
    public boolean isChanged = false;

    public String benchmarkId;
    public String statusDate;
    public String title;
    public String description;
    public TreeMap<Integer, String> versions = new TreeMap<Integer, String>(Comparator.reverseOrder());
    public List<PolParam> polParameters = new ArrayList<PolParam>();
    public List<Rule> rules = new ArrayList<Rule>();
    public List<Collector> collectors = new ArrayList<Collector>();
    public String tempPath;
    public String polFile;
    public List<String> messages = new ArrayList<String>();

    public void removeCollector(Collector col, boolean allInstances) {
        List<Integer> toRemove = new ArrayList<Integer>();
        int toRemoveSingle = -1;
        boolean isSingle = false;

        for (int icol = 0; icol < this.collectors.size(); icol++) {
            if (col.classname.equals(this.collectors.get(icol).classname)) {
                toRemove.add(icol);
                //System.out.println("Adding index for removal: " + icol);
            }
            if (col.colId.equals(this.collectors.get(icol).colId)) {
                toRemoveSingle = icol;
                //System.out.println("Adding index for removal: " + icol + "; size is: " + toRemove.size());
            }
        }
        if (toRemove.size() > 1) {
            isSingle = false;
        } else {
            isSingle = true;
        }
        //System.out.println("toRemove size is: " + toRemove.size());
        File colFile = new File(col.filePath);

        if (Boolean.TRUE.equals(allInstances)) {
            for (int i = 0; i < toRemove.size(); i++) {
                this.collectors.remove(toRemove.get(i).intValue());
                //System.out.println("Removing index: " + toRemove.get(i));
            }
            if (colFile.exists()) {
                colFile.delete();
            }
            this.addVersionNote("Deleted collector " + col.classname + " - version " + col.realVersion + " - deleted .jar file.");
        } else {
            this.collectors.remove(toRemoveSingle);
            //System.out.println("Removing index: " + toRemove.get(0));
            if (isSingle == true) {
                //System.out.println("Deleting file as its single");
                if (colFile.exists()) {
                    colFile.delete();
                    this.addVersionNote("Deleted collector " + col.classname + " - version " + col.realVersion + " - deleted .jar file.");
                }
            } else {
                this.addVersionNote("Deleted collector instance " + col.colId + ".");
            }

        }

    }

    public int load(String file) throws JDOMException, IOException {
        if (!new File(file).exists()) {
            return -1;
        }
        Unzipper unzipper = new Unzipper(file, null);
        String polDir = unzipper.unzip();
        this.tempPath = polDir;
        this.polFile = file;
        String xmlSource = polDir + File.separator + "policy.xml";
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(xmlSource);
        Element policy = jdomDocument.getRootElement();

        Namespace cdf = Namespace.getNamespace("cdf", "http://checklists.nist.gov/xccdf/1.0");
        Namespace scm = Namespace.getNamespace("scm", "?");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        policy.addNamespaceDeclaration(cdf);
        policy.addNamespaceDeclaration(scm);
        policy.addNamespaceDeclaration(xsi);


        List<Element> metas = policy.getChildren();
        for (int i = 0; i < metas.size(); i++) {
            Element meta = metas.get(i);
            String metaName = meta.getName();
            //read title of the policy
            if (metaName.equalsIgnoreCase("title")) {
                this.title = meta.getValue();
                //System.out.println(metaName + " = " + this.title);
            }
            //read description
            if (metaName.equalsIgnoreCase("description")) {
                this.description = meta.getValue();
                //System.out.println(metaName + " = " + this.description);
            }
            //read status date
            if (metaName.equalsIgnoreCase("status")) {
                this.statusDate = meta.getAttributeValue("date");
                //System.out.println(metaName + " = " + this.statusDate);
            }

            //read version history from XML file
            if (metaName.equalsIgnoreCase("versions")) {
                List<Element> versions = meta.getChildren();
                if (versions.size() > 0) {
                    for (int a = 0; a < versions.size(); a++) {
                        Element version = versions.get(a);
                        int number = version.getAttribute("id").getIntValue();
                        this.versions.put(number, version.getValue());
                        //System.out.println(number + " - " + version.getValue());
                    }
                }
            }

            //read policy parameters
            if (metaName.equalsIgnoreCase("Profile")) {
                List<Element> polParams = meta.getChildren();
                if (polParams.size() > 0) {
                    for (int p = 0; p < polParams.size(); p++) {

                        PolParam parameterObj = new PolParam();
                        List<String> paramList = new ArrayList<String>();
                        Element polParam = polParams.get(p);
                        String paramId = polParam.getAttributeValue("id");
                        if (paramId == null) {
                            continue;
                        }
                        String paramType = polParam.getAttributeValue("type", scm);
                        // get policy parameters
                        List<Element> paramValues = polParam.getChildren();
                        if (paramValues.size() > 0) {
                            for (int c = 0; c < paramValues.size(); c++) {
                                Element polValue = paramValues.get(c);
                                if (polValue.getName().equalsIgnoreCase("description")) {
                                    parameterObj.description = polValue.getValue();
                                }
                                // get value element
                                if (polValue.getName().equalsIgnoreCase("default")) {
                                    String valueList = polValue.getValue();
                                    String[] split_values = valueList.split(",");
                                    //get all values
                                    for (int sp = 0; sp < split_values.length; sp++) {
                                        String split_value = split_values[sp];
                                        if (split_value.length() > 0) {
                                            if (split_value.startsWith("'")) {
                                                split_value = split_value.substring(1, split_value.length() - 1);
                                            }
                                            paramList.add(split_value);
                                            System.out.println("Par value: " + split_value);
                                        }

                                    }
                                }
                            }
                        }
                        parameterObj.paramName = paramId;
                        parameterObj.paramType = paramType;
                        parameterObj.values = paramList;
                        this.polParameters.add(parameterObj);
                        //System.out.println("Param value: " + parameterObj.paramType + ", param name: " + parameterObj.paramName);


                        //System.out.println(number + " - " + version.getValue());
                    }
                }
            }
            int rule_count = 0;
            // here check for <cdf:Group id="SCM_Policy"> and start with policy checks
            if (metaName.equalsIgnoreCase("Group")) {

                List<Element> check_children = meta.getChildren();
                for (int ch = 0; ch < check_children.size(); ch++) {
                    Element rule = check_children.get(ch);
                    if (rule.getName().equalsIgnoreCase("Rule")) {
                        rule_count++;
                        List<Element> rule_children = rule.getChildren();
                        Rule check = new Rule();
                        check.ruleId = Integer.toString(rule_count);
                        for (int r = 0; r < rule_children.size(); r++) {
                            // now we finally go trough rule properties (title, sql etc)
                            Element checkChild = rule_children.get(r);

                            String checkChildName = checkChild.getName();

                            if (checkChildName.equalsIgnoreCase("requires")) {
                                check.requires.add(checkChild.getAttributeValue("idref"));
                            }
                            if (checkChildName.equalsIgnoreCase("title")) {
                                check.title = checkChild.getValue();
                            }
                            if (checkChildName.equalsIgnoreCase("description")) {
                                check.description = checkChild.getValue();
                            }
                            if (checkChildName.equalsIgnoreCase("violationMessage")) {
                                check.violationMessage = checkChild.getValue();
                                if (!check.violationMessage.startsWith("Violation: ")) {
                                    this.messages.add("Warning: Check " + check.title + ": violation message does not start with 'Violation: '.");
                                }
                                if (check.violationMessage.contains("Instance") || check.violationMessage.contains("instance")) {
                                    if (!check.violationMessage.matches(".*Instance .+: .*")) {
                                        this.messages.add("Warning: Check " + check.title + ": fix instance string in violation message.");
                                    }
                                }
                            }
                            if (checkChildName.equalsIgnoreCase("priority")) {
                                check.priority = checkChild.getValue();
                            }
                            if (checkChildName.equalsIgnoreCase("versions")) {
                                //System.out.println("Versions found.");
                                List<Element> ruleVersions = checkChild.getChildren();
                                if (ruleVersions.size() > 0) {
                                    //System.out.println("Versions found more than 0.");
                                    for (int z = 0; z < ruleVersions.size(); z++) {
                                        Element ruleVersion = ruleVersions.get(z);
                                        int number = ruleVersion.getAttribute("id").getIntValue();
                                        check.versions.put(number, ruleVersion.getValue());
                                        //System.out.println("Saving version: " + number + " - " + ruleVersion.getValue());
                                    }
                                }
                            }

                            if (checkChildName.equalsIgnoreCase("check")) {
                                List<Element> sqlChildren = checkChild.getChildren();
                                for (int q = 0; q < sqlChildren.size(); q++) {
                                    Element sqlChild = sqlChildren.get(q);
                                    if (sqlChild.getName().equalsIgnoreCase("sql")) {
                                        check.sql = sqlChild.getValue().trim();
                                    }
                                }
                            }
                        }
                        // add check here
                        this.rules.add(check);
                        if (check.versions.isEmpty()) {
                            //System.out.println("Creating new CP version.");
                            check.addVersion("Initial control point version.");
                        }
                        //System.out.println("Added check to policy: " + check.title);
                    }
                    if (rule.getName().equalsIgnoreCase("collector")) {
                        List<Element> col_children = rule.getChildren();
                        Collector collector = new Collector(rule.getAttributeValue("classname", scm), tempPath);
                        collector.colId = rule.getAttributeValue("id");
                        collector.classname = rule.getAttributeValue("classname", scm);
                        collector.minVersion = rule.getAttributeValue("minimumVersion", scm);
                        for (int w = 0; w < col_children.size(); w++) {
                            Element colChild = col_children.get(w);
                            String colChildName = colChild.getName();
                            if (collector.tables.isEmpty()) {
                                if (colChildName.equalsIgnoreCase("maxAge")) {
                                    Collector.Table table = collector.new Table();
                                    table.name = colChild.getAttributeValue("tablename", scm);
                                    collector.tables.add(table);
                                }
                            }
                            // save collector parameters
                            if (colChildName.equalsIgnoreCase("parameter")) {
                                List<String> currentParams = collector.parameters.get(colChild.getAttributeValue("id"));
                                if (currentParams != null) {
                                    currentParams.add(colChild.getValue());
                                    collector.parameters.put(colChild.getAttributeValue("id"), currentParams);
                                } else {
                                    currentParams = new ArrayList<String>();
                                    currentParams.add(colChild.getValue());
                                    collector.parameters.put(colChild.getAttributeValue("id"), currentParams);
                                }
                                //System.out.println("parameter: " + colChild.getAttributeValue("id") + " = " + colChild.getValue());
                            }
                        }
                        this.collectors.add(collector);
                    }
                }
            }
        }
        if (this.versions.isEmpty()) {
            this.addVersion("Initial version.");
        }
        for (int i = 0; i < this.messages.size(); i++) {
            System.out.println(this.messages.get(i));
        }
        Collections.sort(this.collectors, new Comparator<Collector>() {
            public int compare(Collector o1, Collector o2) {
                return o1.colId.compareTo(o2.colId);
            }
        });
        return 1;
    }

    public void build(String saveas_file) throws JDOMException, IOException {
        Namespace cdf = Namespace.getNamespace("cdf", "http://checklists.nist.gov/xccdf/1.0");
        Namespace scm = Namespace.getNamespace("scm", "?");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");


        Element benchmark = new Element("Benchmark", cdf);
        Document policyxml = new Document(benchmark);
        benchmark.setAttribute("id", this.title);

        benchmark.addNamespaceDeclaration(cdf);
        benchmark.addNamespaceDeclaration(scm);
        benchmark.addNamespaceDeclaration(xsi);

        Element status = new Element("status", cdf);
        status.setAttribute("date", (new Date()).toString());
        policyxml.getRootElement().addContent(status);

        Element pol_title = new Element("title", cdf);
        pol_title.setText(this.title);
        policyxml.getRootElement().addContent(pol_title);

        if (!this.description.contains("PVERSION")) {
            if (!this.versions.isEmpty()) {
                this.description = this.description + "\n" + "PVERSION=" + this.versions.firstEntry().getKey();
            }
        } else {
            System.out.println("Adding version to description: " + "PVERSION=" + this.versions.firstEntry().getKey());
            this.description = this.description.replaceFirst("PVERSION.*", "PVERSION=" + this.versions.firstEntry().getKey());
        }
        Element description = new Element("description", cdf);
        description.setAttribute("messageID", "PolicyDescription", scm);
        description.setText(this.description);
        policyxml.getRootElement().addContent(description);

        Element maximumCollectorDataAge = new Element("maximumCollectorDataAge", scm);
        maximumCollectorDataAge.setText("0");
        policyxml.getRootElement().addContent(maximumCollectorDataAge);

        Element version = new Element("version", cdf);
        policyxml.getRootElement().addContent(version);

        Element versions = new Element("versions", cdf);
        Iterator it = this.versions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String date_string = pair.getKey().toString();
            Element version_single = new Element("version", cdf);
            version_single.setAttribute("id", date_string);
            version_single.setText(pair.getValue().toString());
            versions.addContent(version_single);
        }
        policyxml.getRootElement().addContent(versions);

        Element profile = new Element("Profile", cdf);
        profile.setAttribute("id", "default");
        policyxml.getRootElement().addContent(profile);

        Element select = new Element("select", cdf);
        select.setAttribute("idref", "SCM_Policy");
        select.setAttribute("selected", "1");
        profile.addContent(select);

        for (int i = 0; i < this.polParameters.size(); i++) {
            Element value = new Element("Value", cdf);
            //System.out.println("Repeeat: " + i);
            value.setAttribute("id", this.polParameters.get(i).paramName);
            value.setAttribute("type", this.polParameters.get(i).paramType, scm);
            profile.addContent(value);

            Element description_value = new Element("description", cdf);
            value.addContent(description_value);

            Element default_value = new Element("default", cdf);
            String all_values = "";
            for (int a = 0; a < this.polParameters.get(i).values.size(); a++) {
                String curr_value = this.polParameters.get(i).values.get(a);
                if (this.polParameters.get(i).paramType.equals("string_list")) {
                    curr_value = "'" + curr_value + "'";
                }
                if (a == 0) {
                    all_values = curr_value;
                    //System.out.println("Adding first value.");
                } else {
                    all_values = all_values + "," + curr_value;
                    //System.out.println("Adding value with comma.");
                }
                System.out.println("current vlaues: " + all_values);
            }
            default_value.setText(all_values);
            value.addContent(default_value);
        }
        // END OF PROFILE
        Element group = new Element("Group", cdf);
        group.setAttribute("id", "SCM_Policy");
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule_obj = this.rules.get(i);
            //System.out.println("Adding rule: " + rule_obj.title);
            Element rule = new Element("Rule", cdf);
            rule.setAttribute("id", rule_obj.ruleId);
            for (int ia = 0; ia < rule_obj.requires.size(); ia++) {
                Element requires = new Element("requires", cdf);
                requires.setAttribute("idref", rule_obj.requires.get(ia));
                requires.setText("");
                rule.addContent(requires);
            }
            Element rule_title = new Element("title", cdf);
            rule_title.setText(rule_obj.title);
            rule.addContent(rule_title);
            Element rule_description = new Element("description", cdf);
            rule_description.setText(rule_obj.description);
            rule.addContent(rule_description);
            Element rule_violation = new Element("violationMessage", scm);
            rule_violation.setText(rule_obj.violationMessage);
            rule.addContent(rule_violation);
            Element rule_priority = new Element("priority", scm);
            rule_priority.setText(rule_obj.priority);
            rule.addContent(rule_priority);

            Element rule_check = new Element("check", cdf);
            rule_check.setAttribute("system", "TivoliSecurityComplianceManager");
            Element rule_sql = new Element("sql", scm);
            rule_sql.setText(rule_obj.sql);
            rule_check.addContent(rule_sql);
            rule.addContent(rule_check);

            Element rule_versions = new Element("versions", cdf);

            Iterator it_ruleversions = rule_obj.versions.entrySet().iterator();
            while (it_ruleversions.hasNext()) {
                Map.Entry pair_versions = (Map.Entry) it_ruleversions.next();
                Element rule_version = new Element("version", cdf);
                rule_version.setAttribute("id", pair_versions.getKey().toString());
                rule_version.setText(pair_versions.getValue().toString());
                rule_versions.addContent(rule_version);
            }
            rule.addContent(rule_versions);
            group.addContent(rule);

        }
        for (int ic = 0; ic < this.collectors.size(); ic++) {
            Collector col = this.collectors.get(ic);
            Element collector = new Element("collector", scm);
            collector.setAttribute("id", col.colId);
            collector.setAttribute("classname", col.classname, scm);
            collector.setAttribute("minimumVersion", col.minVersion, scm);


            for (int im = 0; im < col.tables.size(); im++) {
                Element col_maxage = new Element("maxAge", scm);
                col_maxage.setAttribute("tablename", col.tables.get(im).name, scm);
                col_maxage.setText("720");
                collector.addContent(col_maxage);
            }
            Element col_schedule = new Element("schedule", scm);
            Element col_monthofyear = new Element("monthOfYear", scm);
            col_monthofyear.setAttribute("random", "false");
            col_monthofyear.setText("xxxxxxxxxxxx");
            col_schedule.addContent(col_monthofyear);

            Element col_dayofmonth = new Element("dayOfMonth", scm);
            col_dayofmonth.setAttribute("random", "false");
            col_dayofmonth.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            col_schedule.addContent(col_dayofmonth);

            Element col_dayofweek = new Element("dayOfWeek", scm);
            col_dayofweek.setAttribute("random", "true");
            col_dayofweek.setText("xxxxxxx");
            col_schedule.addContent(col_dayofweek);

            Element col_hourofday = new Element("hourOfDay", scm);
            col_hourofday.setAttribute("random", "true");
            col_hourofday.setText("xxxxxxxxxxxxxxxxxxxxxxxx");
            col_schedule.addContent(col_hourofday);

            Element col_minuteofhour = new Element("minuteOfHour", scm);
            col_minuteofhour.setAttribute("random", "true");
            col_minuteofhour.setText("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            col_schedule.addContent(col_minuteofhour);
            collector.addContent(col_schedule);

            Iterator it_colparameters = col.parameters.entrySet().iterator();
            while (it_colparameters.hasNext()) {
                Map.Entry pair_params = (Map.Entry) it_colparameters.next();
                List<String> param_list = new ArrayList<String>();
                param_list = (ArrayList<String>) pair_params.getValue();
                for (int ip = 0; ip < param_list.size(); ip++) {
                    Element col_parameter = new Element("parameter", scm);
                    col_parameter.setAttribute("id", pair_params.getKey().toString());
                    col_parameter.setText(param_list.get(ip));
                    collector.addContent(col_parameter);
                }
            }

            group.addContent(collector);
        }
        policyxml.getRootElement().addContent(group);

        XMLOutputter xmlOutput = new XMLOutputter();
        Format fmt = Format.getPrettyFormat();
        fmt.setExpandEmptyElements(true);
        xmlOutput.setFormat(fmt);
        xmlOutput.output(policyxml, new FileWriter(this.tempPath + "/policy.xml"));

        if (saveas_file.equals("") || saveas_file.equals(null)) {
            Unzipper unzipper = new Unzipper(this.polFile, this.tempPath);
            unzipper.zip();
        } else {
            Unzipper unzipper = new Unzipper(saveas_file, this.tempPath);
            unzipper.zip();
        }

    }

    public void addCheck(String title) {
        Rule new_check = new Rule();
        new_check.title = title;
        this.rules.add(new_check);
    }

    public void removeCheck(PolicyPack.Rule rule) {
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule_to_check = this.rules.get(i);
            if (rule_to_check.equals(rule)) {
                this.rules.remove(i);
                break;
            }
        }
    }

    public void addVersion(String description) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date1 = new Date();
        String date = dateFormat.format(date1).toString();
        Iterator it = this.versions.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String date_string = pair.getKey().toString();
            if (date_string.subSequence(0, 8).equals(date.toString())) {
                counter++;
            }
        }
        String final_number = String.format("%02d", (counter + 1));
        String new_version = date.toString() + final_number;
        this.versions.put(Integer.parseInt(new_version), description);
    }

    public void removeVersion() {
        if (this.versions.size() > 0) {
            int toremove = this.versions.pollFirstEntry().getKey();
        }
    }

    public void addCollector(String colId, File colFile) {
        File testf = new File(this.tempPath + File.separator + colFile.getName());
        String className = colFile.getName().substring(0, colFile.getName().length() - 4);
        if (testf.exists()) {
            PolicyPack.Collector col = new PolicyPack.Collector(className, tempPath);
            col.colId = colId;
            col.classname = className;
            this.collectors.add(col);
            this.addVersionNote("Added collector instance " + colId + ".");
        } else {
            try {
                FileUtil.copyFile(colFile.getAbsolutePath(), this.tempPath + File.separator + "Collectors" + File.separator + colFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            PolicyPack.Collector col = new PolicyPack.Collector(className, tempPath);
            col.colId = colId;
            col.classname = className;
            this.collectors.add(col);
            this.addVersionNote("Added collector " + className + " - version " + col.realVersion + ".");
        }

    }

    public PolicyPack.Collector getColByName(String colName) {
        PolicyPack.Collector col = null;
        for (int ic = 0; ic < this.collectors.size(); ic++) {
            if (this.collectors.get(ic).colId.equals(colName)) {
                col = this.collectors.get(ic);
                break;
            }
        }
        return col;
    }

    public void deleteTemp() {
        try {
            FileUtils.deleteDirectory(new File(this.tempPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateFolders() {
        if (this.title != null) {
            if (new File("temp" + File.separator + this.title).mkdir() == true) {
                if (new File("temp" + File.separator + this.title + File.separator + "Collectors").mkdir() == true) {
                    System.out.println("Directory structure created successfuly.");
                }
            }
        }
    }

    public void addVersionNote(String description) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date1 = new Date();
        String date = dateFormat.format(date1).toString();
        if (Policy.this.versions.get(Integer.parseInt(date.toString() + "01")) == null) {
            this.addVersion("Generated note: " + description);
        } else {
            int key = this.versions.firstEntry().getKey();
            String value = this.versions.firstEntry().getValue();
            this.versions.put(key, value +
                    "\nGenerated note: " + description);
        }
    }
}