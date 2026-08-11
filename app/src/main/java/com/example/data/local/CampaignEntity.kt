package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val description: String,
    val targetAudience: String,
    val leadCount: Int,
    val sentCount: Int,
    val openRate: Float, // percentage e.g. 48.5f
    val replyRate: Float, // percentage e.g. 18.2f
    val status: String, // "Draft", "Active", "Paused", "Completed"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "campaign_leads", primaryKeys = ["campaignId", "leadId"])
data class CampaignLeadCrossRef(
    val campaignId: String,
    val leadId: String
)
