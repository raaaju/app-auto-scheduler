# Auto App Sheduler

Requirement:
- The user can schedule any Android app which is installed on the device to start at a
  specific time.
- The user can cancel the schedule if the scheduled app has not started.
- The user can change the time schedule of an existing scheduled app.
- It should support multiple schedules without time conflicts.
- The schedule record must be kept to query if the schedule was successfully
  executed.

Please build the app in Kotlin, follow the standard software development process, design
your own UI/UX to achieve the above requirements. If any of the requirement you are not
able to implement, you can provide any research you have done.

Solution:
App Screens:
1. Splash Screen
2. Main Screen:
    a. check accessibility service settings for the app is permitted, if not show a dialog to open it explaining why this is necessary to open other apps using AccessibilityService, on clicking open settings on dialog user can go directly to accessibility settings
    b. if permission allowed, get all apps listed(installed on the device) along with a schedule timer on right, make ui model for it
    c. must be viewmodel based compose UI
    d. on clicking on any item on app list -> schedule a timer, with workManager to handle the accessibility service to open the app by package name 

Classes:
1. MyAccessibilityService - to open app package
2. OpenAppWorker - with workmanager to schedule app opening by package name
3. ViewModel Hilt Based - data load
4. Compose UI - screens
5. MVVM - architecture