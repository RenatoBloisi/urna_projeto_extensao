package br.com.unijorge.main;

import java.awt.Color;

import javax.swing.JPanel;

public class Organizer extends JPanel {
	
	JPanel menu = new JPanel();
	
	public Organizer() {
		this.setBackground(Color.red);
		this.setBounds(0, 0, 640, 640);
		this.setAlignmentX(CENTER_ALIGNMENT);
		this.setAlignmentY(TOP_ALIGNMENT);
		
		this.add(menu);
		menu.setBackground(Color.orange);
		menu.setBounds(WIDTH / 2, HEIGHT / 2, 640, 640);
	}

}
