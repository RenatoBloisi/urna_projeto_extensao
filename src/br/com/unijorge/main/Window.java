package br.com.unijorge.main;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Window extends JFrame {
	
	Organizer orderManager = new Organizer();;
	
	public Window(/*int telaLarg, int telaAltu*/) {
		this.setTitle("Urna Simulada");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		//this.setSize(telaLarg, telaAltu);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.add(orderManager);
		this.pack();
	}

}
