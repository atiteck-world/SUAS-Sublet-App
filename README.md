# SUAS Sublet App
Sublet App is a mobile application that allows users to list and browse sublet properties. It provides functionality for users to create, edit, and delete sublets, along with the ability to upload and view images, contact subletters, and manage personal profiles.

## Features
  1 **Browse Sublets:** Users can browse available sublet properties with detailed information.
  
  2 **Search:** Search rooms based on location.
  
  3 **Add Sublet:** Users can create new sublet listings, providing property details, availability dates, and prices.
  
  4 **Upload Images:** Upload room images for sublets directly from your device.
  
  5 **Delete Sublet:** Users can edit or delete their sublet listings.
  
  6 **Location Picker:** Select a property location using Google Maps.
  
  7 **Profile Management:** Edit and manage user profile details and upload profile pictures.
  
  8 **Forgot Password:** Users can reset their password if forgotten.

## Installation
  1. **Clone this repository to your local machine:**
  
      git clone
      ```bash
      https://github.com/yourusername/suas-sublet-app.git
  
  2. **Open the project in Android Studio.**
  
  3. **Make sure the following dependencies are installed:**
  This project uses the following libraries:

- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Google Maps SDK
- Picasso for image loading
- hdodenhof for circle image

To add them, include these dependencies in your build.gradle file:

  build.gradle.kts (app level)
          
            dependencies {
            implementation(libs.appcompat)
            implementation(libs.material)
            implementation(libs.activity)
            implementation(libs.constraintlayout)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.database)
            implementation(libs.play.services.maps)
            implementation(libs.firebase.auth)
            implementation(libs.play.services.location)
            implementation(libs.firebase.storage)
            testImplementation(libs.junit)
            androidTestImplementation(libs.ext.junit)
            androidTestImplementation(libs.espresso.core)
            implementation("com.squareup.picasso:picasso:2.8")
            implementation ("de.hdodenhof:circleimageview:3.1.0")
          }

          
  - Add your google-services.json to the project to link Firebase services.
  
  4. **Sync Gradle and run the app on an Android emulator or device.**

## Dependencies
  This project uses the following libraries:
  
  Firebase Authentication
  Firebase Realtime Database
  Firebase Storage
  Google Maps SDK
  Picasso for image loading
  hdodenhof for circle image
  To add them, include these dependencies in your build.gradle file:

  build.gradle.kts (app level)
      
    Copy code
      
        dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.firestore)
        implementation(libs.firebase.database)
        implementation(libs.play.services.maps)
        implementation(libs.firebase.auth)
        implementation(libs.play.services.location)
        implementation(libs.firebase.storage)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation("com.squareup.picasso:picasso:2.8")
        implementation ("de.hdodenhof:circleimageview:3.1.0")
      }

## Usage
1. Create an account or log in to access sublet listings.
2. Browse available sublets or create a new sublet listing.
3. Use Google Maps to set the property location.
4. Upload images of the sublet directly from your device.
5. Manage listings: delete sublets at any time.
6. Contact subletters via email, SMS, or phone call.

## Developer
Bismark Azumah Atiim

## Contact
If you have any questions or feedback, feel free to reach out at:

Email: atiimbis@gmail.com
