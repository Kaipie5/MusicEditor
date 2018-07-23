Project 9 - farmanrl/salkinqg/blimpich/kaipie5
====================
Written Reflection
---------------------

### Give a concise overview of your design. How did you divide the code into classes and methods? How does your design for Project 9 differ from your design to Project 8? How did you respond to feedback? (If I get it to you in time.)

> The most significant changes were the song generation algorithm, the logic of which is handled in the Composition class, and the UI/UX is handled in FXMLController. An Export MIDI function was added to the menu to save compositions. Sliders were added for note length, volume, and playback speed. A night mode toggle was added. A splash screen was added for flavor.

> We didn't create any new classes for this project. A lot of the changes were splattered around the codebase, but weren't big enough to be modularized out into an entirely new class. For example, the night mode feature simply uses a button handler that changes the basic CSS of the panes of the program. This does not warrant a full class, especially since this paticular feature is not something that is inherently going to be extended upon, as it sort of stops and ends with the basic changing of some CSS attributes. This was similar to most of the features we implemented: they didn't warrant a new class by themselves. Several implemented features really just needed global variables which the user could interact with (and some smaller implementation changes).

> We tried to respond to feedback. We focused a significant amount of our efforts in trying to fix a bug that has been with us since project 6: you can't undo and redo note position changes. At the time of writing this README we are still working to fix this, but it's a formidable bug that has proven very difficult to squash. We have actually gotten to work, but in the process it has broken other features so it can't be pushed up to master yet.

### Explain why your way was the elegant way to do it. Address any improvements you made.

> It was elegant because the small changes were just that, small. We also found and fixed a small bug having to do with compositions playing after notes have been deleted. This was a one line fix, but was important as making sure the composition stops when notes, paticular when all the notes, are deleted is something that really should be handled by the program. Additionally our solution had us replacing some lone numbers throughout the program with constants.

> Our song generation uses a graph search algorithm developed using music theory to create a web of randomly generated progressions that we can use for notes in the melody and the bass. Rhythm is generated through a a random list of booleans (which right now is set to always be true). 

> Sliders and buttons were created through FXML, and are as simple as possible within FXMLController.

> Export MIDI is handled through the FileManager, and was quite easy to implement thanks to similar methods used in that class.

### Explain what, if anything, in your solution is inelegant and why you didn't make it elegant (for example, maybe you didn't have time or the knowledge to fix it).

> The FXML Controller continues to grow into a beast that takes all the responsibilities that have no where else to go. There are exceptions, for example, some of the functionality went into the composition class for features like AI generated MIDI compositions, but the FXML Controller has reached a impressive 800 lines at the time of writing this README, and that is anything but elegant. 

> The song generation algorithm should likely be given its own class, combining the responsibilities that are split between FXML Controller and Composition. This algorithm is hardcoded to use a C Major scale, and would take significant development to extend. 

> The volume and playback sliders can't modify the song as it's playing, due to limitations with the MIDIPlayer class.

### Include an estimate of your velocity. How many story points did you estimate you would complete during this project? How many did you actually complete, how many person-hours did the team spend, and what is the ratio of points/person-hour?

> We estimated we would complete 10 story points. We completed 7, at a rate of about one per 3.5 hours, spending at least around 25-ish hours total. Our estimates don't seem to be improving, at least from what we can see. They flucuate in accuracy depending on what our schedules end up being and how much time we end up having to work on the project.

### Include a short summary of your team retrospective. What went well that your team will keep doing during the next project assignment? What will you improve? How?

> Since this project included a lot of small features people were able to largely work on their own and not have to worry about any serious merge conflicts. We don't have a next project assignment, so it's difficult to anwser this question, but we all definitely learned a lot from this class as a whole and how we work as a team, and we can carry those lessons into our next group computer science group projects, and can use what we learned here to improve our future work.
