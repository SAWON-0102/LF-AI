package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id")
    suspend fun getLeadById(id: String): LeadEntity?

    @Query("SELECT * FROM leads WHERE id = :id")
    fun observeLeadById(id: String): Flow<LeadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: String)

    @Query("DELETE FROM leads WHERE id IN (:ids)")
    suspend fun deleteLeadsByIds(ids: List<String>)

    @Query("UPDATE leads SET status = :status, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun updateLeadsStatus(ids: List<String>, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM leads")
    suspend fun deleteAllLeads()
}
