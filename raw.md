Create a project plan of a software product and save the plan to the project directory.
This should be a Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP) project.

Application should works with maps (show current location and route to destination).
App should have Splash screen to check Maps API key, then show Error screen if API key is invalid or go to Search Address screen.
Search Address screen should have a text input fields (departure and destination) and a list of search results.
On Search Address screen app need to request Location permissions. If user reject permission - user can type departure address.
After selecting a address item user should navigate to screen with maps and rout to the selected address.

Maps view should be abstracted. We should have an ":ui:uiMapsViewApi" Gradle module with interfaces and ":ui:uiMapsViewHere" Gradle module with implementations.

We should have a ":core:coreKeyValidationApi", ":core:coreKeyValidationHere" to validate Maps API key.
We should have a ":core:coreAddressSearchApi", ":core:coreAddressSearchHere" to search addresses by user prompt.
We should have a ":core:coreMapRouteApi", ":core:coreMapRouteHere" to get a route from current location to destination.
We should have a ":core:coreLocationApi", ":core:coreLocationNative" to listen current location and location updates.

Documentation for the 4.25.1.0 release of HERE SDK for Android (Explore) https://www.here.com/docs/category/here-sdk-android-explore-latest
Documentation for the 4.25.1.0 release of HERE SDK for iOS (Explore) https://www.here.com/docs/category/here-sdk-ios-explore-latest

Ask questions until all requirements are clear.
