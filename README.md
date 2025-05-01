# NewsARC - News App

A modern Android news application that provides up-to-date news from various sources using the News API. The app includes features like user authentication, personalized news feeds, article search, and language preferences.

## Features

- User authentication (Email, Google, Facebook)
- Top headlines from around the world
- Search functionality for news articles
- Category-based news filtering
- Save favorite articles
- Multi-language support
- Push notifications for breaking news
- Dark/Light theme support
- Profile management

## Screenshots

[Add screenshots of your app here]

## Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK 24 or higher
- Firebase account for authentication and database
- News API key from [newsapi.org](https://newsapi.org/)

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/NewsARC.git
cd NewsARC
```

### 2. Set up API Keys

1. Create an account on [newsapi.org](https://newsapi.org/) to get your API key
2. Copy the `ApiKeys.template.java` file:
   ```bash
   cp app/src/main/java/news/operational/NewsGO/ApiKeys.template.java app/src/main/java/news/operational/NewsGO/ApiKeys.java
   ```
3. Open the `ApiKeys.java` file and replace `YOUR_NEWS_API_KEY_HERE` with your actual News API key

### 3. Set up Firebase

1. Create a Firebase project at [firebase.google.com](https://firebase.google.com/)
2. Add an Android app to your Firebase project with package name `news.operational.NewsGO`
3. Download the `google-services.json` file and place it in the `app/` directory
4. Enable Authentication methods in Firebase console (Email/Password, Google, Facebook)
5. Set up Realtime Database and Storage in Firebase console

### 4. Facebook Login Setup (Optional)

1. Create a Facebook Developer account and register your app
2. Follow the Facebook Login integration guide for Android
3. Update your app's resources with the Facebook App ID and Client Token

### 5. Build and Run

Open the project in Android Studio, sync Gradle, and run the application on your device or emulator.

## Architecture

The app follows a modular architecture with the following components:

- UI Layer: Activities and Fragments for user interface
- Data Layer: Retrofit for API calls, Firebase for authentication and storage
- Model Layer: Data classes for news articles and user information

## Libraries Used

- Retrofit for network requests
- Picasso for image loading
- Firebase Auth, Database, and Storage
- Google Sign-In
- Facebook Login SDK
- RecyclerView for list displays
- SwipeRefreshLayout for pull-to-refresh functionality
- Room for local database storage

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [News API](https://newsapi.org/) for providing the news data
- [Firebase](https://firebase.google.com/) for authentication and database services
- All open-source libraries used in this project
