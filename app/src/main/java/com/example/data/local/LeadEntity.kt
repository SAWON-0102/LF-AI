package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val email: String,
    val company: String,
    val jobTitle: String,
    val industry: String,
    val location: String,
    val website: String,
    val score: Int, // 0 to 100
    val status: String, // "New", "Contacted", "Qualified", "Meeting", "Won", "Lost"
    val priority: String, // "High", "Medium", "Low"
    val source: String, // "AI Search", "Inbound", "CSV Import", "LinkedIn", "Manual"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
