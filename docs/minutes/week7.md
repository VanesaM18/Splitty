---
Date: 2024-03-26
Time: 14:45-15:30 (45 min)
Location: Drebbelweg - Project Room 3
Chair: Noah Swartjes
"Minute Taker": Vanesa Mitseva
Attendees: Nathan Huisman, Noah Swartjes, Ruben van der Giessen, Matei Aruxandei, Andreea Grigoraș, Vanesa Mitseva, Mike Segers
---
- Opening
  - Addition to the agenda (5 min)
    - Nathan found a bug that the expenses don't get deleted from the database. Ruben said that he fixed the same thing with the participants and will look into the expenses as well.
- Announcements from the TA (10 min)
  - Our final presentation is on the 19th of April, 13:20
  - Questions from the lectures material will be asked, but overall, not much theory. For example, where we use Dependency injection?
  - product pitch - video, presentation
  - The product pitch can be either a video or a presentation. We decided that we would do a video and will talk over it.
  - Not everyone needs to present in oral presentation, but each of us can be asked about any part of the application. Redirecting questions might be possible, but general knowledge about every part of the app is needed.
  - We shouldn't forget to reach the knockout criteria!
- Presented application (10 min)
  - New things:
    - Open debts - deletion works now.
    - Tags - basic tags are added.
    - Management overview - now events are shown there and json can be downloaded.
  - Found bugs:
    - Email, IBAN and BIC should become optional.
    - Tags can be added multiple times when editing, which should be fixed. 
  - It should be more intuitive how to use the application.
  - Update README.md - more details, maybe screenshots - how to set up the application.
- Talking points (15 min)
  - HCI feedback:
    - Colors - we talked about different design options, but decided that we like how our app look now, and we will leave it like that.
    - Noah presented an app logo, which we can use.
    - Shortcuts
      - They need to be explained in the README.md
      - A message could be shown when hovering certain buttons.
    - Error messages should be shown in the terminal
  - Testing
    - Indirect - user-based manner, usage tests, how things work together.
  - Previous week
    - Andreea implemented connecting to a localhost with another port.
    - Nathan - edit/delete/sync expenses - delete has some more things to be fixed.
    - Ruben, Vanesa - A lot of tests were added - UI ones including
    - Matei - long polling - sometimes connection doesn't get closed, but still works.
    - Noah - Open debts - almost finished, deletion remains.
    - Vanesa - Basic tags are added.
  - Product pitch (draft)
    - What makes our application unique? - Still needs to be discussed.
    - For the oral presentation - PowerPoint is a good idea.
    - Live showcase is another one, but the time is not enough for that.
    - Have a backup if the demo fails! (video how it works)
  - We decided that events can have the same name.
