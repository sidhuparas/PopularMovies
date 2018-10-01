<p align="center">
<img src="app/src/main/res/drawable/app_icon.png" width=30%/>
</p>

<h1 align="center">Popular Movies</h1>

This Android Application fetches Popular and Top-Rated movies from TheMovieDB API and show them in a user-friendly interface. It was originally written in Java and then converted step by step into Kotlin. For Java version, click <a href="https://github.com/sidhuparas/PopularMovies/tree/7ff1e7e699c5a111287a4a127e58373da65032b2">here</a>.

<a href="http://downloadinformer.com/downloads/pop-mov.apk">Download APK</a>

## Features

1. Browse Popular and Top-Rated Movies
2. Once loaded, the movie data is saved for offline access
3. Star a movie to mark it as a favorite for easy access
4. Landscape/Portrait Orientation Support

## Libraries Used

- LiveData (To observe data changes)
- ViewModel (To persist data under configuration changes)
- Room (To create and manage database)
- Volley (For network operations)
- Picasso (For loading and caching images)
- Gson (To handle JSON parsing)
- ~ButterKnife (To bind the views)~ Kotlin Android Extensions


## Screenshots

Home Screen             |  Movie Detail Screen
:-------------------------:|:-------------------------:
<img src="screenshots/1.png"/>   |  <img src="screenshots/3.png"/>

Favorites Feature            |  Extra Movie Details
:-------------------------:|:-------------------------:
<img src="screenshots/2.png"/>  | <img src="screenshots/4.png"/> 

## Get Started

1. Clone and open the project.
2. Put your API_KEY in `C:\Users\<Username>\.gradle\gradle.properties` with the name `POPULAR_MOVIES_KEY`.
3. Run the project!

## Credits

- App Icon by <a href="https://www.freepik.com/">FreePik</a>

## License

Copyright (c) 2018 Paras Sidhu

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
