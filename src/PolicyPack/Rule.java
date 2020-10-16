package PolicyPack;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Rule {
    public String ruleId;
    public List<String> requires = new ArrayList<String>();
    public String title;
    public String description;
    public String violationMessage;
    public String priority;
    public String checkSystem;
    public String sql;
    public boolean isModified = false;
    public List<Rule> rules = new ArrayList<Rule>();
    public TreeMap<Integer, String> versions = new TreeMap<Integer, String>(Comparator.reverseOrder());

    public Rule() {
        // rules.add(rule);
        if (rules != null && !rules.isEmpty()) {
            this.ruleId = Integer.toString(rules.size() + 1);
        } else {
            this.ruleId = Integer.toString(1);
        }
    }

    public String toString() {
        return this.title;
    }


    public void addVersion(String description) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date1 = new Date();
        String date = dateFormat.format(date1).toString();
        Iterator it = this.versions.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String date_string = pair.getKey().toString();
            if (date_string.subSequence(0, 8).equals(date.toString())) {
                counter++;
            }
        }
        String final_number = String.format("%02d", (counter + 1));
        String new_version = date.toString() + final_number;
        this.versions.put(Integer.parseInt(new_version), description);
        if (versions.get(Integer.parseInt(date.toString() + "01")) == null) {
            //System.out.println("No policy version from today, adding new version.");
            addVersion("Check: " + this.title + " - new version " + new_version);
        } else {

            //System.out.println("Version from this day found, adding comment.");
            int key = versions.firstEntry().getKey();
            String value = versions.firstEntry().getValue();
            versions.put(key, value +
                    "\nCheck: " + this.title + " - new version " + new_version);
        }
    }

    public void removeVersion() {
        if (this.versions.size() > 0) {
            this.versions.pollFirstEntry();
        }
    }
}
