package com.omar.task1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.omar.task1.db.entity.User;

import io.reactivex.Single;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE username=:username AND password=:password")
    LiveData<User> getUser(String username, String password);

    @Query("SELECT * FROM users WHERE id=:id")
    LiveData<User> getUser(long id);


    @Query("SELECT * FROM users WHERE username=:username")
    LiveData<User> getUser(String username);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Insert
    Void insert(User user);

}
