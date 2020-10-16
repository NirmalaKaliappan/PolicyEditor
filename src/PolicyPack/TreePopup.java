package PolicyPack;

import logic.Policy;
import org.jdom2.JDOMException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static gui.MainWindow.pol;


public class TreePopup extends JPopupMenu implements MouseListener {
    public TreePopup(DefaultMutableTreeNode node, JFrame frmPolicyEditor, JTree tree_check) {
        JMenuItem newCheck = new JMenuItem("NewCheck");
        JMenuItem rename = new JMenuItem("RenameCheck");
        JMenuItem delete = new JMenuItem("DeleteCheck");

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.out.println("Delete child");
                try {
                    removeNode(node, frmPolicyEditor, tree_check);
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        rename.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.out.println("rename child");
                renameNode(node);
            }
        });
        newCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("new Check child");
                newCheckNode(frmPolicyEditor, pol, tree_check);
            }
        });
        add(newCheck);
        add(new JSeparator());
        add(rename);
        add(new JSeparator());
        add(delete);
    }

    public void newCheckNode(JFrame frmPolicyEditor, Policy pol, JTree tree_checks) {
        String check_name = " ";
        check_name = JOptionPane.showInputDialog(frmPolicyEditor, "Enter new check title:");
        if (check_name == null) {
            return;
        }
        if (check_name.length() < 3) {
            return;
        }
        pol.addCheck(check_name.trim());
        reloadTreeChecks(tree_checks);
    }

    private void removeNode(DefaultMutableTreeNode node, JFrame frmPolicyEditor, JTree tree_checks) throws JDOMException, IOException {
        if (node == null) return;
        /* retrieve the node that was selected */
        PolicyPack.Rule node_rule_check = (PolicyPack.Rule) node.getUserObject();
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(frmPolicyEditor, "Do you really want to remove check " + node_rule_check.title + "?", "Warning", dialogButton);
        if (dialogResult == JOptionPane.YES_OPTION) {
            pol.removeCheck(node_rule_check);
            reloadTreeChecks(tree_checks);

        }
    }

    private void reloadTreeChecks(JTree tree_checks) {
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

    public void renameNode(DefaultMutableTreeNode node) {
        System.out.println("Rename child");
        JPanel rename_panel = new JPanel(new GridLayout(1, 2, 10, 10));
        rename_panel.setPreferredSize(new Dimension(400, 20));
        JTextField rename_text = new JTextField();
        rename_panel.add(rename_text);
        JOptionPane.showConfirmDialog(null, rename_panel, "enter new name to the control point", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String col_name = rename_text.getText();
        System.out.println("new name:" + col_name);
        if (!col_name.isEmpty()) {
            Rule node_rule_check = (Rule) node.getUserObject();
            node_rule_check.title = col_name;
            try {
                pol.build("");
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("new name to the cp:" + col_name);

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
