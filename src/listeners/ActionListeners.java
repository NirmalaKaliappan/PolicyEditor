package listeners;
/*
import PolicyPack.Collector;
import PolicyPack.Rule;*/

import PolicyPack.Collector;
import PolicyPack.Rule;
import gui.DatabaseWindow;
import logic.Database;
import logic.Policy;
import logic.Table;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jdom2.JDOMException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ActionListeners {


    public void addButtonListener(JButton btnAddVersion_general) {
        for (ActionListener al : btnAddVersion_general.getActionListeners()) {
            btnAddVersion_general.removeActionListener(al);
        }
    }

    public void btnAddVersion_generalActionPerform(Policy pol, JButton btnRemove_general, JList<?> list_general_versions) {
        pol.addVersion("");
        reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
        list_general_versions.setSelectedIndex(0);
    }

    public void btnRemoveVersion_gerneralActionPerform(JFrame frmPolicyEditor, Policy pol, JButton btnRemove_general, JList<?> list_general_versions) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(frmPolicyEditor, "Do you really want to remove last version?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            pol.removeVersion();
            reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
            list_general_versions.setSelectedIndex(0);
        }
    }

    public void btnSaveActionPerformed(JTree tree_checks, JTextField text_title, JTextArea editor_description, JTextArea editor_violation, RSyntaxTextArea editor_sql, JRadioButton rdb_prio_informational, JRadioButton rdb_prio_low, JRadioButton rdb_prio_normal, JRadioButton rdb_prio_high, JButton button_save) {
        DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();
        /* if nothing is selected */
        if (node_check == null) return;
        /* retrieve the node that was selected */
        PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node_check.getUserObject();
        node_rule_check.title = text_title.getText();
        node_rule_check.description = editor_description.getText();
        node_rule_check.violationMessage = editor_violation.getText();
        node_rule_check.sql = editor_sql.getText();
        if (rdb_prio_informational.isSelected()) {
            node_rule_check.priority = "informational";
        }
        if (rdb_prio_low.isSelected()) {
            node_rule_check.priority = "low";
        }
        if (rdb_prio_normal.isSelected()) {
            node_rule_check.priority = "normal";
        }
        if (rdb_prio_high.isSelected()) {
            node_rule_check.priority = "high";
        }
        button_save.setBackground(null);

    }

    public void btnNewCheckActionPerform(JFrame frmPolicyEditor, Policy pol, JTree tree_checks) {
        String check_name = " ";
        check_name = JOptionPane.showInputDialog(frmPolicyEditor, "Enter new check title:");
        if (check_name == null) {
            return;
        }
        if (check_name.length() < 3) {
            JOptionPane.showInputDialog(frmPolicyEditor, "check title must be more than 3 characters");
            return;
        }
        pol.addCheck(check_name.trim());
        reloadTreeChecks(tree_checks, pol);
    }

    public void btnRemoveCheckActionPerform(JTree tree_checks, JFrame frmPolicyEditor, Policy pol) {
        if (tree_checks.getLastSelectedPathComponent() != null) {

            DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();
            /* if nothing is selected */
            if (node_check == null) return;
            /* retrieve the node that was selected */
            PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node_check.getUserObject();
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(frmPolicyEditor, "Do you really want to remove check " + node_rule_check.title + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                pol.removeCheck(node_rule_check);
                reloadTreeChecks(tree_checks, pol);
            }
        }
    }

    public void btn_col_save_paramActionPerform(JList list_col_params, JTree tree_collectors, JTextArea textArea_col_param_values) {
        String colParam = (String) list_col_params.getSelectedValue();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();
        PolicyPack.Collector col = (PolicyPack.Collector) node.getUserObject();

        if (col.parameters != null) {
            if (col.parameters.get(colParam) != null) {
                col.parameters.get(colParam).clear();
            }
        }
        ArrayList<String> values = new ArrayList<String>();
        for (String line : textArea_col_param_values.getText().split("\\n")) {
            if (!line.equals("")) {
                values.add(line);
            } else {
                break;
            }
        }
        col.parameters.put(colParam, values);
    }

    public void reloadVersionsGeneral(JList list_general_versions, Policy policy, JButton remove_button) {
        Iterator it = policy.versions.entrySet().iterator();
        DefaultListModel listModel = new DefaultListModel();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            listModel.addElement(pair.getKey());
        }
        list_general_versions.setModel(listModel);
        list_general_versions.clearSelection();
        list_general_versions.revalidate();
        list_general_versions.repaint();
        if (policy.versions.size() == 0) {
            remove_button.setEnabled(false);
        } else {
            remove_button.setEnabled(true);
        }
    }

    public void reloadTreeChecks(JTree tree_checks, Policy pol) {
        tree_checks.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("Control Points") {
                    {
                        for (int i = 0; i < pol.rules.size(); i++) {
                            //System.out.println("Creating node for check: " + pol.rules.get(i).title);
                            DefaultMutableTreeNode node_rule = new DefaultMutableTreeNode(pol.rules.get(i));
                            //node_rule.setUserObject(pol.rules.get(i));
                            add(node_rule);
                        }
                    }
                }
        ));
        tree_checks.repaint();
    }

    public void runSqlActionPerform(RSyntaxTextArea editor_sql, JTextArea editor_violation, Database db, JFrame frmPolicyEditor, JTabbedPane tabbedPane) {
        String sql_str = editor_sql.getText();
        String violation_str = editor_violation.getText();
        Table table_test = db.runQuery(sql_str);
        DatabaseWindow dw = new gui.DatabaseWindow();
        JPanel result = dw.getPanel(table_test, violation_str);


        JPanel result_panel = dw.getPanel(table_test, violation_str);
        JFrame frame_result = new JFrame("SQL result table");
        frame_result.setVisible(true);
        frame_result.getContentPane().setMinimumSize(new Dimension(1024, 768));
        frame_result.setBounds(new Rectangle(0, 0, 1024, 768));
        frame_result.getContentPane().setBounds(new Rectangle(0, 0, 1024, 768));
        frame_result.setPreferredSize(new Dimension(1024, 768));
        frame_result.getContentPane().setSize(new Dimension(1024, 900));
        frame_result.getContentPane().setPreferredSize(new Dimension(1024, 768));
        frame_result.setTitle("SQL result table");
        frame_result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_result.getContentPane().setLayout(new BorderLayout());
        frame_result.getContentPane().add(result_panel, BorderLayout.CENTER);
        frame_result.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void mntmSaveActionPerform(Policy pol, JTextArea textArea_general_description) {
        try {
            pol.build("");
            textArea_general_description.setText(pol.description);
            textArea_general_description.repaint();
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void mntmNewPolicyActionPerform(Policy pol, JFrame frmPolicyEditor, JTextField text_general_title, JTextArea textArea_messages, JPanel panel_general, JPanel panel_policy, JPanel panel_collectors, JList list_params, JTree tree_checks, JButton btn_col_add_collector, JTree tree_collectors, JList list_general_versions, JButton btnNewParameter, JMenuItem mntmSaveas) {
        String polName = JOptionPane.showInputDialog(frmPolicyEditor, "Type new policy name:", "D041-C-Gv3.1-LNX");
        polName = checkPolName(polName, frmPolicyEditor);
        pol = new Policy();
        pol.tempPath = "temp" + File.separator + polName;
        pol.title = polName;
        pol.generateFolders();
        text_general_title.setText(polName);
        text_general_title.repaint();
        loadMessages(textArea_messages, pol);

        panel_general.setEnabled(true);
        panel_policy.setEnabled(true);
        panel_collectors.setEnabled(true);
        enableComponents(panel_general, true);
        list_params.setEnabled(true);
        btnNewParameter.setEnabled(true);
        enableComponents(panel_policy, false);
        tree_checks.setEnabled(true);
        btn_col_add_collector.setEnabled(true);
        tree_collectors.setEnabled(true);
        mntmSaveas.setEnabled(true);
      //  btnNewCheck.setEnabled(true);

        list_general_versions.setSelectedIndex(0);
    }

    public void mntmSaveasActionPerform(JFrame frmPolicyEditor, Policy pol, JTextArea textArea_general_description, JMenuItem mntmSave) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(frmPolicyEditor);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            try {
                pol.polFile = fileToSave.getAbsolutePath();
                pol.build(fileToSave.getAbsolutePath());
                textArea_general_description.setText(pol.description);
                textArea_general_description.repaint();
                mntmSave.setEnabled(true);
            } catch (Exception ep) {
                // TODO Auto-generated catch block
                ep.printStackTrace();
            }
        }
    }

    public void tree_checksActionPerform(JTree tree_checks, JPanel panel_policy, JButton btnNewCheck, JTextField text_title, JList<?> list_versions, JTextArea text_version_comment, JTextArea editor_description, JTextArea editor_violation, JRadioButton rdb_prio_informational, JRadioButton rdb_prio_low, JRadioButton rdb_prio_normal, JRadioButton rdb_prio_high, RSyntaxTextArea editor_sql, JList check_collectors, JButton btnAddVersion, JButton btnRemove, JList<?> list_general_versions, Policy pol, JButton btnRemove_general, JLabel label_count) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_checks.getLastSelectedPathComponent();

        /* if nothing is selected */
        if (node == null) {
            enableComponents(panel_policy, false);
            btnNewCheck.setEnabled(true);
            tree_checks.setEnabled(true);
            return;
        }
        /* retrieve the node that was selected */

        PolicyPack.Rule node_rule = (PolicyPack.Rule) node.getUserObject();
        if (node_rule == null) {
            enableComponents(panel_policy, false);
            tree_checks.setEnabled(true);
            return;
        }
        enableComponents(panel_policy, true);
        populateForm(node_rule, text_title, list_versions, text_version_comment, editor_description, editor_violation, rdb_prio_informational, rdb_prio_low, rdb_prio_normal, rdb_prio_high, editor_sql, check_collectors);
        text_version_comment.setEditable(false);
        ListSelectionListener listSelectionListener2 = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (list_versions.getSelectedIndex() > -1) {
                    text_version_comment.setEditable(true);
                } else {
                    text_version_comment.setEditable(false);
                }
                JList list = (JList) listSelectionEvent.getSource();
                ListModel listModel = list.getModel();
                if (listModel.getSize() > 0) {
                    text_version_comment.setEditable(true);
                    String descr = "";
                    if (node_rule.versions.size() > 0) {

                        if (list.getSelectedValue() != null) {
                            descr = node_rule.versions.get((int) list.getSelectedValue());
                            //System.out.println("Description: " + descr);
                            if (descr == null) {
                                descr = "";
                            }
                        } else {
                            descr = "";
                        }

                    } else {
                        descr = "";
                    }

                    loadVersionDescription(text_version_comment, descr);
                } else {
                    text_version_comment.setEditable(false);
                    text_version_comment.setText("");
                    text_version_comment.setCaretPosition(0);
                    text_version_comment.repaint();
                }
            }
        };
        for (ActionListener al : btnAddVersion.getActionListeners()) {
            btnAddVersion.removeActionListener(al);
        }

        // addButtonListener(btnAddVersion);
        btnAddVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println("add before:" + node_rule.versions.toString());
                node_rule.addVersion("");
                //System.out.println("add after:" + node_rule.versions.toString());
                reloadVersions(list_versions, node_rule, btnRemove);
                list_versions.setSelectedIndex(0);
                reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
            }
        });
        // addButtonListener(btnRemove);
        addButtonListener(btnRemove);

        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to remove last version?", "Warning", dialogButton);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    node_rule.removeVersion();
                    reloadVersions(list_versions, node_rule, btnRemove);
                    list_versions.setSelectedIndex(0);
                }
            }
        });

        for (ListSelectionListener al : list_versions.getListSelectionListeners()) {
            list_versions.removeListSelectionListener(al);
        }
        list_versions.addListSelectionListener(listSelectionListener2);
        check_collectors.setSelectedIndex(0);
        list_versions.setSelectedIndex(0);

        //for()

        String appendixTitle = ((Rule) node.getUserObject()).title;
        String[] splitString = appendixTitle.split("-");
        AppendixName appendixNameComparison = new AppendixName();
        String appendixName = appendixNameComparison.appendixName(splitString[0]);

        label_count.setText("CP & Appendix Name:" + node.getParent().getChildCount() + "&" + appendixName);
    }

    public void btnSaveDescriptionActionPerform(JTextArea text_version_comment, JList<?> list_versions, JTree tree_checks) {
        String desc_text = text_version_comment.getText();
        if (list_versions.getSelectedValue() == null) {
            return;
        }
        int sel_val = Integer.parseInt(list_versions.getSelectedValue().toString());
        //System.out.println("value: " + sel_val);
        DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();
        /* if nothing is selected */
        if (node_check == null) return;
        /* retrieve the node that was selected */
        PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node_check.getUserObject();
        node_rule_check.versions.put(sel_val, desc_text);
    }

    public void listSelectionListenerActionPerformed(ListSelectionEvent listSelectionEvent, JList<?> list_general_versions, JTextArea textArea_general_version_description, Policy pol) {
        boolean adjust = listSelectionEvent.getValueIsAdjusting();
        if (!adjust) {
            if (list_general_versions.getSelectedIndex() > -1) {
                textArea_general_version_description.setEditable(true);
            } else {
                textArea_general_version_description.setEditable(false);
            }
            String descript = "";
            JList list = (JList) listSelectionEvent.getSource();
            ListModel listModel = list_general_versions.getModel();
            if (list.getSelectedValue() != null) {
                descript = pol.versions.get((int) list.getSelectedValue());
                System.out.println("Description: " + descript);
                if (descript == null) {
                    descript = "";
                }
			          /*
			          if (list.getSelectedIndex() == 0) {
			        	  descript = "!!! Current version !!!\n" + descript;
			          }*/
            } else {
                descript = "";
            }
            loadVersionDescription(textArea_general_version_description, descript);
            //System.out.println("Selected: " + list.getSelectedValue());
        }
    }

    public void saveVersionDescriptionActionPerformed(JTextArea textArea_general_version_description, JList<?> list_general_versions, Policy pol) {
        String desc_text = textArea_general_version_description.getText();
        if (list_general_versions.getSelectedValue() == null) {
            return;
        }
        int sel_val = Integer.parseInt(list_general_versions.getSelectedValue().toString());
        pol.versions.put(sel_val, desc_text);

    }

    public void btnCheckColAddActionPerformed(Policy pol, JFrame frmPolicyEditor, JTree tree_checks, JList check_collectors) {
        Icon icon = null;
        if (pol.collectors == null || pol.collectors.isEmpty()) {
            return;
        }
        PolicyPack.Collector col = (PolicyPack.Collector) JOptionPane.showInputDialog(
                frmPolicyEditor,
                "Select collector",
                "Add collector",
                JOptionPane.PLAIN_MESSAGE,
                icon,
                (Object[]) pol.collectors.toArray(),
                (Object) pol.collectors.get(0));
        if (col == null) {
            return;
        } else {
            DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();
            /* if nothing is selected */
            if (node_check == null) return;
            /* retrieve the node that was selected */
            PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node_check.getUserObject();
            node_rule_check.requires.add(col.colId);
            loadCheckCollectors(check_collectors, node_rule_check);
        }
    }

    public void btnCheckColRemoveActionPerformed(JFrame frmPolicyEditor, JTree tree_checks, JList<?> check_collectors) {
        int answer = JOptionPane.showConfirmDialog(
                frmPolicyEditor,
                "Do you really want to remove this collector?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {

            DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();
            /* if nothing is selected */
            if (node_check == null) return;
            /* retrieve the node that was selected */
            PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node_check.getUserObject();

            String colName = (String) check_collectors.getSelectedValue();
            int toRemove = -1;
            for (int i = 0; i < node_rule_check.requires.size(); i++) {
                if (node_rule_check.requires.get(i) == colName) {
                    toRemove = i;
                    break;
                }
            }
            node_rule_check.requires.remove(toRemove);

            loadCheckCollectors(check_collectors, node_rule_check);
        }
    }

    public void listSelectionListenerParamsActionPerformed(JTextArea textArea_param_detail, ListSelectionEvent listSelectionEvent, JPanel panel_parameters, JComboBox comboBox_paramtype, JList list_params, JButton btnNewParameter) {
        textArea_param_detail.setText("");
        JList list = (JList) listSelectionEvent.getSource();
        Policy.PolParam param = (Policy.PolParam) list.getSelectedValue();
        if (param != null) {
            enableComponents(panel_parameters, true);
            for (int i = 0; i < param.values.size(); i++) {
                String text = textArea_param_detail.getText();
                if (i > 0) {
                    text = text + "\n" + param.values.get(i);
                } else {
                    text = text + param.values.get(i);
                }
                textArea_param_detail.setText(text);
                textArea_param_detail.setCaretPosition(0);
            }
            String param_type = param.paramType;
            if (param_type.equals("string")) {
                comboBox_paramtype.setSelectedIndex(0);
            }
            if (param_type.equals("string_list")) {
                comboBox_paramtype.setSelectedIndex(1);
            }
            if (param_type.equals("integer")) {
                comboBox_paramtype.setSelectedIndex(2);
            }
            if (param_type.equals("integer_list")) {
                comboBox_paramtype.setSelectedIndex(3);
            }
        } else {
            enableComponents(panel_parameters, false);
            list_params.setEnabled(true);
            btnNewParameter.setEnabled(true);
        }
    }

    public void btnSaveParameterActionPerformed(JList<?> list_params, JComboBox comboBox_paramtype, JTextArea textArea_param_detail) {
        Policy.PolParam par = (Policy.PolParam) list_params.getSelectedValue();
        int type_index = comboBox_paramtype.getSelectedIndex();
        if (type_index == 0) {
            par.paramType = "string";
        }
        if (type_index == 1) {
            par.paramType = "string_list";
        }
        if (type_index == 2) {
            par.paramType = "integer";
        }
        if (type_index == 3) {
            par.paramType = "integer_list";
        }
        par.values = new ArrayList<String>();
        for (String line : textArea_param_detail.getText().split("\\n")) {
            if (!line.equals("")) {
                System.out.println("Param line: " + line);
                par.values.add(line);
            } else {
                break;
            }
        }
    }

    public void btnNewParameterActionPerformed(JFrame frmPolicyEditor, Policy pol, JTextArea textArea_param_detail, JList list_params) {
        String param_name = " ";
        param_name = JOptionPane.showInputDialog(frmPolicyEditor, "Enter new parameter name (without $ and parentheses):");
        if (param_name == null) {
            return;
        }
        if (param_name.length() < 3) {
            return;
        }

        Policy.PolParam param = new Policy.PolParam();
        param.paramName = ("$(" + param_name + ")").trim();
        param.paramType = "string_list";
        pol.polParameters.add(param);
        textArea_param_detail.setEnabled(false);
        loadParameters(pol, list_params);
    }

    public void btnDeleteParameterActionPerformed(JList<?> list_params, Policy pol) {
        Policy.PolParam par = (Policy.PolParam) list_params.getSelectedValue();
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to remove parameter " + par.paramName + "?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            int index = -1;
            for (int i = 0; i < pol.polParameters.size(); i++) {

                if (par.paramName.equals(pol.polParameters.get(i).paramName)) {
                    index = i;
                    break;
                }
            }
            if (index > -1) {
                pol.polParameters.remove(index);
            }
            loadParameters(pol, list_params);

        }
    }

    public void treeCollectorsAddTreeSelectionListener(JTree tree_collectors, JPanel panel_collectors, JButton btn_col_add_collector, JTextPane textPane_col_info, JList list_col_params, JTextArea textArea_col_param_values) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();

        /* if nothing is selected */
        if (node == null) {
            enableComponents(panel_collectors, false);
            tree_collectors.setEnabled(true);
            btn_col_add_collector.setEnabled(true);
            return;
        } else {
            enableComponents(panel_collectors, true);
        }
        /* retrieve the node that was selected */
        Collector node_collector = (Collector) node.getUserObject();
        if (node_collector == null) {
            enableComponents(panel_collectors, false);
            tree_collectors.setEnabled(true);
            btn_col_add_collector.setEnabled(true);
            tree_collectors.setEnabled(true);
            return;
        }
        enableComponents(panel_collectors, true);
        populateCollector(textPane_col_info, list_col_params, textArea_col_param_values, node_collector);
        list_col_params.setSelectedIndex(0);
    }

    public void listSelectionListenerColParamsActionPerformed(JTextArea textArea_col_param_values, ListSelectionEvent listSelectionEvent, JTree tree_collectors) {
        textArea_col_param_values.setText("");
        JList list = (JList) listSelectionEvent.getSource();
        String paramName = (String) list.getSelectedValue();
        if (tree_collectors == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Collector node_collector = (Collector) node.getUserObject();

        if (paramName != null) {
            loadColParamValues(node_collector, paramName, textArea_col_param_values);
        }
    }

    public void listSelectionListenerCheckCollectorsListenerActionPerformed(JComboBox comboBox_table_list, ListSelectionEvent listSelectionEvent, Policy pol) {
        comboBox_table_list.removeAllItems();
        JList list = (JList) listSelectionEvent.getSource();
        String colName = (String) list.getSelectedValue();

        Collector col = pol.getColByName(colName);
        if (col == null) {
            return;
        }
        if (col.tables == null) {
            return;
        }

        for (int i = 0; i < col.tables.size(); i++) {
            comboBox_table_list.addItem(col.tables.get(i));
        }

    }

    public void comboBoxTableListActionPerformed(ActionEvent e, JTextPane textPane_check_col_columns) {
        JComboBox cb = (JComboBox) e.getSource();
        PolicyPack.Collector.Table table = (PolicyPack.Collector.Table) cb.getSelectedItem();
        String text = "";
        if (table == null) {
            return;
        }
        if (table.columns == null) {
            return;
        }
        for (int i = 0; i < table.columns.size(); i++) {
            text = text + table.columns.get(i).name + " - " + table.columns.get(i).type + "\n";
        }
        textPane_check_col_columns.setText(text);
        textPane_check_col_columns.setCaretPosition(0);
    }

    public void btnColDeleteParamActionPerformed(JList<?> list_col_params, JTree tree_collectors, JTextArea textArea_col_param_values) {
        String colParam = (String) list_col_params.getSelectedValue();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();
        Collector col = (Collector) node.getUserObject();
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to remove parameter " + colParam + "?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            col.parameters.remove(colParam);
            loadColParameters(col, list_col_params);
            loadColParamValues(col, colParam, textArea_col_param_values);
        }
    }

    public void btnColAddParamActionPerformed(JList<?> list_col_params, JTree tree_collectors, JFrame frmPolicyEditor, JTextArea textArea_col_param_values) {
        String colParam = (String) list_col_params.getSelectedValue();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();
        Collector col = (Collector) node.getUserObject();
        String paramName = JOptionPane.showInputDialog(frmPolicyEditor, "Enter new parameter name:");
        if (paramName == null) {
            return;
        }
        if (paramName.length() < 3) {
            return;
        }

        col.parameters.put(paramName, null);
        loadColParameters(col, list_col_params);
        loadColParamValues(col, paramName, textArea_col_param_values);
    }

    public void btnColDeleteCollectorActionPerformed(JTree tree_collectors, JFrame frmPolicyEditor, Policy pol, JList list_general_versions, JButton btnRemove_general, JList list_col_params) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree_collectors.getLastSelectedPathComponent();
        Collector col = (Collector) node.getUserObject();

        JCheckBox checkbox = new JCheckBox();
        checkbox.setText("Remove all instances including collector .jar file");
        JLabel label = new JLabel();
        label.setText("Info: in case that this collector has only one instance, .jar file will be deleted.");
        label.setForeground(Color.BLUE);
        Object[] remove_settings = {"Are you sure you want to remove collector " + col.colId + "?", label, checkbox};
        Icon icon = null;
        boolean allInstances = false;
        int choice = JOptionPane.showConfirmDialog(frmPolicyEditor, remove_settings, "Remove collector", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (checkbox.isSelected()) {
                allInstances = true;
            } else {
                allInstances = false;
            }
            pol.removeCollector(col, allInstances);
            loadCollectors(pol, tree_collectors);
            reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
            tree_collectors.clearSelection();
        }


        loadColParameters(col, list_col_params);

    }

    public void btnColAddCollectorActionPerformed(Policy pol, JFrame frmPolicyEditor, JList<?> list_general_versions, JButton btnRemove_general, JTree tree_collectors) {
        File f = new File(pol.tempPath + "/Collectors");
        final JFileChooser fc = new JFileChooser(f);
        int returnVal = fc.showOpenDialog(frmPolicyEditor);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".jar collectors", "jar");
        fc.setFileFilter(filter);

        File colfile = fc.getSelectedFile();
        String defname = "";
        if (colfile != null) {
            if (colfile.getName() != null) {
                defname = colfile.getName().substring(0, colfile.getName().length() - 4);
                String[] defname_split = defname.split("\\.");
                defname = defname_split[defname_split.length - 1];
            }
        }

        String collectorId = JOptionPane.showInputDialog(frmPolicyEditor, "Enter new collector id:", defname);

        collectorId = checkColId(collectorId, pol, frmPolicyEditor);
        if (collectorId == null) {
            return;
        }
        pol.addCollector(collectorId, colfile);
        reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
        loadCollectors(pol, tree_collectors);
        JOptionPane.showMessageDialog(frmPolicyEditor, "Collector has been added.");

    }

    private String checkColId(String colId, Policy pol, JFrame frmPolicyEditor) {
        if (colId == null) {
            return null;
        }
        if ((pol.getColByName(colId) == null) && (!colId.equals("")) && (colId.length() > 3)) {
            return colId;
        } else {
            String collectorId = JOptionPane.showInputDialog(frmPolicyEditor, "Already exists or name is too short, enter different name:", colId);
            collectorId = checkColId(collectorId, pol, frmPolicyEditor);
            return collectorId;
        }
    }

    private void loadColParamValues(Collector col, String paramName, JTextArea textArea_col_param_values) {
        String values = "";
        if (col.parameters.get(paramName) == null) {
            return;
        }
        for (int i = 0; i < col.parameters.get(paramName).size(); i++) {
            String now_text = col.parameters.get(paramName).get(i);
            if (i != (col.parameters.get(paramName).size() - 1)) {
                values = values + now_text + "\n";
            } else {
                values = values + now_text;
            }

        }
        textArea_col_param_values.setText(values);
        textArea_col_param_values.setCaretPosition(0);
    }

    public void populateCollector(JTextPane textPane_col_info, JList<?> list_col_params, JTextArea textArea_col_param_values,Collector col) {
        textPane_col_info.setContentType("text/html");
        String text = "<strong>Collector name:</strong> " + col.classname + "<br>" +
                "<strong>Supported systems:</strong> " + col.validOs + "<br>" +
                "<strong>Release:</strong> " + col.realVersion + "<br>" +
                "<strong>Description:</strong> <br> " + col.description + "<br>" +
                "<strong>List of possible parameters: </strong>" + "<br>" +
                col.allParams;
        textPane_col_info.setText(text);
        textPane_col_info.setCaretPosition(0);

        loadColParameters(col, list_col_params);
    }

    private void loadColParameters(Collector col, JList<?> list_col_params) {
        DefaultListModel listModel = new DefaultListModel();

        Iterator it = col.parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            listModel.addElement(pair.getKey());
        }
        list_col_params.setModel(listModel);
        list_col_params.clearSelection();
        list_col_params.revalidate();
        list_col_params.repaint();
    }

    public void loadVersionDescription(JTextArea verDesc, String description) {
        verDesc.setText(description);
        verDesc.setCaretPosition(0);
        verDesc.repaint();
    }

    private void loadCheckCollectors(JList check_collectors, PolicyPack.Rule rule) {
        DefaultListModel model_cols = new DefaultListModel();
        for (int ii = 0; ii < rule.requires.size(); ii++) {
            model_cols.addElement(rule.requires.get(ii));
        }

        check_collectors.setModel(model_cols);
        check_collectors.revalidate();
        check_collectors.repaint();
    }

    public void reloadVersions(JList list_versions, PolicyPack.Rule rule, JButton remove_button) {
        Iterator it = rule.versions.entrySet().iterator();
        DefaultListModel listModel = new DefaultListModel();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            listModel.addElement(pair.getKey());
        }
        list_versions.setModel(listModel);
        list_versions.clearSelection();
        list_versions.revalidate();
        list_versions.repaint();
        if (rule.versions.size() == 0) {
            remove_button.setEnabled(false);
        } else {
            remove_button.setEnabled(true);
        }
    }

    public void populateForm(PolicyPack.Rule rule, JTextField text_title, JList<?> list_versions, JTextArea text_version_comment, JTextArea editor_description,
                             JTextArea editor_violation, JRadioButton rdb_prio_informational, JRadioButton rdb_prio_low, JRadioButton rdb_prio_normal, JRadioButton rdb_prio_high, RSyntaxTextArea editor_sql, JList check_collectors) {
        text_title.setText(rule.title);
        editor_description.setText(rule.description);
        editor_description.setCaretPosition(0);
        editor_violation.setText(rule.violationMessage);
        editor_violation.setCaretPosition(0);
        if (rule.priority != null) {
            switch (rule.priority) {
                case "informational":
                    rdb_prio_informational.setSelected(true);
                    rdb_prio_low.setSelected(false);
                    rdb_prio_normal.setSelected(false);
                    rdb_prio_high.setSelected(false);
                    break;
                case "low":
                    rdb_prio_informational.setSelected(false);
                    rdb_prio_low.setSelected(true);
                    rdb_prio_normal.setSelected(false);
                    rdb_prio_high.setSelected(false);
                    break;
                case "normal":
                    rdb_prio_informational.setSelected(false);
                    rdb_prio_low.setSelected(false);
                    rdb_prio_normal.setSelected(true);
                    rdb_prio_high.setSelected(false);
                    break;
                case "high":
                    rdb_prio_informational.setSelected(false);
                    rdb_prio_low.setSelected(false);
                    rdb_prio_normal.setSelected(false);
                    rdb_prio_high.setSelected(true);
                    break;
                default:
                    rdb_prio_informational.setSelected(false);
                    rdb_prio_low.setSelected(false);
                    rdb_prio_normal.setSelected(true);
                    rdb_prio_high.setSelected(false);
                    break;
            }
        }
        Iterator it = rule.versions.entrySet().iterator();
        int counter_values = 0;
        DefaultListModel listModel = new DefaultListModel();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            listModel.addElement(pair.getKey());
            counter_values++;
            //System.out.println("versions: " + pair.getKey());
        }
        list_versions.setModel(listModel);
        list_versions.clearSelection();
        list_versions.revalidate();
        list_versions.repaint();
        editor_sql.setText(rule.sql);
        editor_sql.setCaretPosition(0);

        loadCheckCollectors(check_collectors, rule);

    }

    public void loadParameters(Policy pol, JList<?> list_params) {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < pol.polParameters.size(); i++) {
            listModel.addElement(pol.polParameters.get(i));
        }
        list_params.setModel(listModel);
        list_params.clearSelection();
        list_params.revalidate();
        list_params.repaint();

    }

    public void loadCollectors(Policy pol, JTree tree_collectors) {
        DefaultTreeModel model = new DefaultTreeModel(
                new DefaultMutableTreeNode("JTree") {
                    {
                        System.out.println("Collector count: " + pol.collectors.size());
                        for (int i = 0; i < pol.collectors.size(); i++) {
                            //System.out.println("Creating node for check: " + pol.rules.get(i).title);
                            DefaultMutableTreeNode node_collector = new DefaultMutableTreeNode(pol.collectors.get(i));
                            //node_rule.setUserObject(pol.rules.get(i));
                            add(node_collector);
                        }
                    }
                }
        );
        tree_collectors.setModel(model);
        tree_collectors.clearSelection();
        tree_collectors.revalidate();
        tree_collectors.repaint();
    }


    public void loadMessages(JTextArea messArea, Policy pol) {
        String mess = "";
        if (pol.messages != null) {
            for (int i = 0; i < pol.messages.size(); i++) {
                mess = mess + pol.messages.get(i) + "\n";
            }
            messArea.setText(mess);
            messArea.repaint();
        }
    }

    public void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }

    private String checkPolName(String polName, JFrame frmPolicyEditor) {
        if (polName == null) {
            return null;
        }
        if ((!polName.equals("")) && (polName.length() > 3)) {
            return polName;
        } else {

            String polNameTest = JOptionPane.showInputDialog(frmPolicyEditor, "Policy name must have at least 4 characters:", polName);
            polName = checkPolName(polNameTest, frmPolicyEditor);
            return polName;
        }
    }


}
