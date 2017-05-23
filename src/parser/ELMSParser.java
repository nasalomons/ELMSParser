package parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.List;

import javax.swing.JOptionPane;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
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
 * up. 
 * 
 * (If I'm being completely honest, it's really not that bad, but it gets annoying and I wanted some more Java
 * practice beyond school.)
 * 
 * This program opens up ELMS, logs in, retrieves the assignments on a given day, and puts them in a text document
 * on the Desktop. I used HTMLUnit to navigate the webpage, which is a library I did not create. It was created by
 * Gargoyle Software Inc. More information on this incredibly helpful library can be found here: 
 * http://htmlunit.sourceforge.net/. */
public class ELMSParser {
	
	public static void main(final String[] args) throws MalformedURLException, IOException {
		
		// Turn off htmlunit warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
	 
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	        	        
		// Opening login page
		final HtmlPage loginPage = webClient.getPage("https://myelms.umd.edu/login/ldap");	
		System.out.println(loginPage.getUrl());
		
		// Getting username and password forms			
		List<HtmlForm> forms = loginPage.getForms();	
		HtmlInput usernameIn = forms.get(0).getInputByName("pseudonym_session[unique_id]");								
		HtmlInput passwordIn = forms.get(0).getInputByName("pseudonym_session[password]");				
										
		// Getting log in button			
		HtmlButton loginButton = (HtmlButton) loginPage.getElementsByTagName("button").get(0);				
		
		// Setting username and password - WILL BE CONSTANT IN MY USAGE	(instead of using JOptionPane)			
		usernameIn.setValueAttribute(JOptionPane.showInputDialog("Enter username:"));	
	    passwordIn.setValueAttribute(JOptionPane.showInputDialog("Enter password:"));	
										
		// Logging in
	    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
	     * NOTE: I am not checking to make sure that username and password were correct because the reason I created *
	     * this program was convenience. Instead of using JOptionPane above, my username and password will be there. *
	     * Since they will always be correct (since it won't change), there is no need to check for incorrect        *
	     * entries. I am using JOptionPane now because I do not want to give out my username/password on the 		 *
	     * internet.                                                                                                 *
	     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		try {
			HtmlPage home = loginButton.click();	
			System.out.println(home.getUrl());
		} catch (SocketException e) {
			System.out.println("Problem connecting to page");
			webClient.close();
			return;
		}
				
		webClient.close();
	}
}