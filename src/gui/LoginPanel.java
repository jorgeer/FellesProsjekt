package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import util.ChangeType;
import util.GUIListener;
import util.GUIListenerSupport;

/**
 * 
 * Her lages guiet til loginskjermen. 
 *
 */
public class LoginPanel extends JPanel implements ActionListener{

	JTextField usernameField;
	JTextField passwordField;
	JButton loginBtn, exitBtn;

	GUIListenerSupport gls;

	public LoginPanel(){
		
		gls = new GUIListenerSupport();
		setBackground(GConfig.LOGINCOLOR);
		setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.anchor = GridBagConstraints.CENTER;
		g.weightx=0.5;
		g.weighty=0.5;
		g.gridx=0;
		g.gridy=0;
		g.insets = new Insets(10, 5, 0, 5);
		g.insets.bottom = 40;

		g.gridwidth=2;
		add(new JLabel("Her legger vi inn en logo"),g);

		g.anchor = GridBagConstraints.WEST;
		g.insets.bottom = 5;
		g.gridwidth=1;
		g.gridy=1;
		add(new JLabel("Brukernavn:"),g);

		usernameField = new JTextField("",15);

		g.gridx=1;
		g.gridy=1;
		add(usernameField,g);

		g.gridx=0;
		g.gridy=2;
		add(new JLabel("Passord: "),g);

		passwordField = new JPasswordField("",15);

		g.gridx=1;
		g.gridy=2;
		passwordField.addKeyListener(new passListen());
		add(passwordField,g);

		loginBtn = new JButton("Logg inn");
		loginBtn.addActionListener(this);

		g.anchor = GridBagConstraints.CENTER;
		g.gridx=0;
		g.gridy=3;
		add(loginBtn,g);
		
		exitBtn = new JButton("Avslutt");
		exitBtn.addActionListener(this);
		g.gridx=1;
		add(exitBtn,g);
	}


	public void addGuiListener(GUIListener listener){
		gls.add(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==loginBtn){
			ArrayList<Object> array = new ArrayList<Object>();
			array.add(usernameField.getText());
			array.add(passwordField.getText());
			gls.notifyListeners(ChangeType.LOGIN, array);
			usernameField.setText("");
			passwordField.setText("");
		}else if(e.getSource()==exitBtn){
			System.exit(0);
		}
	}
	
	class passListen implements KeyListener { //Listeners.

		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_ENTER)
			{ 
				ArrayList<Object> array = new ArrayList<Object>();
				array.add(usernameField.getText());
				array.add(passwordField.getText());
				gls.notifyListeners(ChangeType.LOGIN, array);
				usernameField.setText("");
				passwordField.setText("");
			}
		}
		public void keyReleased(KeyEvent e){
		}
		public void keyTyped(KeyEvent arg0){
		}
	}
}