package controller;



import gui.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import model.DBAppointment;
import model.DBMeeting;

import model.DBUser;

import model.User;
import util.ChangeType;
import util.GUIListener;


/**
 * This class controls the GUI, and takes input from changes in the Model. 
 * @author jorgen
 *
 */
public class GUIController implements GUIListener, ListSelectionListener{

	private ProjectPanel pp;
	private Toolkit tool = Toolkit.getDefaultToolkit();
	private boolean loggedIn = false;
	private LoginPanel loginPanel;
	private CalendarPanel calendarPanel;
	private NewPanel newPanel;
	private User user, viewUser;
	private GridBagConstraints g;
	private int selectedAppointmentID;
	private boolean wasSelectedApp;
	private JFrame jf;


	/**
	 * Initializes the GUI and frame, then calls ChangePanel with no input to open the loginPanel.
	 */
	public GUIController(){
		System.out.println("Starting GuiController");

		startPanels();

		jf = new JFrame("Kalender");
		jf.setLayout(new GridBagLayout());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//	jf.setContentPane(pp);
		jf.add(pp);
		jf.getContentPane().setPreferredSize(new Dimension((int)tool.getScreenSize().getWidth(), (int)(tool.getScreenSize().getHeight())));
		jf.getContentPane().setBackground(Color.DARK_GRAY);
		jf.setVisible(true);

		notifyGui(ChangeType.LOGOUT, null);

		System.out.println("GuiController made");
	}




	/**
	 * Initializes all the panels, and adds listeners.
	 */
	private void startPanels(){
		System.out.println("Initializing panels");

		g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		pp = new ProjectPanel();
		pp.setLayout(new GridBagLayout());


		loginPanel = new LoginPanel();
		loginPanel.addGuiListener(this);

		calendarPanel = new CalendarPanel();
		calendarPanel.addGuiListener(this);
		calendarPanel.cl.calendarList.addListSelectionListener(this);
		calendarPanel.il.inkallingList.addListSelectionListener(this);
		populateCalendarList();

		System.out.println("Panels initialized");
	}


	public void populateCalendarList(){
		ArrayList<User> users = DBUser.getUsersInSystem();
		System.out.println(users);
		for(int i=0;i<users.size();i++)
			calendarPanel.cl.addUserToList(users.get(i));
	}



	/**
	 * This method is located in GUIController, and handles all input from the GUI panels. 
	 * Changes the content of ProjectPanel to the specified JPanel.
	 * 
	 * @param ct
	 * ct is used to specify the type of event that occurred in the gui panel. 
	 * If an existing ChangeType exists that describes your purpose, use that. 
	 * If not, add it to the ChangeType class and fill in the description here.
	 * 
	 * @param list
	 * The arrayList parameter list is used to pass any information related to the GUI event. 
	 * If there is no information to be passed, this parameter can be set to "null". 
	 * You do not need to make an empty array List. 
	 * What the list indexes contain are described below, 
	 * where L1 means list.get(1), L0 means list.get(0) and so on.
	 * 
	 * <p> ChangeTypes currently accepted are:
	 * <p> LOGIN 	- L0 = username, L1 = password	-	Retrieves the user from DB, then tries to log in. If successful, open calendar.
	 * <p> LOGOUT	- list = null	-	Sets user and LoggedIn to null, opens LoginPanel.
	 * <p> NEWAPP	- list = null	-	Opens NewPanel after button is clicked in CalendarPanel.
	 *
	 */
	@Override
	public void notifyGui(ChangeType ct, ArrayList<Object> list) {
		System.out.println("Notifying GUI: " + ct);

		//Lists all the items of list for debugging.
		if(list != null){

			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}
		}


		pp.removeAll();


		//If user in
		if (!loggedIn){
			System.out.println("not logged in");
			loginPanel.setPreferredSize(new Dimension((int)tool.getScreenSize().getWidth()/3, (int)(tool.getScreenSize().getHeight()/3)));
			pp.add(loginPanel);

		}



		// Attempts to log the user in using list.get(0) as Username and list.get(1) as password. 
		if(ct == ChangeType.LOGIN){
			System.out.println("Ct = login");

			//Here there should be a call to Database for user matching the username. 
			//Place user in this variable and call validate Login on it.
			user = DBUser.getUser((String)list.get(0));
			viewUser = user;
			System.out.println(user.getName());
			if(user.validateLogin((String)list.get(0),(String)list.get(1))){
				System.out.println("logged in");
				loggedIn= true;
				notifyGui(ChangeType.CALENDAR, null);
			}
		}



		//Opens the calendarPanel
		else if(ct == ChangeType.CALENDAR){
			System.out.println("adding calendar");

			calendarPanel.setPreferredSize(
					new Dimension((int)(tool.getScreenSize().getWidth()-20), (int)((tool.getScreenSize().getHeight()))- 40));

			System.out.println("firstDay: " + calendarPanel.wp.getFirstDay());
			System.out.println("Last day: " + calendarPanel.wp.getLastDay());
			System.out.println("id " + user.getId());
			//			calendarPanel.wp.setAppointments(
			//					DBAppointment.getAppointmentsInInterval(
			//							calendarPanel.wp.getFirstDay(), calendarPanel.wp.getLastDay(), user.getId()));
			ArrayList a = DBUser.getUserAppointments(user.getId());
			System.out.println(DBUser.getUser(user.getId()).getName());
			System.out.println("Number of appointments related to user: " + a.size());
			calendarPanel.wp.setAppointments(a);

			pp.add(calendarPanel);
		}



		// Logout button pressed in CalendarPanel. Log user out and return to LoginPanel.
		else if(ct == ChangeType.LOGOUT){
			System.out.println("ct = logout");
			user = null;
			loggedIn = false;
			loginPanel.setPreferredSize(new Dimension((int)tool.getScreenSize().getWidth()/3, (int)(tool.getScreenSize().getHeight()/3)));
			pp.add(loginPanel);

		}

		//Appointment button called, switching to editPanel for selected appointment
		else if(ct == ChangeType.APPBUTTON){
			System.out.println("Appointment button called");
			System.out.println("Appointment id: " + ((AButton)(list.get(0))).getAppointment());
			EditPanel ep = new EditPanel(((AButton)(list.get(0))).getAppointment());
			selectedAppointmentID = ((AButton)(list.get(0))).getAppointment().getId();
			wasSelectedApp = ep.getIsMeeting();
			ep.addListener(this);
			pp.add(ep);
		}


		else if(ct == ChangeType.NEXTWEEK || ct == ChangeType.PREVWEEK){

			ArrayList a = DBUser.getUserAppointments(viewUser.getId());
			calendarPanel.wp.setAppointments(a);
			calendarPanel.buildCalendarPanel();
			pp.add(calendarPanel, g);
		}

		// New appointment button clicked in Calendar Panel. Change view to newPanel.
		else if(ct == ChangeType.NEWAPP){
			System.out.println("ct = newApp");

			newPanel = new NewPanel();
			newPanel.setPreferredSize(new Dimension((int)tool.getScreenSize().getWidth() - 40, (int)(tool.getScreenSize().getHeight()-40)));
			newPanel.addGuiListener(this);
			newPanel.setUser(this.user);
			pp.add(newPanel);

		}

		else if(ct == ChangeType.CREATEMEETING){
			System.out.println("Mottatte del: "+list);
			notifyGui(ChangeType.CALENDAR, null);
		}else if(ct== ChangeType.DELETE){
			System.out.println(DBAppointment.getAppointment(selectedAppointmentID));
			if(wasSelectedApp)
				DBAppointment.deleteAppointment(selectedAppointmentID);
			else
				DBMeeting.deleteMeeting(selectedAppointmentID);
			notifyGui(ChangeType.CALENDAR, null);
		}
		else{
			System.out.println("ChangeType not recognized");

		}

		pp.validate();
		pp.revalidate();
		pp.repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

		if(e.getSource() == calendarPanel.il.inkallingList){
			String s = (String)JOptionPane.showInputDialog(
                    jf,
                    "Accept invitation=",
                    JOptionPane.YES_NO_OPTION
                    );
			System.out.println(s);


		}
		else{
			pp.removeAll();
			viewUser = (User)calendarPanel.cl.calendarList.getSelectedValue();
			ArrayList a = DBUser.getUserAppointments(viewUser.getId());
			calendarPanel.wp.setAppointments(a);
			pp.add(calendarPanel);
			pp.revalidate();
			pp.repaint();
		}
	}


}
