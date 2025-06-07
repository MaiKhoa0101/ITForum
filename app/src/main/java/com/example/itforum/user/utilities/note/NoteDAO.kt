package com.example.itforum.user.utilities.note

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.itforum.user.utilities.note.NoteEntity
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE userId = :userId")
    fun getAllNotes(userId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)
}


