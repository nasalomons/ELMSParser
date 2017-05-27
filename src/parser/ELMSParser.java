package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.plaf.synth.SynthSeparatorUI;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/* This program was designed to help me practice as well as convenience accessing the list of school assignments that I 
 * need to finish. University of Maryland: College Park uses a service called ELMS, created by Instructure, to
 * help students and staff organize assignments, grades, and files. It is located here: https://elms.umd.edu/. 
 * 
 * Since I look at what assignments I need to do almost every day when I'm taking classes, I thought it would be 
 * useful to create a program that accesses my account automatically and retrieves the information I want. To get to
 * these assignments, I need to log in and navigate to my agenda, which takes a few clicks to many and that time adds
 * up. Doing it often can be annoying, so I decided to get some Java practice and make this program.
 * 
 * This program opens up ELMS, logs in, retrieves the assignments on a given day, and puts them in a text document
 * on the Desktop. I used HTMLUnit to navigate the webpage, which is a library I did not create. It was created by
 * Gargoyle Software Inc. More information on this incredibly helpful library can be found here: 
 * http://htmlunit.sourceforge.net/. */
public class ELMSParser {	
	private static BufferedWriter out;
	private static BufferedReader in;
	
	public static void main(final String[] args) {
				
		// Turn off htmlunit warnings					
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
		// Saving instance of static inner class for use later and starting thread
		ThreadCode threadCode = new ThreadCode();
		Thread thread = new Thread(threadCode);
		thread.start();	
	 
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	        	        
		// Opening login page
		final HtmlPage loginPage;
		List<HtmlForm> forms = null;
		HtmlButton loginButton = null;
		try {
			loginPage = webClient.getPage("https://myelms.umd.edu/calendar#view_name=agenda&view_start=2017-05-24");	
			forms = loginPage.getForms();	
			
			//Getting log in button
			loginButton = (HtmlButton) loginPage.getElementsByTagName("button").get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Getting username and password forms			
		HtmlInput usernameIn = forms.get(0).getInputByName("pseudonym_session[unique_id]");								
		HtmlInput passwordIn = forms.get(0).getInputByName("pseudonym_session[password]");				
												
		// Setting username and password - WILL BE CONSTANT IN MY USAGE	(instead of using JOptionPane)			
		usernameIn.setValueAttribute(JOptionPane.showInputDialog("Enter username:"));	
	    passwordIn.setValueAttribute(JOptionPane.showInputDialog("Enter password:"));	
										
		// Logging in and navigating to the Agenda
	    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	     * NOTE: I am not checking to make sure that username and password were correct because the reason I created *
	     * this program was convenience. Instead of using JOptionPane above, my username and password will be there. *
	     * Since they will always be correct (since it won't change), there is no need to check for incorrect        *
	     * entries. I am using JOptionPane now because I do not want to give out my username/password on the 		 *
	     * internet.                                                                                                 *
	     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	    final HtmlPage intermediate;
	    try {
			intermediate = loginButton.click();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    // Waiting for the thread to finish retrieving the date from the user
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieving date from thread and parsing it to get the month, day, and year requested
		String date = threadCode.getDate();
		
		String month = null, day = null, year = null;
		try {
			month = date.substring(0, 2);			
			day = date.substring(3, 5);			
			year = date.substring(6, 10);
		} catch(NumberFormatException e) {
			e.printStackTrace();		
		}

		HtmlPage agenda;

		try {				
			
			// Opening the agenda webpage at the date provided
			agenda = webClient.getPage("https://myelms.umd.edu/calendar#view_name=agenda&view_start=" 
					+ year + "-" + month + "-" + day);
			
			// Navigating to where assignments are stored
			DomElement temp = agenda.getElementById("content");
			temp = temp.getLastElementChild().getPreviousElementSibling();
			temp = temp.getFirstElementChild().getNextElementSibling();
			// Waiting for the page to load (just in case)
			while(temp.getChildElementCount() == 0) {
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Navigating to where the events are stored
			temp = temp.getFirstElementChild();
			temp = temp.getFirstElementChild().getNextElementSibling();
			DomElement eventList = temp.getFirstElementChild();
			
			// curr will be what is changed to move from element to element
			DomElement curr = eventList.getFirstElementChild();
			if(curr == null) {
				System.out.println("No assignments due on " + month + "/" + day + "/" + year);
			}
			
			/* Retrieving and cleaning up the due date, assignment name, and course name of each assignment 
			 * and printing it to the file */
			StringBuffer line = new StringBuffer("");
			out.newLine();
			for(int i = 1; curr != null; i++) {
				DomElement event = curr.getFirstElementChild();
				DomElement dueDate = event.getFirstElementChild().getNextElementSibling();
				DomElement assignmentName = dueDate.getNextElementSibling();
				DomElement courseName = assignmentName.getNextElementSibling();
				
				// Assignment Number
				line.append(i + ") ");
				// If the assignment is already done, the user would want to know
				if(assignmentName.toString().contains("completed")) {
					line.append("COMPLETED -- ");
				}
				// The time always starts at index 6 (it is a little off if it's a calendar event, but overall fine)
				line.append("Due: " + dueDate.asText().substring(6) + " - ");
				
				// Assignment name is always concluded by a space then a comma (if an assignment)
				if(assignmentName.asText().contains(",")) {
					line.append(assignmentName.asText().substring(0, assignmentName.asText().indexOf(',')) + "- ");
				// If it's a calendar event it might not have one
				} else {
					line.append(assignmentName.asText() + " - ");
				}
				
				// Course name always is preceded by a comma and is 7 characters
				line.append(courseName.asText().subSequence(1, 8));
				
				// Printing line to the file
				out.write(line.toString());
				out.newLine();
				
				// Clearing line
				line.delete(0, line.length());
				
				// Next event
				curr = curr.getNextElementSibling();
			}
											
			// Closing output
			if(out != null) {
				out.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		// Closing the WebClient
		if(webClient != null) {
			webClient.close();
		}
		
	}

	private static class ThreadCode implements Runnable {
		private String date = "penis";
		private BufferedWriter writer;
		
		@Override
		public void run() {
			
			try {
				// Getting location of Desktop of the computer and creating agenda file (might be a better way)
				File desktop = new File(System.getProperty("user.home") + File.separator + "Desktop");
				File agenda = File.createTempFile("agenda", ".txt", desktop);
				
				// Creating BufferedWriter and BufferedReader for agenda
				try {
					in = new BufferedReader(new FileReader(agenda));
					out = new BufferedWriter(new FileWriter(agenda, true));
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				}
			
				// Printing prompt to agenda
				String prompt = "Enter a date (MM/DD/YYYY):";
				out.write(prompt);
				out.flush();
				
				// Reading input (Note: file must be saved before program can read it)
				boolean keepReading = true;
				in.readLine();
				while(keepReading) {
					if((date = in.readLine()) == null || (date.equals(""))) {
						try {
							Thread.sleep(7000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						keepReading = false;
					}
				}
						
				// Closing input (need output for later)
				if(in != null) {
					in.close();
				}
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		// Method to allow us to retrieve the date in main
		public String getDate() {
			return date;
		}
	}
}