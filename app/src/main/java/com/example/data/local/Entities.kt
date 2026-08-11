package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val leadId: String,
    val type: String, // "Note Added", "Status Changed", "Outreach Generated", "Score Evaluated", "Email Sent", "Created"
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "outreach_templates")
data class OutreachTemplateEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val subject: String,
    val body: String,
    val linkedinMessage: String = "",
    val followUp: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val companyName: String,
    val industry: String,
    val companySize: String,
    val targetMarket: String,
    val targetLocation: String,
    val idealCustomer: String,
    val targetJobTitles: String,
    val monthlyTarget: Int,
    val plan: String = "Growth", // "Starter", "Growth", "Agency"
    val isOnboarded: Boolean = true
)
