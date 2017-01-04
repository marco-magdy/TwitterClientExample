# TwitterClientExample

###Twitter Client is an android application using fabric platform “Twitter Kit”, to interact with twitter SDK.

##Features:
- Login to the app using Twitter authentication.
- Enables multiple accounts
- Get user followers in a list that contains profile image, full name and bio, with the advantages of pull to refresh and endless scrolling.
- Display logged in user followers “online and offline mode”
- Display follower tweets, profile image and background banner in a sticky header. 
- Localization(Arabic and English)

#Screens

##1. Splash Screen
Starting screen that contains an image view with frequently fade-in and fade-out animation.

<img src="https://cloud.githubusercontent.com/assets/3523226/21662590/2a16df14-d2e3-11e6-8e40-ed379c3e0d73.png" width="300" align="middle">

##2.Login Screen
Contains a login button, when the user click this button a web page is opened and ask the user to grant authentication for the application,

<img src="https://cloud.githubusercontent.com/assets/3523226/21662724/32ce7dd2-d2e4-11e6-8c25-b38fb18e8e8e.png" width="300">

##3.Follower List screen
Contains a list of user followers ,pull to refresh and endless scroll view for loading more users.

If the network unavailable, then the list displays the already cached data.

It also contains a drop down menu that show multi-accounts that they already saved 
on the settings, when the user choose any of these accounts, the page will reload and show the followers corresponding to the selected
account. The user can also logout from the account.

<img src="https://cloud.githubusercontent.com/assets/3523226/21662771/7942257a-d2e4-11e6-97b1-8153f6093b35.png" width="300">
<img src="https://cloud.githubusercontent.com/assets/3523226/21663401/a34d39aa-d2e8-11e6-9266-4fab237b7e80.png" width="300">

##4.Follower information screen
This screen displays profile image and background image of a previously selected follower from the followers list", it also displays the last 10 tweets of that user

It contains a sticky header for the user background,
when the user scroll the screen, the background should still stick to the top of the screen and stretch.

<img src="https://cloud.githubusercontent.com/assets/3523226/21662820/cc9e6710-d2e4-11e6-9223-86ed8671e33d.png" width="300">
<img src="https://cloud.githubusercontent.com/assets/3523226/21662836/e29e5dcc-d2e4-11e6-8ef7-ff18b8426832.png" width="300">

##5. Multi-Language
The application supports multi-language (English, Arabic) and RTL Support

<img src="https://cloud.githubusercontent.com/assets/3523226/21663211/6fe0178c-d2e7-11e6-8ec0-635d4a93e3eb.png" width="300">
<img src="https://cloud.githubusercontent.com/assets/3523226/21663212/6fe3bafe-d2e7-11e6-95ca-f5c12d910610.png" width="300">

#Libraries and frameworks:

##1.	Fabric framework

 Fabric is a platform that helps your mobile team build better apps, understand your users in real time, and grow your business based on analytics. It is not supported for iOS, Android, tvOS, Unity platforms.
##2.	Universal Image Loader

 Universal Image Loader is an smart and powerful library that helps in loading, caching and displaying images on Android. This means, using this library you can download remote images and display on ImageView.
 
 Universal Image Loader Features:
   - Asynchronous and multi-threaded image loading. This allows you to download multiple images Asynchronously.
   - Supports various configurations that helps to tune for your requirement. With this you can control memory, cache type, decoder, display image options, etc.
   - Possibility of image caching in memory and/or on device’s file system (or SD card)
   - Possibility to “listen” loading process. Allows various callback methods using which you will get to know the progress/state of your download request.

##3.	Retrofit 2.0
Retrofit is a REST Client for Android and Java by Square. It makes it relatively easy to retrieve and upload JSON (or other structured data) via a REST based webservice. Retrofit can be configured with converter is used for its data serialization. Typically for JSON you use GSon, but you can add custom converters to process XML or other protocols. Retrofit uses the OkHttp library for HTTP requests.

#Design patterns:

1. Dependency injection.
2. Adapter.


