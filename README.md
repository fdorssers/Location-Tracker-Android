# Location-Tracker-Android
An Android app giving regular location updates of the user, even when the app is in the background, by using a sticky notification.

This was mostly just a proof of concept to see if and how it could be done.

Possible extensions could be pushing these locations to a server by using a job scheduling library and firing off events in `onNext`.
The advantage of many of these job scheduling libraries is that they often also support serialization of events, so locations are automatically stored if these are received while the user is offline.
