package com.parassidhu.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.parassidhu.popularmovies.models.FavoriteMovie;
import com.parassidhu.popularmovies.models.MovieItem;

@Database(entities = {MovieItem.class, FavoriteMovie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();

    private static MovieDatabase INSTANCE;

    private static String DATABASE_NAME = "movie_database";
    public static MovieDatabase getDatabase(final Context context){
        if (INSTANCE==null){
            synchronized (MovieDatabase.class){
                if (INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MovieDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}
