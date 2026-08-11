package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getCampaignById(id: String): CampaignEntity?

    @Query("SELECT * FROM campaigns WHERE id = :id")
    fun observeCampaignById(id: String): Flow<CampaignEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<CampaignEntity>)

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    @Query("DELETE FROM campaigns WHERE id = :id")
    suspend fun deleteCampaignById(id: String)

    // Campaign - Lead cross refs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaignLeadRef(ref: CampaignLeadCrossRef)

    @Query("DELETE FROM campaign_leads WHERE campaignId = :campaignId AND leadId = :leadId")
    suspend fun removeCampaignLeadRef(campaignId: String, leadId: String)

    @Query("SELECT leadId FROM campaign_leads WHERE campaignId = :campaignId")
    fun getLeadIdsForCampaign(campaignId: String): Flow<List<String>>

    @Query("DELETE FROM campaigns")
    suspend fun deleteAllCampaigns()
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE leadId = :leadId ORDER BY createdAt DESC")
    fun getActivitiesForLead(leadId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)
}

@Dao
interface OutreachTemplateDao {
    @Query("SELECT * FROM outreach_templates ORDER BY createdAt DESC")
    fun getAllTemplates(): Flow<List<OutreachTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: OutreachTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<OutreachTemplateEntity>)

    @Query("DELETE FROM outreach_templates WHERE id = :id")
    suspend fun deleteTemplateById(id: String)
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces LIMIT 1")
    fun getWorkspace(): Flow<WorkspaceEntity?>

    @Query("SELECT * FROM workspaces LIMIT 1")
    suspend fun getWorkspaceOnce(): WorkspaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity)

    @Update
    suspend fun updateWorkspace(workspace: WorkspaceEntity)
}
