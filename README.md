This program was designed to help me practice as well as convenience accessing the list of school assignments that I 
need to finish. University of Maryland: College Park uses a service called ELMS, created by Instructure, to
help students and staff organize assignments, grades, and files. It is located here: https://elms.umd.edu/. 

Since I look at what assignments I need to do almost every day when I'm taking classes, I thought it would be 
useful to create a program that accesses my account automatically and retrieves the information I want. To get to
these assignments, I need to log in and navigate to my agenda, which takes a few clicks to many and that time adds
up. Doing it often can be annoying, so I decided to get some Java practice and make this program.

The program logs into ELMS and waits. A thread is created which creates a text file on the desktop and prompts
the user to enter a date in the text file. Once the user enters a text file, the thread is terminated and the 
main thread retrieves the date. It navigates to where the assignments for that date are stored and prints each
assignment, due date, and class to the text file.

I used HTMLUnit to navigate the webpage, which is a library I did not create. It was created by
Gargoyle Software Inc. More information on this incredibly helpful library can be found here: 
http://htmlunit.sourceforge.net/.

Future directions:
- Loop until user wants to stop
- Range of dates instead of just one
- Optimizations
