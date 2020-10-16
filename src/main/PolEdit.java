package main; 
import java.awt.EventQueue;

import gui.MainWindow;

public class PolEdit {
	public static float VERSION = 1.1f; 
	
	public static void main(String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmPolicyEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
		});
	}
}
