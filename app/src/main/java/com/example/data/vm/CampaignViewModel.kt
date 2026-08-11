package com.example.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampaignEntity
import com.example.data.local.LeadForgeDatabase
import com.example.data.repository.CampaignRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CampaignViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LeadForgeDatabase.getDatabase(application)
    val campaignRepository = CampaignRepository(db.campaignDao())

    val allCampaigns: StateFlow<List<CampaignEntity>> = campaignRepository.allCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCampaign(name: String, description: String, targetAudience: String) {
        viewModelScope.launch {
            val newCampaign = CampaignEntity(
                id = "camp-${UUID.randomUUID().toString().take(8)}",
                workspaceId = "ws-demo-1",
                name = name,
                description = description,
                targetAudience = targetAudience,
                leadCount = 0,
                sentCount = 0,
                openRate = 0f,
                replyRate = 0f,
                status = "Active"
            )
            campaignRepository.insertCampaign(newCampaign)
        }
    }

    fun toggleCampaignPause(campaign: CampaignEntity) {
        viewModelScope.launch {
            val nextStatus = if (campaign.status == "Active") "Paused" else "Active"
            campaignRepository.updateCampaign(campaign.copy(status = nextStatus))
        }
    }

    fun deleteCampaign(id: String) {
        viewModelScope.launch {
            campaignRepository.deleteCampaign(id)
        }
    }

    fun addLeadToCampaign(campaignId: String, leadId: String) {
        viewModelScope.launch {
            campaignRepository.addLeadToCampaign(campaignId, leadId)
        }
    }
}
