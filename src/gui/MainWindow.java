package gui;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import PolicyPack.Collector;
import PolicyPack.Rule;
import PolicyPack.TreePopup;
import listeners.ActionListeners;
import listeners.AppendixName;
import listeners.JExpandableTextArea;
import logic.LoadDatabase;
import logic.Table;
import net.miginfocom.swing.MigLayout;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.fife.ui.rtextarea.*;
import org.jdom2.JDOMException;

import org.fife.ui.rsyntaxtextarea.*;

import logic.Policy;

import logic.Database;


public class MainWindow {

    public JFrame frmPolicyEditor;
    private JTextField text_title;
    private JTextField text_general_title;
    public static Policy pol;
    public static Database db;
    ActionListeners actionListeners;

    /**
     * Create the application.
     */
    public MainWindow() {

        initialize();

    }

    /**
     * Initialize the contents of the frame.
     */

    private void initialize() {
        frmPolicyEditor = new JFrame();
        actionListeners = new ActionListeners();

        frmPolicyEditor.setVisible(true);
        frmPolicyEditor.getContentPane().setMinimumSize(new Dimension(1024, 768));
        frmPolicyEditor.setBounds(new Rectangle(0, 0, 1024, 768));
        frmPolicyEditor.getContentPane().setBounds(new Rectangle(0, 0, 1024, 768));
        frmPolicyEditor.setPreferredSize(new Dimension(1024, 768));
        frmPolicyEditor.getContentPane().setSize(new Dimension(1024, 900));
        frmPolicyEditor.getContentPane().setPreferredSize(new Dimension(1024, 768));
        frmPolicyEditor.setTitle("Policy Editor");
        frmPolicyEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmPolicyEditor.getContentPane().setLayout(new BoxLayout(frmPolicyEditor.getContentPane(), BoxLayout.X_AXIS));

        JButton btn_other_db = new JButton("load database/JAC_DATA");
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(new Rectangle(0, 0, 1024, 768));
        tabbedPane.setPreferredSize(new Dimension(1024, 768));
        frmPolicyEditor.getContentPane().add(tabbedPane);

        JPanel panel_general = new JPanel();
        panel_general.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        panel_general.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_general.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabbedPane.addTab("General", null, panel_general, null);
        tabbedPane.setEnabledAt(0, true);
        panel_general.setLayout(new MigLayout("", "[100px:600px:600px,grow,fill][100px:250px:300px,grow,fill][100px:250px:300px,grow,fill][]", "[50px:50px:50px,grow,fill][100px:200px:300px,grow][][100px:200px:200px,grow][]"));

        JScrollPane scroll_bar = new JScrollPane(panel_general);
        scroll_bar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_bar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.add(scroll_bar, "General");


        JPanel panel_general_title = new JPanel();
        panel_general.add(panel_general_title, "cell 0 0");
        panel_general_title.setLayout(new BorderLayout(0, 0));

        JLabel lblPolicyTitle = new JLabel("Policy Title");
        panel_general_title.add(lblPolicyTitle, BorderLayout.NORTH);

        text_general_title = new JTextField();
        text_general_title.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        text_general_title.setPreferredSize(new Dimension(4, 30));
        panel_general_title.add(text_general_title, BorderLayout.SOUTH);
        text_general_title.setColumns(30);

        /*JExpandableTextArea expansion = new JExpandableTextArea(3, 3);
        expansion.setLineWrap(true);
        panel_general_title.add(new JScrollPane(expansion), null);
        frmPolicyEditor.setVisible(true);*/

        JPanel panel_general_description = new JPanel();
        panel_general_description.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_general.add(panel_general_description, "cell 0 1,grow");
        panel_general_description.setLayout(new BorderLayout(0, 0));

        JLabel lblPolicyDescription = new JLabel("Policy Description");
        panel_general_description.add(lblPolicyDescription, BorderLayout.NORTH);

        JTextArea textArea_general_description = new JTextArea();
        textArea_general_description.setLineWrap(true);
        textArea_general_description.setWrapStyleWord(true);
        textArea_general_description.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        textArea_general_description.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPolicyDescription.setLabelFor(textArea_general_description);
        textArea_general_description.setRows(8);
        textArea_general_description.setColumns(40);
        final JScrollPane scrollPane_general_description = new JScrollPane(textArea_general_description);
        GridBagConstraints gbc_scrollPane_general_description = new GridBagConstraints();
        panel_general_description.add(scrollPane_general_description, BorderLayout.CENTER);

        JPanel panel_general_version = new JPanel();
        panel_general.add(panel_general_version, "cell 0 3,grow");
        GridBagLayout gbl_panel_general_version = new GridBagLayout();
        gbl_panel_general_version.columnWidths = new int[]{60, 270};
        gbl_panel_general_version.rowHeights = new int[]{15, 100, 0, 50};
        gbl_panel_general_version.columnWeights = new double[]{1.0, 1.0};
        gbl_panel_general_version.rowWeights = new double[]{0.0, 1.0, 1.0};
        panel_general_version.setLayout(gbl_panel_general_version);

        JLabel label_general_versions = new JLabel("Versions");
        GridBagConstraints gbc_label_general_versions = new GridBagConstraints();
        gbc_label_general_versions.insets = new Insets(0, 0, 5, 5);
        gbc_label_general_versions.fill = GridBagConstraints.BOTH;
        gbc_label_general_versions.gridx = 0;
        gbc_label_general_versions.gridy = 0;
        panel_general_version.add(label_general_versions, gbc_label_general_versions);

        JScrollPane scrollPane_general_versions = new JScrollPane();
        GridBagConstraints gbc_scrollPane_general_versions = new GridBagConstraints();
        gbc_scrollPane_general_versions.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane_general_versions.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_general_versions.gridx = 0;
        gbc_scrollPane_general_versions.gridy = 1;
        panel_general_version.add(scrollPane_general_versions, gbc_scrollPane_general_versions);

        JList<?> list_general_versions = new JList<Object>();
        list_general_versions.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane_general_versions.setViewportView(list_general_versions);

        JScrollPane scrollPane_general_versions_description = new JScrollPane();
        GridBagConstraints gbc_scrollPane_general_versions_description = new GridBagConstraints();
        gbc_scrollPane_general_versions_description.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_general_versions_description.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_general_versions_description.gridx = 1;
        gbc_scrollPane_general_versions_description.gridy = 1;
        panel_general_version.add(scrollPane_general_versions_description, gbc_scrollPane_general_versions_description);

        JTextArea textArea_general_version_description = new JTextArea();
        textArea_general_version_description.setWrapStyleWord(true);
        textArea_general_version_description.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane_general_versions_description.setViewportView(textArea_general_version_description);

        JPanel panel_version_buttons = new JPanel();
        GridBagConstraints gbc_panel_version_buttons = new GridBagConstraints();
        gbc_panel_version_buttons.insets = new Insets(0, 0, 5, 5);
        gbc_panel_version_buttons.fill = GridBagConstraints.BOTH;
        gbc_panel_version_buttons.gridx = 0;
        gbc_panel_version_buttons.gridy = 2;
        panel_general_version.add(panel_version_buttons, gbc_panel_version_buttons);

        JButton btnAddVersion_general = new JButton("Add version");
        panel_version_buttons.add(btnAddVersion_general);
        for (ActionListener al : btnAddVersion_general.getActionListeners()) {
            btnAddVersion_general.removeActionListener(al);
        }

        JButton btnRemove_general = new JButton("Remove last");
        panel_version_buttons.add(btnRemove_general);
        btnAddVersion_general.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pol.addVersion("");
                reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
                list_general_versions.setSelectedIndex(0);
            }
        });
        for (ActionListener al : btnRemove_general.getActionListeners()) {
            btnRemove_general.removeActionListener(al);
        }
        btnRemove_general.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //actionListeners.btnAddVersion_generalActionPerform(pol, btnRemove_general, list_general_versions);
                actionListeners.btnRemoveVersion_gerneralActionPerform(frmPolicyEditor, pol, btnRemove_general, list_general_versions);
            }
        });


        JPanel panel_policy = new JPanel();
        panel_policy.setPreferredSize(new Dimension(1600, 900));
        tabbedPane.addTab("Control Points", null, panel_policy, null);
        tabbedPane.setEnabledAt(1, true);
        panel_policy.setLayout(new MigLayout("", "[326px,grow,fill][526px,grow,fill][731px,grow,fill]", "[815px,grow]"));

        JScrollPane policy_scroll_bar = new JScrollPane(panel_policy);
        scroll_bar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_bar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.add(policy_scroll_bar, "Control Points");


        JLabel control_point_count = new JLabel("CP & Appendix Name");
        control_point_count.setHorizontalAlignment(JLabel.RIGHT);
        panel_policy.add(control_point_count, BorderLayout.NORTH);

        JPanel panel_tree = new JPanel();
        panel_tree.setAutoscrolls(true);
        panel_policy.add(panel_tree, "cell 0 0,grow");


        JPanel panel_meta = new JPanel();
        panel_policy.add(panel_meta, "cell 1 0,grow");
        GridBagLayout gbl_panel_meta = new GridBagLayout();
        gbl_panel_meta.columnWidths = new int[]{145};
        gbl_panel_meta.rowHeights = new int[]{35, 134, 150, 150, 25, 0, 25};
        gbl_panel_meta.columnWeights = new double[]{1.0};
        gbl_panel_meta.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 1.0};
        panel_meta.setLayout(gbl_panel_meta);

        JPanel panel_title = new JPanel();
        GridBagConstraints gbc_panel_title = new GridBagConstraints();
        gbc_panel_title.fill = GridBagConstraints.BOTH;
        gbc_panel_title.insets = new Insets(0, 0, 5, 0);
        gbc_panel_title.gridx = 0;
        gbc_panel_title.gridy = 0;
        panel_meta.add(panel_title, gbc_panel_title);
        panel_title.setLayout(new BorderLayout(0, 0));

        JLabel lblTitle = new JLabel("Title");
        lblTitle.setVerticalAlignment(SwingConstants.TOP);
        panel_title.add(lblTitle, BorderLayout.NORTH);

        text_title = new JTextField();
        text_title.setMinimumSize(new Dimension(4, 25));
        text_title.setPreferredSize(new Dimension(4, 25));
        text_title.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        lblTitle.setLabelFor(text_title);
        panel_title.add(text_title, BorderLayout.CENTER);
        text_title.setColumns(50);

        JPanel panel_version = new JPanel();
        panel_version.setAlignmentY(Component.TOP_ALIGNMENT);
        GridBagConstraints gbc_panel_version = new GridBagConstraints();
        gbc_panel_version.insets = new Insets(0, 0, 5, 0);
        gbc_panel_version.fill = GridBagConstraints.BOTH;
        gbc_panel_version.gridx = 0;
        gbc_panel_version.gridy = 1;
        panel_meta.add(panel_version, gbc_panel_version);
        GridBagLayout gbl_panel_version = new GridBagLayout();
        gbl_panel_version.columnWidths = new int[]{60, 270};
        gbl_panel_version.rowHeights = new int[]{20, 150, 30};
        gbl_panel_version.columnWeights = new double[]{1.0, 1.0};
        gbl_panel_version.rowWeights = new double[]{1.0, 1.0, 1.0};
        panel_version.setLayout(gbl_panel_version);

        JLabel label_version = new JLabel("CP Versioning");
        label_version.setVerticalTextPosition(SwingConstants.BOTTOM);
        label_version.setVerticalAlignment(SwingConstants.BOTTOM);
        label_version.setAlignmentY(Component.TOP_ALIGNMENT);
        GridBagConstraints gbc_label_version = new GridBagConstraints();
        gbc_label_version.fill = GridBagConstraints.BOTH;
        gbc_label_version.insets = new Insets(0, 0, 5, 5);
        gbc_label_version.gridx = 0;
        gbc_label_version.gridy = 0;
        panel_version.add(label_version, gbc_label_version);


        JTextArea text_version_comment = new JTextArea();
        text_version_comment.setLineWrap(true);
        text_version_comment.setWrapStyleWord(true);
        text_version_comment.setAlignmentY(Component.TOP_ALIGNMENT);
        text_version_comment.setAlignmentX(Component.LEFT_ALIGNMENT);
        text_version_comment.setPreferredSize(new Dimension(270, 69));
        text_version_comment.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        GridBagConstraints gbc_text_version_comment = new GridBagConstraints();
        gbc_text_version_comment.fill = GridBagConstraints.BOTH;
        gbc_text_version_comment.gridx = 1;
        gbc_text_version_comment.gridy = 1;
        panel_version.add(text_version_comment, gbc_text_version_comment);
        final JScrollPane scrollPane_versions = new JScrollPane(text_version_comment);
        scrollPane_versions.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollPane_versions.setPreferredSize(new Dimension(270, 69));
        GridBagConstraints gbc_scrollPane_versions = new GridBagConstraints();
        gbc_scrollPane_versions.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_versions.anchor = GridBagConstraints.NORTH;
        gbc_scrollPane_versions.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_versions.gridx = 1;
        gbc_scrollPane_versions.gridy = 1;
        panel_version.add(scrollPane_versions, gbc_scrollPane_versions);

        JList<?> list_versions = new JList();
        list_versions.setMinimumSize(new Dimension(0, 150));
        list_versions.setSize(new Dimension(0, 270));
        list_versions.setVisibleRowCount(4);
        list_versions.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));


        GridBagConstraints gbc_list_versions = new GridBagConstraints();
        gbc_list_versions.insets = new Insets(0, 0, 5, 5);
        gbc_list_versions.anchor = GridBagConstraints.NORTH;
        gbc_list_versions.fill = GridBagConstraints.HORIZONTAL;
        gbc_list_versions.gridx = 0;
        gbc_list_versions.gridy = 1;

        final JScrollPane scrollPane_versions_list = new JScrollPane(list_versions);
        scrollPane_versions_list.setAlignmentY(Component.TOP_ALIGNMENT);
        scrollPane_versions_list.setOpaque(false);
        scrollPane_versions_list.setMinimumSize(new Dimension(22, 150));
        panel_version.add(scrollPane_versions_list, gbc_list_versions);

        JPanel panel_versoin_buttons = new JPanel();
        GridBagConstraints gbc_panel_versoin_buttons = new GridBagConstraints();
        gbc_panel_versoin_buttons.insets = new Insets(0, 0, 0, 5);
        gbc_panel_versoin_buttons.fill = GridBagConstraints.BOTH;
        gbc_panel_versoin_buttons.gridx = 0;
        gbc_panel_versoin_buttons.gridy = 2;
        panel_version.add(panel_versoin_buttons, gbc_panel_versoin_buttons);

        JButton btnAddVersion = new JButton("Add version");
        panel_versoin_buttons.add(btnAddVersion);

        JButton btnRemove = new JButton("Remove last");
        panel_versoin_buttons.add(btnRemove);

        JButton btnSaveDescription = new JButton("Save version description");

        GridBagConstraints gbc_btnSaveDescription = new GridBagConstraints();
        gbc_btnSaveDescription.gridx = 1;
        gbc_btnSaveDescription.gridy = 2;
        panel_version.add(btnSaveDescription, gbc_btnSaveDescription);


        JPanel panel_description = new JPanel();
        GridBagConstraints gbc_panel_description = new GridBagConstraints();
        gbc_panel_description.insets = new Insets(0, 0, 5, 0);
        gbc_panel_description.fill = GridBagConstraints.BOTH;
        gbc_panel_description.gridx = 0;
        gbc_panel_description.gridy = 2;
        panel_meta.add(panel_description, gbc_panel_description);
        panel_description.setLayout(new BorderLayout(0, 5));

        JLabel label_description = new JLabel("Description");
        label_description.setVerticalAlignment(SwingConstants.TOP);
        panel_description.add(label_description, BorderLayout.NORTH);
        label_description.setHorizontalTextPosition(SwingConstants.LEADING);

        JTextArea editor_description = new JTextArea();
        editor_description.setLineWrap(true);
        editor_description.setWrapStyleWord(true);
        panel_description.add(editor_description, BorderLayout.CENTER);
        editor_description.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        label_description.setLabelFor(editor_description);
        final JScrollPane scrollPane_description = new JScrollPane(editor_description);
        panel_description.add(scrollPane_description);

        JPanel panel_violation = new JPanel();
        GridBagConstraints gbc_panel_violation = new GridBagConstraints();
        gbc_panel_violation.insets = new Insets(0, 0, 5, 0);
        gbc_panel_violation.fill = GridBagConstraints.BOTH;
        gbc_panel_violation.gridx = 0;
        gbc_panel_violation.gridy = 3;
        panel_meta.add(panel_violation, gbc_panel_violation);
        panel_violation.setLayout(new BorderLayout(0, 5));

        JTextArea editor_violation = new JTextArea();
        editor_violation.setLineWrap(true);
        editor_violation.setWrapStyleWord(true);
        panel_violation.add(editor_violation, BorderLayout.CENTER);
        editor_violation.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

        JLabel label_violation = new JLabel("Violation Message");
        label_violation.setVerticalAlignment(SwingConstants.TOP);
        panel_violation.add(label_violation, BorderLayout.NORTH);
        label_violation.setLabelFor(editor_violation);
        final JScrollPane scrollPane_violation = new JScrollPane(editor_violation);
        panel_violation.add(scrollPane_violation);

        JPanel panel_priority = new JPanel();
        GridBagConstraints gbc_panel_priority = new GridBagConstraints();
        gbc_panel_priority.insets = new Insets(0, 0, 5, 0);
        gbc_panel_priority.fill = GridBagConstraints.BOTH;
        gbc_panel_priority.gridx = 0;
        gbc_panel_priority.gridy = 4;
        panel_meta.add(panel_priority, gbc_panel_priority);

        ButtonGroup btngrp = new ButtonGroup();
        JRadioButton rdb_prio_informational = new JRadioButton("Informational");
        rdb_prio_informational.setForeground(Color.BLACK);
        rdb_prio_informational.setBackground(Color.CYAN);

        JRadioButton rdb_prio_low = new JRadioButton("Low");
        rdb_prio_low.setBackground(new Color(51, 204, 0));
        rdb_prio_low.setActionCommand("Low");

        JRadioButton rdb_prio_normal = new JRadioButton("Normal");
        rdb_prio_normal.setBackground(new Color(255, 102, 0));
        panel_priority.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel label_priority = new JLabel("Priority:");
        panel_priority.add(label_priority);
        panel_priority.add(rdb_prio_informational);
        panel_priority.add(rdb_prio_low);
        panel_priority.add(rdb_prio_normal);

        JRadioButton rdb_prio_high = new JRadioButton("High");
        rdb_prio_high.setBackground(new Color(204, 0, 0));
        panel_priority.add(rdb_prio_high);
        btngrp.add(rdb_prio_informational);
        btngrp.add(rdb_prio_low);
        btngrp.add(rdb_prio_normal);
        rdb_prio_normal.setSelected(true);
        btngrp.add(rdb_prio_high);

        JPanel panel_save = new JPanel();
        GridBagConstraints gbc_panel_save = new GridBagConstraints();
        gbc_panel_save.insets = new Insets(0, 0, 5, 0);
        gbc_panel_save.fill = GridBagConstraints.BOTH;
        gbc_panel_save.gridx = 0;
        gbc_panel_save.gridy = 5;
        panel_meta.add(panel_save, gbc_panel_save);

        JPanel panel_workProgress = new JPanel();
        GridBagConstraints gbc_panel_work_progress = new GridBagConstraints();
        gbc_panel_work_progress.insets = new Insets(0, 0, 2, 0);
        gbc_panel_work_progress.fill = GridBagConstraints.BOTH;
        gbc_panel_work_progress.gridx = 0;
        gbc_panel_work_progress.gridy = 5;
        panel_meta.add(panel_workProgress, gbc_panel_work_progress);

        Border border = BorderFactory.createTitledBorder("SQLStatus");
        panel_workProgress.setBorder(border);
        ButtonGroup btnWorkgroup = new ButtonGroup();

        JRadioButton rdb_prio_notstarted = new JRadioButton("not started");
        // rdb_prio_notstarted.setBackground(Color.red);
        rdb_prio_notstarted.setActionCommand("not started");
        panel_workProgress.add(rdb_prio_notstarted);
        rdb_prio_notstarted.addActionListener(this::actionPerformed);

        JRadioButton rdb_prio_progress = new JRadioButton("progress");
        //rdb_prio_progress.setBackground(Color.orange);
        rdb_prio_progress.setActionCommand("progress");
        panel_workProgress.add(rdb_prio_progress);
        rdb_prio_progress.addActionListener(this::actionPerformed);

        JRadioButton rdb_prio_completed = new JRadioButton("completed");
        //rdb_prio_completed.setBackground(Color.green);
        rdb_prio_completed.setActionCommand("completed");
        panel_workProgress.add(rdb_prio_completed);
        rdb_prio_completed.addActionListener(this::actionPerformed);


        btnWorkgroup.add(rdb_prio_notstarted);
        btnWorkgroup.add(rdb_prio_progress);
        rdb_prio_notstarted.setSelected(true);
        btnWorkgroup.add(rdb_prio_completed);
        panel_workProgress.setVisible(false);


        panel_priority.add(panel_workProgress, BorderLayout.SOUTH);


        JLabel lblDoNotForget = new JLabel("Do not forget to SAVE your changes before you switch to another check!");
        lblDoNotForget.setForeground(Color.RED);
        GridBagConstraints gbc_lblDoNotForget = new GridBagConstraints();
        gbc_lblDoNotForget.gridx = 0;
        gbc_lblDoNotForget.gridy = 6;
        panel_meta.add(lblDoNotForget, gbc_lblDoNotForget);


        JPanel panel_sql = new JPanel();
        panel_policy.add(panel_sql, "cell 2 0,grow");
        panel_sql.setLayout(new BorderLayout(0, 0));

        JLabel label_sql = new JLabel("SQL");
        panel_sql.add(label_sql, BorderLayout.NORTH);

        RSyntaxTextArea editor_sql = new RSyntaxTextArea();
        panel_sql.add(editor_sql, BorderLayout.CENTER);

        editor_sql.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        editor_sql.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        editor_sql.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(editor_sql);
        panel_sql.add(sp);

        JPanel panel_collectors_check = new JPanel();
        panel_collectors_check.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel_collectors_check.setPreferredSize(new Dimension(0, 250));
        panel_collectors_check.setSize(new Dimension(0, 200));
        panel_sql.add(panel_collectors_check, BorderLayout.SOUTH);
        panel_collectors_check.setLayout(new BorderLayout(0, 0));

        JLabel lblCollectorList = new JLabel("List of collectors required for this check");
        panel_collectors_check.add(lblCollectorList, BorderLayout.NORTH);

        JPanel panel_check_col_detail = new JPanel();
        panel_collectors_check.add(panel_check_col_detail, BorderLayout.CENTER);
        panel_check_col_detail.setLayout(new BorderLayout(0, 0));

        JComboBox comboBox_table_list = new JComboBox();
        panel_check_col_detail.add(comboBox_table_list, BorderLayout.NORTH);

        panel_tree.setLayout(new BorderLayout(0, 0));


        JTextPane textPane_check_col_columns = new JTextPane();
        textPane_check_col_columns.setEditable(false);

        final JScrollPane rules_scrollPane_columns = new JScrollPane(textPane_check_col_columns);
        panel_check_col_detail.add(rules_scrollPane_columns, BorderLayout.CENTER);

        JPanel panel_col_list = new JPanel();
        panel_collectors_check.add(panel_col_list, BorderLayout.WEST);
        panel_col_list.setLayout(new BorderLayout(0, 0));

        JList check_collectors = new JList();
        check_collectors.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        check_collectors.setPreferredSize(new Dimension(300, 0));
        check_collectors.setSize(new Dimension(200, 0));
        final JScrollPane scrollPane_collectors_check_list = new JScrollPane(check_collectors);
        panel_col_list.add(scrollPane_collectors_check_list, BorderLayout.CENTER);

        scrollPane_collectors_check_list.setAutoscrolls(true);

        JPanel panel_col_list_buttons = new JPanel();
        panel_col_list.add(panel_col_list_buttons, BorderLayout.SOUTH);

        JButton btn_check_col_add = new JButton("Add collector");
        panel_col_list_buttons.add(btn_check_col_add);

        JButton btn_check_col_remove = new JButton("Remove collector");
        panel_col_list_buttons.add(btn_check_col_remove);


        JTree tree_checks = new JTree();
        tree_checks.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("JTree") {
                    {
                    }
                }
        ));
        tree_checks.setRootVisible(false);
        tree_checks.setEditable(true);
        tree_checks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {

                }
            }
        });

        tree_checks.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                /*JMenuItem rename = new JMenuItem("Rename");
                JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));
                p.setPreferredSize(new Dimension(200, 40));
                p.add(rename);
                p.add(new JSeparator());
                rename.setEnabled(true);JOptionPane.showMessageDialog(null, "Enter SQL COMMAND",
                        "ALERT MESSAGE",
                        JOptionPane.WARNING_MESSAGE);*/
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSelectionEvent
                        .getPath().getLastPathComponent();
                final TreePopup treePopup = new TreePopup(node, frmPolicyEditor, tree_checks);

                tree_checks.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            treePopup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
                // treePopup.renameNode(node);
                System.out.println("selected node:" + node);
            }
        });
        tree_checks.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        final JScrollPane rules_scrollPane = new JScrollPane(tree_checks);
        panel_tree.add(rules_scrollPane, BorderLayout.CENTER);
        panel_save.setLayout(new BorderLayout(0, 0));

        JButton button_save = new JButton("Save changes");
        button_save.setBackground(UIManager.getColor("Button.background"));
        button_save.setForeground(Color.BLACK);
        button_save.setFont(new Font("Dialog", Font.BOLD, 12));
        panel_save.add(button_save, BorderLayout.WEST);


        JButton runsql = new JButton("Run SQL (test control point)");
        panel_save.add(runsql, BorderLayout.EAST);


        button_save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DefaultMutableTreeNode node_check = (DefaultMutableTreeNode) tree_checks.getLastSelectedPathComponent();

                /* if nothing is selected */
                if (node_check == null) return;
                /* retrieve the node that was selected */
                Rule node_rule_check = (Rule) node_check.getUserObject();
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
          /*      try {
                    pol.build("");
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }

        });


        // CHECK TREE HERE
        GridBagConstraints gbc_rule_tree = new GridBagConstraints();
        gbc_rule_tree.anchor = GridBagConstraints.NORTH;
        gbc_rule_tree.fill = GridBagConstraints.HORIZONTAL;
        gbc_rule_tree.gridx = 0;
        gbc_rule_tree.gridy = 1;

        JLabel lblPolicyContent = new JLabel("Policy Content");
        panel_tree.add(lblPolicyContent, BorderLayout.NORTH);

        lblPolicyContent.setLabelFor(tree_checks);

        JPanel panel = new JPanel();
        panel_tree.add(panel, BorderLayout.SOUTH);
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);

      /* JButton btnNewCheck = new JButton("New Check");
        btnNewCheck.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(btnNewCheck);
        btnNewCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionListeners.btnNewCheckActionPerform(frmPolicyEditor, pol, tree_checks);
            }
        });


        JButton btnRemoveCheck = new JButton("Remove Check");
        btnRemoveCheck.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(btnRemoveCheck);
        btnRemoveCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionListeners.btnRemoveCheckActionPerform(tree_checks, frmPolicyEditor, pol);
            }
        });*/
        JPanel panel_general_cp_sql_edit = new JPanel();
        panel_general_cp_sql_edit.setLayout(new BorderLayout());


        JPanel panel_CP_SQL_Edit = new JPanel(new GridLayout(1, 0, 0, 2));
        panel_CP_SQL_Edit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("CP-SQL-Edit", null, panel_CP_SQL_Edit, null);
        panel_CP_SQL_Edit.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        JScrollPane scrollbar_cp_sql_edit = new JScrollPane(panel_general_cp_sql_edit
        );
        scrollbar_cp_sql_edit.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollbar_cp_sql_edit.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.add(scrollbar_cp_sql_edit, "CP-SQL-Edit");

        JPanel panel_violation_message = new JPanel();
        JLabel lblcpviol = new JLabel("violation description");
        panel_violation_message.add(lblcpviol, BorderLayout.LINE_START);
        JTextArea textArea_violation = new JTextArea(2, 100);
        panel_violation_message.add(textArea_violation, BorderLayout.CENTER);
        textArea_violation.setEditable(false);

        JScrollPane scrollpane_for_panel_violation = new JScrollPane(textArea_violation);
        scrollpane_for_panel_violation.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel_violation_message.add(scrollpane_for_panel_violation);
        panel_general_cp_sql_edit.add(panel_violation_message, BorderLayout.NORTH);

        JPanel panel_sql_tables = new JPanel(new GridLayout(1, 2, 5, 50));
        panel_sql_tables.setPreferredSize(new Dimension(600, 600));
        panel_sql_tables.setBorder(BorderFactory.createLineBorder(Color.ORANGE));

        JPanel panel_cplgeneral_title = new JPanel();
        panel_cplgeneral_title.setBorder(BorderFactory.createEmptyBorder(500, 5, 50, 10));
        panel_sql_tables.add(panel_cplgeneral_title, "cell 0 0");
        panel_cplgeneral_title.setLayout(new BorderLayout(0, 0));

        JLabel lblcpPolicyTitle = new JLabel("Sql Edit");
        panel_cplgeneral_title.add(lblcpPolicyTitle, BorderLayout.NORTH);
        panel_cplgeneral_title.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        JTextArea text_area_title = new JTextArea();
        text_area_title.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        text_area_title.setPreferredSize(new Dimension(500, 550));
        text_area_title.setCaretPosition(0);
        text_area_title.setWrapStyleWord(true);
        text_area_title.setLineWrap(true);
        panel_cplgeneral_title.add(text_area_title, BorderLayout.CENTER);

        JPanel panel_sqledit = new JPanel();
        panel_sql_tables.add(panel_sqledit, "cell 0 1");
        panel_sqledit.setLayout(new BorderLayout(0, 0));

        JLabel lblsql = new JLabel("Tables");
        panel_sqledit.add(lblsql, BorderLayout.NORTH);

        JTable table_cpedit = new JTable();
        table_cpedit.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table_cpedit.setFillsViewportHeight(true);

        JScrollPane scrollPane_table_cpedit = new JScrollPane(table_cpedit);
        scrollPane_table_cpedit.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_table_cpedit.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel_sqledit.add(scrollPane_table_cpedit, BorderLayout.CENTER);

        MutableComboBoxModel combobox_table_cpedit = new DefaultComboBoxModel();
        JComboBox comboBox_tables_cpedit = new JComboBox(combobox_table_cpedit);
        comboBox_tables_cpedit.setMaximumRowCount(10);
        panel_sqledit.add(comboBox_tables_cpedit, BorderLayout.SOUTH);
        panel_CP_SQL_Edit.add(panel_sql_tables);

        JPanel panel_cp_sql_edit_button = new JPanel();
        JButton button_run_cp_sql = new JButton("execute");
        button_run_cp_sql.setFont(new Font("Dialog", Font.BOLD, 11));
        button_run_cp_sql.setVisible(true);
        button_run_cp_sql.setPreferredSize(new Dimension(100, 50));
        button_run_cp_sql.setMaximumSize(new Dimension(120, 35));
        button_run_cp_sql.setMinimumSize(new Dimension(80, 35));
        panel_cp_sql_edit_button.add(button_run_cp_sql);
        panel_cp_sql_edit_button.setBorder(BorderFactory.createLineBorder(Color.blue));


        panel_general_cp_sql_edit.add(panel_CP_SQL_Edit, BorderLayout.CENTER);
        panel_general_cp_sql_edit.add(panel_cp_sql_edit_button, BorderLayout.SOUTH);
        panel_general_cp_sql_edit.setVisible(true);


        JPanel panel_parameters = new JPanel();
        tabbedPane.addTab("Policy parameters", null, panel_parameters, null);
        panel_parameters.setLayout(new MigLayout("", "[250][326px,grow][grow,fill]", "[grow][40px]"));

        JScrollPane scrollPane_params = new JScrollPane();
        scrollPane_params.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel_parameters.add(scrollPane_params, "cell 0 0,grow");


        JList list_params = new JList();
        list_params.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

        scrollPane_params.setColumnHeaderView(list_params);
        scrollPane_params.setViewportView(list_params);

        JPanel panel_param_detail = new JPanel();
        panel_parameters.add(panel_param_detail, "cell 1 0,grow");
        panel_param_detail.setLayout(new BorderLayout(0, 5));

        JComboBox comboBox_paramtype = new JComboBox();
        comboBox_paramtype.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        comboBox_paramtype.setModel(new DefaultComboBoxModel(new String[]{"String value", "String list", "Integer value", "Integer list"}));
        panel_param_detail.add(comboBox_paramtype, BorderLayout.NORTH);

        JScrollPane scrollPane_param_detail = new JScrollPane();
        panel_param_detail.add(scrollPane_param_detail, BorderLayout.CENTER);

        JTextArea textArea_param_detail = new JTextArea();
        textArea_param_detail.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane_param_detail.setViewportView(textArea_param_detail);

        JButton btnNewParameter = new JButton("New parameter");
        panel_parameters.add(btnNewParameter, "flowx,cell 0 1");

        JButton btnDeleteParameter = new JButton("Delete parameter");
        panel_parameters.add(btnDeleteParameter, "cell 0 1");

        JButton btnSaveParameter = new JButton("Save parameter");
        panel_parameters.add(btnSaveParameter, "cell 1 1,alignx center");

        JPanel panel_collectors = new JPanel();
        tabbedPane.addTab("Collectors", null, panel_collectors, null);
        tabbedPane.setEnabledAt(3, true);
        panel_collectors.setLayout(new MigLayout("", "[300px][1295px,grow]", "[815px,grow]"));

        JPanel panel_col_detail = new JPanel();
        panel_collectors.add(panel_col_detail, "cell 1 0,grow");
        panel_col_detail.setLayout(new BorderLayout(0, 0));

        JPanel panel_col_description = new JPanel();
        panel_col_description.setPreferredSize(new Dimension(0, 300));
        panel_col_detail.add(panel_col_description, BorderLayout.NORTH);
        panel_col_description.setLayout(new BorderLayout(0, 0));

        JTextPane textPane_col_info = new JTextPane();
        textPane_col_info.setEditable(false);
        textPane_col_info.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panel_col_description.add(textPane_col_info, BorderLayout.CENTER);
        final JScrollPane scrollPane_collectors_info = new JScrollPane(textPane_col_info);
        panel_col_description.add(scrollPane_collectors_info, BorderLayout.CENTER);

        scrollPane_collectors_info.setAutoscrolls(true);

        JPanel panel_col_parameters = new JPanel();
        panel_col_detail.add(panel_col_parameters, BorderLayout.CENTER);
        panel_col_parameters.setLayout(new BorderLayout(0, 0));

        JLabel lbl_col_params = new JLabel("Collector parameters");
        panel_col_parameters.add(lbl_col_params, BorderLayout.NORTH);

        JPanel panel_colparams = new JPanel();
        panel_col_parameters.add(panel_colparams, BorderLayout.WEST);
        panel_colparams.setLayout(new BorderLayout(0, 0));

        JList list_col_params = new JList();
        panel_colparams.add(list_col_params);
        list_col_params.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        list_col_params.setPreferredSize(new Dimension(300, 0));
        final JScrollPane scrollPane_col_param_list = new JScrollPane(list_col_params);
        panel_colparams.add(scrollPane_col_param_list, BorderLayout.CENTER);

        scrollPane_col_param_list.setAutoscrolls(true);

        JPanel panel_col_params_buttons = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_col_params_buttons.getLayout();
        flowLayout_1.setVgap(0);
        panel_colparams.add(panel_col_params_buttons, BorderLayout.SOUTH);

        JButton btn_col_add_param = new JButton("Add parameter");
        btn_col_add_param.setFont(new Font("Dialog", Font.BOLD, 11));
        btn_col_add_param.setPreferredSize(new Dimension(150, 25));
        panel_col_params_buttons.add(btn_col_add_param);
        btn_col_add_param.setMaximumSize(new Dimension(80, 25));
        btn_col_add_param.setMinimumSize(new Dimension(80, 25));

        JButton btn_col_delete_param = new JButton("Delete parameter");
        btn_col_delete_param.setFont(new Font("Dialog", Font.BOLD, 11));
        panel_col_params_buttons.add(btn_col_delete_param);
        btn_col_delete_param.setMinimumSize(new Dimension(80, 25));
        btn_col_delete_param.setPreferredSize(new Dimension(150, 25));

        JPanel panel_col_param_values = new JPanel();
        panel_col_parameters.add(panel_col_param_values, BorderLayout.CENTER);
        panel_col_param_values.setLayout(new BorderLayout(5, 0));


        JButton btn_col_save_param = new JButton("Save parameter");
        btn_col_save_param.setFont(new Font("Dialog", Font.BOLD, 11));
        panel_col_param_values.add(btn_col_save_param, BorderLayout.SOUTH);

        JTextArea textArea_col_param_values = new JTextArea();
        panel_col_param_values.add(textArea_col_param_values, BorderLayout.CENTER);
        textArea_col_param_values.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        final JScrollPane scrollPane_col_param_values = new JScrollPane(textArea_col_param_values);
        panel_col_param_values.add(scrollPane_col_param_values, BorderLayout.CENTER);

        scrollPane_col_param_values.setAutoscrolls(true);

        JPanel panel_col_tree = new JPanel();
        panel_col_tree.setPreferredSize(new Dimension(300, 0));
        panel_collectors.add(panel_col_tree, "cell 0 0,grow");
        panel_col_tree.setLayout(new BorderLayout(0, 0));

        JTree tree_collectors = new JTree();
        //tree_collectors.setMaximumSize(new Dimension(30000, 30000));
        tree_collectors.setAutoscrolls(true);
        //tree_collectors.setPreferredSize(new Dimension(0, 0));
        tree_collectors.setModel(new DefaultTreeModel(
                new DefaultMutableTreeNode("JTree") {
                    {
                    }
                }
        ));

        btn_col_save_param.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionListeners.btn_col_save_paramActionPerform(list_col_params, tree_collectors, textArea_col_param_values);
            }
        });


        tree_collectors.setRootVisible(false);
        tree_collectors.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        final JScrollPane scrollPane_collectors = new JScrollPane(tree_collectors);
        panel_col_tree.add(scrollPane_collectors, BorderLayout.CENTER);

        scrollPane_collectors.setAutoscrolls(true);

        JPanel panel_col_param_buttons = new JPanel();
        panel_col_param_buttons.setPreferredSize(new Dimension(300, 25));
        panel_col_tree.add(panel_col_param_buttons, BorderLayout.SOUTH);
        panel_col_param_buttons.setLayout(new BorderLayout(0, 0));

        JButton btn_col_add_collector = new JButton("Add collector");
        btn_col_add_collector.setFont(new Font("Dialog", Font.BOLD, 11));
        btn_col_add_collector.setActionCommand("");
        btn_col_add_collector.setPreferredSize(new Dimension(150, 25));
        panel_col_param_buttons.add(btn_col_add_collector, BorderLayout.WEST);

        JButton btn_col_delete_collector = new JButton("Delete collector");
        btn_col_delete_collector.setFont(new Font("Dialog", Font.BOLD, 11));
        btn_col_delete_collector.setMaximumSize(new Dimension(135, 25));
        btn_col_delete_collector.setMinimumSize(new Dimension(135, 25));
        btn_col_delete_collector.setPreferredSize(new Dimension(150, 25));
        btn_col_delete_collector.setSize(new Dimension(135, 25));
        panel_col_param_buttons.add(btn_col_delete_collector, BorderLayout.CENTER);

        JPanel panel_messages = new JPanel();
        tabbedPane.addTab("Messages", null, panel_messages, null);
        panel_messages.setLayout(new BorderLayout(0, 0));

        JTextArea textArea_messages = new JTextArea();

        JScrollPane scrollPane_messages = new JScrollPane(textArea_messages);
        panel_messages.add(scrollPane_messages, BorderLayout.CENTER);
        textArea_messages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        textArea_messages.setEditable(false);

        // DATABASE PANEL
        //JPanel panel_database = new DatabaseWindow().getPanel();
        JPanel panel_database = new JPanel();
        tabbedPane.addTab("Database", null, panel_database, null);
        panel_database.setLayout(new MigLayout("", "[326px,grow,fill]", "[815px,grow]"));
        JPanel panel_tables = new JPanel();
        panel_database.add(panel_tables, "cell 0 0,grow");
        panel_tables.setLayout(new BorderLayout(0, 0));

        JScrollPane database_scroll_bar = new JScrollPane(panel_database);
        scroll_bar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll_bar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.add(database_scroll_bar, "Database");

        JPanel panel_database_btns = new JPanel();
        JButton btn_default_db = new JButton("Default Database");
        panel_database_btns.add(btn_default_db, BorderLayout.WEST);
        MutableComboBoxModel cbm = new DefaultComboBoxModel();
        JComboBox comboBox_tables = new JComboBox(cbm);
        btn_default_db.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_other_db.setVisible(false);
                btn_default_db.setVisible(false);
                File dbDir = new File("JAC_DATA");
                boolean exists = dbDir.exists();
                tabbedPane.setEnabledAt(5, false);

                if (exists) {
                    tabbedPane.setEnabledAt(5, true);
                    db = new Database(new String[0]);
                    for (int i = 0; i < db.tables.size(); i++) {
                        cbm.addElement(db.tables.get(i));
                    }
                    // comboBox_tables = new JComboBox(cbm);
                    comboBox_tables.setMaximumRowCount(15);
                    panel_tables.removeAll();
                    panel_tables.add(comboBox_tables, BorderLayout.NORTH);
                    JTable table_data = new JTable();
                    table_data.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                    JScrollPane scrollPane_table = new JScrollPane(table_data);
                    scrollPane_table.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    table_data.setFillsViewportHeight(true);
                    panel_tables.add(scrollPane_table, BorderLayout.CENTER);
                    comboBox_tables.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JComboBox cb = (JComboBox) e.getSource();
                            Table table = (Table) cb.getSelectedItem();
                            loadGUITable(table, db);
                        }

                        private void loadGUITable(Table table, Database db) {
                            //System.out.println("Loading database table");
                            String[] columnNames = new String[table.columns.size()];
                            table.loadValues(db);
                            String[][] data = new String[table.values.size()][table.columns.size()];
                            for (int cols = 0; cols < table.columns.size(); cols++) {
                                columnNames[cols] = table.columns.get(cols).name;
                                //System.out.println("Printing table name: " + columnNames[cols]);
                            }
                            for (int rows = 0; rows < table.values.size(); rows++) {
                                for (int rcol = 0; rcol < table.columns.size(); rcol++) {
                                    data[rows][rcol] = table.values.get(rows)[rcol];
                                    //System.out.println("Saving data: " + data[rows][rcol]);
                                }
                            }
                            DatabaseWindow.DBTableModel model = new DatabaseWindow.DBTableModel(columnNames, data);
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

                    });
                    panel_tables.setVisible(true);
                    comboBox_tables.setVisible(true);

                }
            }
        });
        btn_default_db.setVisible(false);
        JPanel panel_refresh = new JPanel();

        JButton btn_refresh = new JButton("Refresh");
        panel_refresh.add(btn_refresh, BorderLayout.NORTH);
        panel_database.add(panel_refresh, BorderLayout.WEST);
        btn_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_default_db.setVisible(false);
                btn_other_db.setVisible(true);
                comboBox_tables.setVisible(false);
                panel_tables.setVisible(false);

            }
        });

        // JButton btn_other_db = new JButton("Load Database-Manually");
        panel_database_btns.add(btn_other_db, BorderLayout.SOUTH);
        String[] str = new String[]{""};
        final File[] trial_file = {new File("")};

        panel_database.add(panel_database_btns, BorderLayout.NORTH);
        btn_other_db.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_default_db.setVisible(false);
                btn_other_db.setVisible(false);

                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("JAC_DATA selection");
                chooser.showOpenDialog(frmPolicyEditor);
                System.out.println("getCurrentDirectory(): "
                        + chooser.getSelectedFile());
                trial_file[0] = chooser.getSelectedFile();

                boolean exists = chooser.getSelectedFile().exists();
                if (exists) {
                    str[0] = chooser.getSelectedFile().getAbsolutePath();


                    LoadDatabase database = new LoadDatabase(str);
                    MutableComboBoxModel combo = new DefaultComboBoxModel();
                    MutableComboBoxModel combobox_table_cpedit = new DefaultComboBoxModel();
                    for (int i = 0; i < database.tables.size(); i++) {
                        combo.addElement(database.tables.get(i));
                        combobox_table_cpedit.addElement(database.tables.get(i));
                        System.out.println("combobox data:" + database.tables.get(i));
                    }
                    JComboBox comboBox_other_tables = new JComboBox(combo);
                    comboBox_other_tables.setMaximumRowCount(15);
                    panel_tables.removeAll();
                    panel_tables.add(comboBox_other_tables, BorderLayout.NORTH);

                    JTable other_table_data = new JTable();
                    other_table_data.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                    JScrollPane scrollPane_table = new JScrollPane(other_table_data);
                    scrollPane_table.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    other_table_data.setFillsViewportHeight(true);
                    panel_tables.add(scrollPane_table, BorderLayout.CENTER);

                    JComboBox comboBox_tables_cpedit = new JComboBox(combobox_table_cpedit);
                    comboBox_tables_cpedit.setMaximumRowCount(10);
                    panel_sqledit.removeAll();
                    panel_sqledit.add(comboBox_tables_cpedit, BorderLayout.SOUTH);

                    JTable table_cpedit = new JTable();
                    table_cpedit.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                    JScrollPane scrollPane_table_cpedit = new JScrollPane(table_cpedit);
                    scrollPane_table_cpedit.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane_table_cpedit.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    table_cpedit.setFillsViewportHeight(true);
                    panel_sqledit.add(scrollPane_table_cpedit, BorderLayout.CENTER);

                    comboBox_other_tables.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JComboBox cb_other_tables = (JComboBox) e.getSource();
                            Table table = (Table) cb_other_tables.getSelectedItem();
                            loadGUITable(table, database);
                        }

                        private void loadGUITable(Table other_table, LoadDatabase db) {

                            String[] other_table_columnNames = new String[other_table.columns.size()];
                            other_table.loadOtherTableValues(db);
                            String[][] data = new String[other_table.values.size()][other_table.columns.size()];
                            for (int cols = 0; cols < other_table.columns.size(); cols++) {
                                other_table_columnNames[cols] = other_table.columns.get(cols).name;
                                //System.out.println("Printing table name: " + columnNames[cols]);
                            }
                            for (int rows = 0; rows < other_table.values.size(); rows++) {
                                for (int rcol = 0; rcol < other_table.columns.size(); rcol++) {
                                    data[rows][rcol] = other_table.values.get(rows)[rcol];
                                    //System.out.println("Saving data: " + data[rows][rcol]);
                                }
                            }
                            DatabaseWindow.DBTableModel model = new DatabaseWindow.DBTableModel(other_table_columnNames, data);
                            other_table_data.setModel(model);
                            TableColumnModel columnModel = other_table_data.getColumnModel();
                            if (columnModel.getColumnCount() > 20) {
                                other_table_data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            } else {
                                other_table_data.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                            }
                            for (int colm = 0; colm < columnModel.getColumnCount(); colm++) {
                                columnModel.getColumn(colm).setPreferredWidth(80);
                            }
                        }

                    });
                    panel_tables.setVisible(true);
                    //panel_other_tables.setVisible(true);

                    comboBox_tables_cpedit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox cb_cp_edit = (JComboBox) actionEvent.getSource();
                            Table table_cp_edit = (Table) cb_cp_edit.getSelectedItem();
                            loadTable_for_cp_edit(table_cp_edit, database);
                        }

                        private void loadTable_for_cp_edit(Table table_content_for_cpedit, LoadDatabase databases) {
                            String[] cp_table_columnNames = new String[table_content_for_cpedit.columns.size()];
                            table_content_for_cpedit.loadOtherTableValues(databases);
                            String[][] cp_data = new String[table_content_for_cpedit.values.size()][table_content_for_cpedit.columns.size()];
                            for (int cols = 0; cols < table_content_for_cpedit.columns.size(); cols++) {
                                cp_table_columnNames[cols] = table_content_for_cpedit.columns.get(cols).name;
                            }
                            for (int rows = 0; rows < table_content_for_cpedit.values.size(); rows++) {
                                for (int rcol = 0; rcol < table_content_for_cpedit.columns.size(); rcol++) {
                                    cp_data[rows][rcol] = table_content_for_cpedit.values.get(rows)[rcol];
                                    //System.out.println("Saving data: " + data[rows][rcol]);
                                }
                            }
                            DatabaseWindow.DBTableModel cp_model = new DatabaseWindow.DBTableModel(cp_table_columnNames, cp_data);
                            table_cpedit.setModel(cp_model);
                            TableColumnModel cp_columnModel = table_cpedit.getColumnModel();
                            if (cp_columnModel.getColumnCount() > 20) {
                                table_cpedit.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            } else {
                                table_cpedit.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                            }
                            for (int colm = 0; colm < cp_columnModel.getColumnCount(); colm++) {
                                cp_columnModel.getColumn(colm).setPreferredWidth(80);
                            }
                        }
                    });
                }
            }
        });


        runsql.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String sql_str = editor_sql.getText();
                String violation_str = editor_violation.getText();

                //check which database is loaded from database tab then proceed for the loop
                DatabaseWindow dw = new gui.DatabaseWindow();
                JPanel result_panel;
                String dataInDatabaseTab = str[0].trim();
                boolean checkDatabaseValue = dataInDatabaseTab.isEmpty();
                if (!checkDatabaseValue) {
                    LoadDatabase database = new LoadDatabase(str); //manual database tables
                    Table table_test = database.runQuery(sql_str);
                    result_panel = dw.getPanel(table_test, violation_str);
                } else {
                    db = new Database(new String[0]);
                    Table table_test = db.runQuery(sql_str);
                    result_panel = dw.getPanel(table_test, violation_str);
                }
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
        });
        // END OF DATABASE PANEL

        JMenuBar menuBar = new JMenuBar();
        frmPolicyEditor.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Policy files", "pol");
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);


        // LETS POPULATE FORM WITH OPENED POLICY INFO
        JMenuItem mntmNewPolicy = new JMenuItem("New policy");
        mnFile.add(mntmNewPolicy);
        JMenuItem mntmOpenPolicy = new JMenuItem("Open Policy");
        mnFile.add(mntmOpenPolicy);

        /*JExpandableTextArea expansion = new JExpandableTextArea(3, 3);
        expansion.setLineWrap(true);
        panel_general_title.add(new JScrollPane(expansion), null);
        frmPolicyEditor.setVisible(true);*/

        JMenuItem mntmSaveas = new JMenuItem("Build policy as...");
        mnFile.add(mntmSaveas);

        JMenuItem mntmSave = new JMenuItem("Build policy");
        mnFile.add(mntmSave);
        mntmSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
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
        });
        JMenuItem mntmExit = new JMenuItem("Exit");
        mnFile.add(mntmExit);
        mntmSave.setEnabled(false);
        mntmSaveas.setEnabled(false);

        mntmNewPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionListeners.mntmNewPolicyActionPerform(pol, frmPolicyEditor, text_general_title, textArea_messages, panel_general, panel_policy, panel_collectors, list_params, tree_checks, btn_col_add_collector, tree_collectors, list_general_versions, btnNewParameter, mntmSaveas);
                //actionListeners.mntmNewPolicyActionPerform(pol, frmPolicyEditor, expansion, textArea_messages, panel_general, panel_policy, panel_collectors, list_params, tree_checks, btn_col_add_collector, tree_collectors, list_general_versions, btnNewParameter, mntmSaveas, btnNewCheck);
            }
        });

        mntmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frmPolicyEditor.dispose();
            }
        });

        mntmSaveas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionListeners.mntmSaveasActionPerform(frmPolicyEditor, pol, textArea_general_description, mntmSave);
            }
        });

        mntmOpenPolicy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int returnVal = fc.showOpenDialog(frmPolicyEditor);
                File polfile = fc.getSelectedFile();
                //txtColname.setText(colname.getPath());
                if (polfile == null || returnVal == fc.CANCEL_OPTION) {
                    return;
                }
                try {
                    if (pol != null) {
                        pol.deleteTemp();
                    }
                    pol = new Policy();
                    if (pol.load(polfile.getPath()) != 1) {
                        JOptionPane.showMessageDialog(frmPolicyEditor,
                                "There was an issue with opening the file.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    loadMessages(textArea_messages, pol);
                    mntmSave.setEnabled(true);
                    mntmSaveas.setEnabled(true);
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
                    list_general_versions.setSelectedIndex(0);
                } catch (JDOMException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // populate info from policy to form
/*
                text_general_title.setText(pol.title);
                text_general_title.repaint(); */
                text_general_title.setText(pol.title);
                text_general_title.repaint();
                textArea_general_description.setText(pol.description);
                textArea_general_description.setCaretPosition(0);
                textArea_general_description.repaint();
                Iterator it = pol.versions.entrySet().iterator();
                int counter_values = 0;
                DefaultListModel listModel = new DefaultListModel();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    listModel.addElement(pair.getKey());
                    counter_values++;
                }

                list_general_versions.setModel(listModel);
                list_general_versions.repaint();

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
                loadParameters(pol, list_params);
                loadCollectors(pol, tree_collectors);
            }
        });

        // listener for policy version select
        textArea_general_version_description.setEditable(false);

        JButton btnSaveVersionDescription = new JButton("Save version description");
        GridBagConstraints gbc_btnSaveVersionDescription = new GridBagConstraints();
        gbc_btnSaveVersionDescription.insets = new Insets(0, 0, 5, 0);
        gbc_btnSaveVersionDescription.gridx = 1;
        gbc_btnSaveVersionDescription.gridy = 2;
        panel_general_version.add(btnSaveVersionDescription, gbc_btnSaveVersionDescription);


        JLabel lblSaveAllGeneral = new JLabel("Save all general info before saving the policy!");
        lblSaveAllGeneral.setForeground(Color.RED);
        GridBagConstraints gbc_lblSaveAllGeneral = new GridBagConstraints();
        gbc_lblSaveAllGeneral.insets = new Insets(0, 0, 0, 5);
        gbc_lblSaveAllGeneral.gridx = 0;
        gbc_lblSaveAllGeneral.gridy = 3;
        panel_general_version.add(lblSaveAllGeneral, gbc_lblSaveAllGeneral);


        //*************editor tab *************//

        JPanel panel_textArea = new JPanel();
        panel_textArea.setPreferredSize(new Dimension(1024, 800));
        panel_textArea.setBorder(BorderFactory.createLineBorder(Color.ORANGE));


        JTextArea sql_script = new JTextArea(40, 60);
        sql_script.setBorder(BorderFactory.createLineBorder(Color.RED));
        sql_script.setLineWrap(true);
        sql_script.setWrapStyleWord(true);
        sql_script.setPreferredSize(new Dimension(800, 700));
        panel_textArea.add(sql_script, BorderLayout.NORTH);


        JPanel panel_btn_sql = new JPanel();
        JButton btn_run_sql_editor_tab = new JButton("RUN SQL");
        btn_run_sql_editor_tab.setSize(10, 570);
        panel_btn_sql.add(btn_run_sql_editor_tab, BorderLayout.NORTH);

        panel_textArea.add(panel_btn_sql, BorderLayout.NORTH);


        JPanel sql_panel_refresh = new JPanel();
        JButton btn_sql_refresh = new JButton("Refresh");
        sql_panel_refresh.add(btn_sql_refresh, BorderLayout.NORTH);
        panel_btn_sql.add(sql_panel_refresh, BorderLayout.WEST);


        panel_btn_sql.add(sql_panel_refresh, BorderLayout.SOUTH);


        JPanel panel_sql_result = new JPanel();
        JLabel label_sql_result = new JLabel();
        label_sql_result.setSize(10, 30);
        panel_sql_result.add(label_sql_result);

        // panel_textArea.add(panel_sql_result);

        JScrollPane sqlEditor_scroll_bar = new JScrollPane(panel_textArea);
        scroll_bar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll_bar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.add(sqlEditor_scroll_bar, "SQL Editor");


        JPanel panel_available_tablesList = new JPanel();
        JLabel lbl_availableTables = new JLabel("Available tables in Database:");
        panel_available_tablesList.add(lbl_availableTables, BorderLayout.NORTH);

        JPanel panel_other_tables = new JPanel();
        JLabel lbl_available_other_tables = new JLabel("Available tables in loaded Database:");
        panel_other_tables.add(lbl_available_other_tables, BorderLayout.SOUTH);

        panel_available_tablesList.add(panel_other_tables);
        panel_other_tables.setVisible(false);

        JPanel panel_label_available_tables = new JPanel();
        JLabel lbl_click_on_lists = new JLabel("click on the combobox to know the available columns for SQL operations");
        panel_btn_sql.add(lbl_click_on_lists, BorderLayout.SOUTH);
        lbl_click_on_lists.setVisible(false);

        panel_textArea.add(panel_label_available_tables, BorderLayout.SOUTH);


        btn_run_sql_editor_tab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String update_sql_command = sql_script.getText();
                if (update_sql_command.equals("")) {
                    JOptionPane.showMessageDialog(null, "Enter SQL COMMAND",
                            "ALERT MESSAGE",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        String[] loadDatabaseValues = new String[]{""};
                        String loadDatabaseValuesPath = trial_file[0].getAbsolutePath();
                        loadDatabaseValues[0] = loadDatabaseValuesPath;

                        if (!loadDatabaseValuesPath.isEmpty() && loadDatabaseValuesPath.toUpperCase().endsWith("JAC_DATA")) {

                            LoadDatabase database = new LoadDatabase(loadDatabaseValues);
                            String viol_message = editor_violation.getText();
                            database.sqlEditorQuery(update_sql_command, frmPolicyEditor, label_sql_result, viol_message);
                        } else {
                            JOptionPane.showMessageDialog(null, "Please load databse and then do editing",
                                    "WARNING MESSAGE",
                                    JOptionPane.OK_OPTION);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        button_run_cp_sql.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String update_sql_command = text_area_title.getText();
                if (update_sql_command.equals("")) {
                    JOptionPane.showMessageDialog(null, "Enter SQL COMMAND",
                            "ALERT MESSAGE",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        String[] loadDatabaseValues = new String[]{""};
                        String loadDatabaseValuesPath = trial_file[0].getAbsolutePath();
                        loadDatabaseValues[0] = loadDatabaseValuesPath;

                        if (!loadDatabaseValuesPath.isEmpty() && loadDatabaseValuesPath.toUpperCase().endsWith("JAC_DATA")) {

                            LoadDatabase database = new LoadDatabase(loadDatabaseValues);
                            String viola_message = editor_violation.getText();
                            database.sqlEditorQuery(update_sql_command, frmPolicyEditor, lblsql, viola_message);
                        } else {
                            JOptionPane.showMessageDialog(null, "Please load databse and then do editing",
                                    "WARNING MESSAGE",
                                    JOptionPane.OK_OPTION);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // panel_available_tablesList.add(panel_btn_sql,BorderLayout.NORTH);
        // JPanel panel_btn_loadAvailableList = new JPanel();
        JButton btn_load_available_list_db = new JButton("SHOW AVAILABLE TABLES");
        btn_load_available_list_db.setSize(10, 570);
        panel_btn_sql.add(btn_load_available_list_db, BorderLayout.SOUTH);
        btn_load_available_list_db.setVisible(false);

        JPanel panel_loaded_db = new JPanel();
        JButton btn_load_manual_available_list_db = new JButton("SHOW AVAILABLE TABLES in LOADED DB");
        btn_load_manual_available_list_db.setSize(10, 570);
        panel_loaded_db.add(btn_load_manual_available_list_db, BorderLayout.SOUTH);
        panel_btn_sql.add(panel_loaded_db, BorderLayout.SOUTH);

        MutableComboBoxModel combobox_load_database = new DefaultComboBoxModel();
        JComboBox comboBox_available_table_list = new JComboBox(combobox_load_database);
        JLabel lbl_available_column_names = new JLabel("Available columns:");
        JScrollPane column_values_scrollPane = new JScrollPane();
        JPanel panel_table_columns = new JPanel();
        final DefaultListModel<String> columns_values = new DefaultListModel<>();
        final JList<String> columns_list = new JList<>(columns_values);
        btn_load_manual_available_list_db.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_load_manual_available_list_db.setVisible(false);
                String[] loadDatabaseValues = new String[]{""};
                String loadDatabaseValuesPath = trial_file[0].getAbsolutePath();

                if (!loadDatabaseValuesPath.isEmpty() && loadDatabaseValuesPath.toUpperCase().endsWith("JAC_DATA")) {
                    loadDatabaseValues[0] = loadDatabaseValuesPath;
                    LoadDatabase database = new LoadDatabase(loadDatabaseValues);
                    for (int i = 0; i < database.tables.size(); i++) {
                        combobox_load_database.addElement(database.tables.get(i));
                    }
                    comboBox_available_table_list.setMaximumRowCount(30);
                    comboBox_available_table_list.setPreferredSize(new Dimension(250, 30));
                    comboBox_available_table_list.setVisible(true);
                    panel_available_tablesList.add(comboBox_available_table_list, BorderLayout.LINE_END);
                    panel_available_tablesList.setVisible(true);
                    panel_textArea.add(panel_available_tablesList);
                    panel_available_tablesList.add(lbl_available_column_names);
                    lbl_available_column_names.setVisible(false);
                    lbl_availableTables.setVisible(true);

                    comboBox_available_table_list.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox combo_box = (JComboBox) actionEvent.getSource();
                            Table selected_table = (Table) combo_box.getSelectedItem();
                            // l1.clear();
                            loadGUITable(selected_table);
                        }

                        private void loadGUITable(Table table) {
                            String[] columnNames = new String[table.columns.size()];
                            table.loadOtherTableValues(database);
                            panel_table_columns.setVisible(true);
                            columns_list.setVisible(true);
                            columns_list.removeAll();
                            columns_values.clear();
                            for (int cols = 0; cols < table.columns.size(); cols++) {
                                columnNames[cols] = table.columns.get(cols).name;
                                columns_values.addElement(columnNames[cols]);
                                panel_table_columns.add(columns_list);
                            }
                            column_values_scrollPane.setVisible(true);
                            column_values_scrollPane.setViewportView(columns_list);
                            //  column_values_scrollPane.getVerticalScrollBar().setForeground(Color.BLUE);
                            columns_list.setLayoutOrientation(JList.VERTICAL);
                            panel_textArea.add(column_values_scrollPane);
                            panel_textArea.add(panel_table_columns);
                            lbl_available_column_names.setVisible(true);
                        }
                    });
                } else {

                    JOptionPane.showMessageDialog(
                            frmPolicyEditor,
                            "JAC_DATA is not selected in Database tab");
                }
            }
        });

        btn_load_available_list_db.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_load_available_list_db.setVisible(false);
                lbl_click_on_lists.setVisible(true);
                panel_other_tables.setVisible(true);

                db = new Database(new String[0]);
                MutableComboBoxModel cobm = new DefaultComboBoxModel();
                for (int i = 0; i < db.tables.size(); i++) {
                    cobm.addElement(db.tables.get(i));
                }

                JComboBox comboBox_available_table_list = new JComboBox(cobm);
                comboBox_available_table_list.setMaximumRowCount(15);
                comboBox_available_table_list.setPreferredSize(new Dimension(250, 30));
                comboBox_available_table_list.removeAll();
                panel_available_tablesList.add(comboBox_available_table_list, BorderLayout.LINE_END);

                panel_textArea.add(panel_available_tablesList);
                JPanel panel_table_columns = new JPanel();

                final DefaultListModel<String> l1 = new DefaultListModel<>();
                final JList<String> list2 = new JList<>(l1);

                JLabel lbl_available_column_names = new JLabel("Available columns:");
                panel_available_tablesList.add(lbl_available_column_names);
                lbl_available_column_names.setVisible(false);
                comboBox_available_table_list.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        JComboBox combo_box = (JComboBox) actionEvent.getSource();
                        Table selected_table = (Table) combo_box.getSelectedItem();
                        l1.clear();
                        loadGUITable(selected_table);
                    }

                    private void loadGUITable(Table table) {
                        String[] columnNames = new String[table.columns.size()];
                        table.loadValues(db);

                        for (int cols = 0; cols < table.columns.size(); cols++) {
                            columnNames[cols] = table.columns.get(cols).name;
                            l1.addElement(columnNames[cols]);
                            panel_table_columns.add(list2);
                        }
                        panel_textArea.add(panel_table_columns);
                        lbl_available_column_names.setVisible(true);
                    }
                });
            }
        });


        btn_sql_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                btn_load_manual_available_list_db.setVisible(true);
                lbl_availableTables.setVisible(false);
                comboBox_available_table_list.setVisible(false);
                panel_available_tablesList.setVisible(false);
                lbl_available_column_names.setVisible(false);
                panel_table_columns.setVisible(false);
                columns_list.setVisible(false);
                column_values_scrollPane.setVisible(false);

            }
        });
        JButton btnSaveInfo = new JButton("Save all general info");
        btnSaveInfo.setFont(new Font("Dialog", Font.BOLD, 16));
        GridBagConstraints gbc_btnSaveInfo = new GridBagConstraints();
        gbc_btnSaveInfo.gridx = 1;
        gbc_btnSaveInfo.gridy = 3;
        panel_general_version.add(btnSaveInfo, gbc_btnSaveInfo);
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
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
                        //System.out.println("Description: " + descript);
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
        };
        list_general_versions.addListSelectionListener(listSelectionListener);


        // listener for rule selection
        tree_checks.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree_checks.getLastSelectedPathComponent();

                /* if nothing is selected */
                if (node == null) {
                    enableComponents(panel_policy, false);
                    //btnNewCheck.setEnabled(true);
                    tree_checks.setEnabled(true);
                    return;
                }
                /* retrieve the node that was selected */
                Rule node_rule = (Rule) node.getUserObject();
                if (node_rule == null) {
                    enableComponents(panel_policy, false);
                    tree_checks.setEnabled(true);
                    return;
                }
                enableComponents(panel_policy, true);
                populateForm(node_rule, text_title, list_versions, text_version_comment, editor_description, editor_violation, rdb_prio_informational, rdb_prio_low, rdb_prio_normal, rdb_prio_high, editor_sql, check_collectors);
                textArea_violation.setText(node_rule.violationMessage);
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
                for (ActionListener al : btnRemove.getActionListeners()) {
                    btnRemove.removeActionListener(al);
                }
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


                String appendixTitle = ((PolicyPack.Rule) node.getUserObject()).title;
                String[] splitString = appendixTitle.split("-");
                AppendixName appendixNameComparison = new AppendixName();
                String appendixName = appendixNameComparison.appendixName(splitString[0]);

                control_point_count.setText("CP & Appendix Name in SPM:" + node.getParent().getChildCount() + "&" + appendixName);
            }
        });
        btnSaveDescription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
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
                Rule node_rule_check = (Rule) node_check.getUserObject();
                node_rule_check.versions.put(sel_val, desc_text);
            }
        });
        btnSaveVersionDescription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String desc_text = textArea_general_version_description.getText();
                if (list_general_versions.getSelectedValue() == null) {
                    return;
                }
                int sel_val = Integer.parseInt(list_general_versions.getSelectedValue().toString());
                pol.versions.put(sel_val, desc_text);
            }
        });
        btnSaveInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pol.title = text_general_title.getText();
                pol.description = textArea_general_description.getText();
            }
        });

        btn_check_col_add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Icon icon = null;
                if (pol.collectors == null || pol.collectors.isEmpty()) {
                    return;
                }
                Collector col = (Collector) JOptionPane.showInputDialog(
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
                    Rule node_rule_check = (Rule) node_check.getUserObject();
                    node_rule_check.requires.add(col.colId);
                    loadCheckCollectors(check_collectors, node_rule_check);
                }
            }
        });
        btn_check_col_remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

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
                    Rule node_rule_check = (Rule) node_check.getUserObject();

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
        });

        // PARAMETERS LISTENERS
        ListSelectionListener listSelectionListener_params = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
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
        };
        list_params.addListSelectionListener(listSelectionListener_params);

        btnSaveParameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
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
        });

        btnNewParameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });

        btnDeleteParameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });

        // COLLECTOR LISTENERS
        tree_collectors.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
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
        });

        ListSelectionListener listSelectionListener_col_params = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
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
        };
        list_col_params.addListSelectionListener(listSelectionListener_col_params);

        ListSelectionListener check_collectors_listener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
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
        };
        check_collectors.addListSelectionListener(check_collectors_listener);

        ActionListener comboBox_table_list_listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                Collector.Table table = (Collector.Table) cb.getSelectedItem();
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
                ;
            }
        };
        comboBox_table_list.addActionListener(comboBox_table_list_listener);

        btn_col_delete_param.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });

        btn_col_add_param.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });

        btn_col_delete_collector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });

        btn_col_add_collector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

                collectorId = checkColId(collectorId);
                if (collectorId == null) {
                    return;
                }
                pol.addCollector(collectorId, colfile);
                reloadVersionsGeneral(list_general_versions, pol, btnRemove_general);
                loadCollectors(pol, tree_collectors);
                JOptionPane.showMessageDialog(frmPolicyEditor, "Collector has been added.");

            }
        });

        frmPolicyEditor.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (pol != null && pol.tempPath != null) {
                    pol.deleteTemp();
                }
                System.exit(0);
            }
        });

        enableComponents(panel_general, false);
        enableComponents(panel_policy, false);
        enableComponents(panel_collectors, false);
        enableComponents(panel_parameters, false);


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

    public void populateCollector(JTextPane textPane_col_info, JList list_col_params, JTextArea textArea_col_param_values, Collector col) {
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

    public void loadColParamValues(Collector col, String paramName, JTextArea textArea_col_param_values) {
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

    public void loadVersionDescription(JTextArea verDesc, String description) {
        verDesc.setText(description);
        verDesc.setCaretPosition(0);
        verDesc.repaint();
    }

    public void populateForm(Rule rule, JTextField text_title, JList list_versions, JTextArea text_version_comment, JTextArea editor_description,
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

    public void loadColParameters(Collector col, JList list_col_params) {
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

    public void loadCheckCollectors(JList check_collectors, Rule rule) {
        DefaultListModel model_cols = new DefaultListModel();
        for (int ii = 0; ii < rule.requires.size(); ii++) {
            model_cols.addElement(rule.requires.get(ii));
        }

        check_collectors.setModel(model_cols);
        check_collectors.revalidate();
        check_collectors.repaint();
    }

    public void reloadVersions(JList list_versions, Rule rule, JButton remove_button) {
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
        tree_checks.setEditable(true);
        tree_checks.repaint();
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

    public void loadParameters(Policy pol, JList list_params) {
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

    public void actionPerformed(ActionEvent actionEvent) {

        System.out.println("WorkProgress status:" + actionEvent.getActionCommand());
        //db.retrieveAutoSaveData();

    }

    public String checkColId(String colId) {
        if (colId == null) {
            return null;
        }
        if ((pol.getColByName(colId) == null) && (!colId.equals("")) && (colId.length() > 3)) {
            return colId;
        } else {
            String collectorId = JOptionPane.showInputDialog(frmPolicyEditor, "Already exists or name is too short, enter different name:", colId);
            collectorId = checkColId(collectorId);
            return collectorId;
        }
    }

    public String checkPolName(String polName) {
        if (polName == null) {
            return null;
        }
        if ((!polName.equals("")) && (polName.length() > 3)) {
            return polName;
        } else {
            String polNameTest = JOptionPane.showInputDialog(frmPolicyEditor, "Policy name must have at least 4 characters:", polName);
            polName = checkPolName(polNameTest);
            return polName;
        }
    }
}
/*
GENERAL TAB
text_general_title - JTextField with policy title in general tab
textArea_general_description - JTextArea with policy description in general tab
list_general_versions - JList with list of policy versions in general tab
textArea_general_version_description - JTextArea with general policy version description
btnAddVersion_general - JButton to add policy version in general tab
btnRemove_general - JButton button to remove policy version in general tab
btnSaveInfo - JButton button to save policy info in general tab
btnSaveVersionDescription -JButton button to save policy version description

POLICY TAB
tree_checks - JTree with list of checks
btnRemoveCheck - JButton to remove check
btnNewCheck - JButton to add new check
text_title - JTextField with check title
list_versions - JList with list of control point versions in policy tab
text_version_comment - JTextArea with version description of the check
btnAddVersion - JButton to add check version
btnRemove - JButton to remove check version
btnSaveDescription - JButton to save check version description
editor_description - JTextArea with check description
editor_violation - JTextArea with violation message
rdb_prio_informational
rdb_prio_low
rdb_prio_normal
db_prio_high - JRadioButton priority buttons
editor_sql - RSyntaxTextArea (with library) for SQL editing in policy tab
button_save - JButton to save all changes to check

TAB LIST
panel_general - general panel
panel_policy - policy panel
panel_collectors - collector panel

PARAMS
scrollPane_params - scrollPane
list_params - JList
panel_param_detail
textArea_param_detail
comboBox_paramtype - JComboBox
btnNewParameter - JButton
btnDeleteParameter - JButton
btnSaveParameter - JButton

COLLECTORS
tree_collectors
textPane_col_info
list_col_params
textArea_col_param_values
btn_col_save_param
btn_col_new_param
btn_col_param_delete




*/
