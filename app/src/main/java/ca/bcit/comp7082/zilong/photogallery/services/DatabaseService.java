package ca.bcit.comp7082.zilong.photogallery.services;

import android.arch.persistence.room.Room;
import android.content.Context;

import ca.bcit.comp7082.zilong.photogallery.models.AppDatabase;
import ca.bcit.comp7082.zilong.photogallery.models.PictureDao;

public class DatabaseService {

    private static AppDatabase db = null;

    public static void initDatabaseService(Context context) {
        if (DatabaseService.db == null) {
            DatabaseService.db = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app-database").allowMainThreadQueries().build(); //TODO allowMainThreadQueries() is not recommend
        }
    }

    public static AppDatabase getDatabase() {
        return db;
    }

    public static PictureDao getPictureDao() {
        return DatabaseService.getDatabase().pictureDao();
    }

}
