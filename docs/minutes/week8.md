---
Date: 2024-04-02
Time: 14:45-15:30 (45 min)
Location: Drebbelweg - Project Room 3
Chair: Matei Aruxandei
Minute Taker: Andreea Grigoraș
Attendees: Nathan Huisman, Noah Swartjes, Ruben van der Giessen, Matei Aruxandei, Andreea Grigoraș, Vanesa Mitseva, Mike Segers
---

- Opening
  - Check-in (3 min) 
    - All attendees reported being in good spirits.
    - Vanesa joined online.
  - Addition to the agenda (1 min)
    - Small remark if the time allows for ideas from the team about the language switch.
- Announcements from the TA (10 min)
  - SELF-REFLECTION (Draft) - Deadline: this Friday, (week 8) 5th of April
    - The assignment is individual.
    - If possible aim to have less than the maximum (2700 words - about 3 pages).
    - Make sure to have more than 2200 words (minimum).
    - Main things to cover: Self-Reflection process, Code contribution progress (covering the technical learning objectives - aspects learned from the lecture material).
    - IMPORTANT: Reflect on existing knowledge and personal development. You should write it about yourselves, not about someone in general!
    - The submission for this Friday comes with formative feedback, use this (draft) version for the final (summative).
  - BUDDYCHECK - Deadline: next Friday, (week 9) 12th of April
    - Mandatory assignment.
    - Pass/Fail.
    - Not completing it results in failing the course ! ! !
  - CODE FREEZE - Deadline: next Friday, (week 9) 12th of April
    - Friday will be the last day for any form of code to be added to the repository that counts for the code contributions.
    - Submitting code anytime after Friday 23:59 will not be taken into consideration.
    - After this deadline FOCUS on the final presentation (making the product pitch - can be slides/video).
  - Knock-out Criteria
  	- It is not clear if the criteria are in place for week 9 (unknown from the course staff).
  	- All members should contribute to finishing the application, nevertheless.
  - FINAL PRESENTATION - (20 min total)
    - All members should be present at the Final Presentation.
    - It includes 2 parts (as in the rubric on Brightspace) - product pitch (9-10 min) and oral presentation (10 min).
    - Suggested by TA: as a demo make a video running through the application with/without voice-over, and run the application live at the final presentation (possible errors can occur).
    - We might be stopped if we go over time with the product pitch (thus, we should have a clear distribution of our time, roughly up to 10 min in total).
    - Suggested and discussed: making a script (including distribution of parts and minutes) for the product pitch.
  - Code of conduct
    - It can help us with the Self-Reflection assignment.
    - e.g. use the rubric from the Code of Conduct about being on time for the weekly meetings to elaborate on a situation in the Self-Reflection document.

- Presented application (5 min)
  - Server URL was added to the Settings page, and on the StartUp page but it was not shown, providing UI for the user to connect to another server.
  - Discussed having something visible in the UI for ordering and deletion of events in the Management Overview (e.g. showing the arrow in the table columns, button for deletion).
  - Management Overview was completed with the Import JSON Dump feature.
  - Tags for expenses were added for the Statistics feature.
  - Discussed errors: some errors showed in the client from the beginning - not influencing the functionality of the application, deletion of expenses.
  - Discussed the possibility of having one tag for every expense and how will the tags be used for statistics
  - Clarified the email functionality, (is working by changing with good email in the config file)
  
- Talking points (15 min)
  - Product Pitch feedback:
    - Showcase interactions in the form of user stories for the final pitch.
    - Decided to make a video with talk over, and try to run live the application at the final presentation.
    - Special features to present?
    	- Showcase everything the app does and can do.
    	- Have multiple features presented in one user case to optimize the time.
    - For the demo: choose a client user story (group of friends managing an example event) and one user case for the admin part of the application.

  - Implemented features feedback:
    - ordering existing events, working
    - deletion of events, working
    - email feature, working
    - statistics for tags, shown working example 
    - live language switch, shown flags addition, proposed by the team to work with the user being sent a file as a template preferably in English for the custom language (taken from the course staff clarification)
    
  - Previous week (7)
    - Andreea - import dumps for backup in management overview, complete validation of server URL
    - Nathan - deleting expenses, the 'delete' button is to be moved
    - Vanesa - tags for expenses (editing/deleting)
    - Ruben - deleting events from the management overview
    - Matei - email system
    - Noah - open debts in n-1 + added logo

  - Week 8 task distribution:
    - Ruben - testing the application, resolving possible existent bugs.
    - Nathan - deleting of expenses UI fix, looking into bugs for expenses.
    - Matei  - config file check, resolving bugs with the debts.
    - Andreea - languages extra feature, adding translations of UI elements (the rest of the alerts will be translated in week 9).
    - Noah  - Finishing open debts calculation and settling.
    - Vanesa - implementing the Statistics extra feature, for statistics 3rd time entering the colors dissapear, add absolute values (percentages) and relative values (how much money value was spent).
    
  - Testing more of the application before the Code Freeze.
  - Pointed out by TA that Gradle errors appear after running the application.
  - Usuall, be sure the app runs in the terminal with Gradle before running in IntelliJ.
  - Some accessibility things remain to be added (e.g. on key actions for buttons - admin view press enter for login button) although not for open debts yet because they are still not responsive.
  - Everyone received a pass for meeting roles so that is good, make sure to choose a chair and a minute for the last week.
  - Priority on having all basic requirements finished by week 9 so we can see what other bugs there are to be solved before the code freeze.
  - The final presentation schedule has not changed!
  
- After meeting extended talking points:
	- Discussed potential design choices for the custom language, course staff find the solution for this to be the user downloading a file so we'll stick with that.
	- Discussed behaviour of expenses, editing is working now in most cases, but still needs to be looked into more.
	- Discussed the possible influence of the current relations in the database influencing the completion of the statistics feature.
	- Discussed the implementation of open debts, glitching of the debts screen occurs sometimes, calculation of debts determines the chaining of expenses and in some cases they are not settled properly.

    
