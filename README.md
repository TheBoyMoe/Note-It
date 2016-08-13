Note-It Android App
===================

Inspired by Google Keep, Note-It allows you to create and save text, photo audio and video notes. 
Note titles and descriptions are saved to an SQLite database. All images, video and audio are saved
to the device's external storage. Notes are displayed in the main screen via a RecyclerView 
implementing a Staggered Layout Manager. Each note is shown in a CardView.

The app was a learning exercise in the implementation of the following:
- SQLite database
- Phone layout using activities/fragments
- activity/fragment communication via interfaces
- RecyclerView implementing Multi Choice Mode
- Android 6 Permissions
- EventBus 
- Background Threading
- Android CardView

Pre-requisites
--------------

- Min Android SDK supported v16

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Screenshots
-----------

![Phone](screenshots/note-it-app-preview-1.gif "Interacting with the app, add a text note")
![Phone](screenshots/note-it-app-preview-2.gif "Interacting with the app, delete a number o notes simultaneously")

Credit
------
The project uses the following 3rd party libraries:
- GreenRobot EventBus (https://github.com/greenrobot/EventBus)
- Square's Picasso Image downloading and caching library (https://github.com/square/picasso)
- Timber Android logging library by Jake Wharton (https://github.com/JakeWharton/timber)
- Material Dialogs library by Aidan Follestad (https://github.com/afollestad/material-dialogs)
- Floating Action Button library by Base Lab (https://github.com/futuresimple/android-floating-action-button)

MIT License
-----------

Copyright (c) [2016] [William Fero]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.