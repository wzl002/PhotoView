package ca.bcit.comp7082.zilong.photogallery.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PictureDao {
    @Query("SELECT * FROM picture order by uid desc")
    public List<Picture> getAll();

    @Query("SELECT * FROM picture WHERE uid IN (:uids) ORDER BY uid DESC")
    public List<Picture> loadAllByIds(int[] uids);

    @Query("SELECT * FROM picture WHERE title LIKE :keyword ORDER BY uid DESC LIMIT 10")
    public List<Picture> findByName(String keyword);

    @Query("SELECT * FROM picture WHERE time BETWEEN :startTime AND :endTime ORDER BY uid DESC LIMIT 10")
    public List<Picture> findByTime(long startTime, long endTime);

    @Query("SELECT * FROM picture WHERE title LIKE :keyword and time BETWEEN :startTime AND :endTime ORDER BY uid DESC LIMIT 10")
    public List<Picture> findByNameAndTime(String keyword, long startTime, long endTime);

    @Update
    public void updatePictures(Picture... pictures);

    /**
     * @param pictures
     * @return new rowIds
     */
    @Insert
    public long[] insertAll(Picture... pictures);

    @Delete
    public void delete(Picture picture);
}