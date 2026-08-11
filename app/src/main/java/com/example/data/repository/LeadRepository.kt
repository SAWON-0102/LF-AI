package com.example.data.repository

import com.example.data.local.ActivityDao
import com.example.data.local.ActivityEntity
import com.example.data.local.LeadDao
import com.example.data.local.LeadEntity
import com.example.data.local.OutreachTemplateDao
import com.example.data.local.OutreachTemplateEntity
import com.example.data.local.WorkspaceDao
import com.example.data.local.WorkspaceEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LeadRepository(
    private val leadDao: LeadDao,
    private val activityDao: ActivityDao,
    private val templateDao: OutreachTemplateDao,
    private val workspaceDao: WorkspaceDao
) {
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    val activeWorkspace: Flow<WorkspaceEntity?> = workspaceDao.getWorkspace()
    val allTemplates: Flow<List<OutreachTemplateEntity>> = templateDao.getAllTemplates()

    fun getLeadById(id: String): Flow<LeadEntity?> = leadDao.observeLeadById(id)
    fun getActivitiesForLead(leadId: String): Flow<List<ActivityEntity>> = activityDao.getActivitiesForLead(leadId)

    suspend fun insertLead(lead: LeadEntity) {
        leadDao.insertLead(lead)
        activityDao.insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                leadId = lead.id,
                type = "Created",
                description = "Lead created: ${lead.name} (${lead.company}) - Score ${lead.score}"
            )
        )
    }

    suspend fun insertLeads(leads: List<LeadEntity>) {
        leadDao.insertLeads(leads)
    }

    suspend fun updateLead(lead: LeadEntity) {
        leadDao.updateLead(lead)
        activityDao.insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                leadId = lead.id,
                type = "Status Changed",
                description = "Lead updated: Status set to ${lead.status}, Priority ${lead.priority}"
            )
        )
    }

    suspend fun addLeadNote(leadId: String, note: String) {
        val lead = leadDao.getLeadById(leadId) ?: return
        val updatedNotes = if (lead.notes.isBlank()) note else "${lead.notes}\n• $note"
        val updated = lead.copy(notes = updatedNotes, updatedAt = System.currentTimeMillis())
        leadDao.updateLead(updated)
        activityDao.insertActivity(
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                leadId = leadId,
                type = "Note Added",
                description = "Note added: \"$note\""
            )
        )
    }

    suspend fun deleteLead(id: String) {
        leadDao.deleteLeadById(id)
    }

    suspend fun deleteLeads(ids: List<String>) {
        leadDao.deleteLeadsByIds(ids)
    }

    suspend fun updateLeadsStatus(ids: List<String>, newStatus: String) {
        leadDao.updateLeadsStatus(ids, newStatus)
        ids.forEach { leadId ->
            activityDao.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    leadId = leadId,
                    type = "Status Changed",
                    description = "Bulk status update: changed to $newStatus"
                )
            )
        }
    }

    suspend fun saveTemplate(template: OutreachTemplateEntity) {
        templateDao.insertTemplate(template)
    }

    suspend fun updateWorkspace(workspace: WorkspaceEntity) {
        workspaceDao.updateWorkspace(workspace)
    }
}
