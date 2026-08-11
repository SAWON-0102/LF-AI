package com.example.data.repository

import com.example.data.local.CampaignDao
import com.example.data.local.CampaignEntity
import com.example.data.local.CampaignLeadCrossRef
import kotlinx.coroutines.flow.Flow

class CampaignRepository(
    private val campaignDao: CampaignDao
) {
    val allCampaigns: Flow<List<CampaignEntity>> = campaignDao.getAllCampaigns()

    fun getCampaignById(id: String): Flow<CampaignEntity?> = campaignDao.observeCampaignById(id)

    suspend fun insertCampaign(campaign: CampaignEntity) {
        campaignDao.insertCampaign(campaign)
    }

    suspend fun updateCampaign(campaign: CampaignEntity) {
        campaignDao.updateCampaign(campaign)
    }

    suspend fun deleteCampaign(id: String) {
        campaignDao.deleteCampaignById(id)
    }

    suspend fun addLeadToCampaign(campaignId: String, leadId: String) {
        campaignDao.insertCampaignLeadRef(CampaignLeadCrossRef(campaignId, leadId))
        val campaign = campaignDao.getCampaignById(campaignId) ?: return
        val updated = campaign.copy(leadCount = campaign.leadCount + 1)
        campaignDao.updateCampaign(updated)
    }

    suspend fun removeLeadFromCampaign(campaignId: String, leadId: String) {
        campaignDao.removeCampaignLeadRef(campaignId, leadId)
        val campaign = campaignDao.getCampaignById(campaignId) ?: return
        val updated = campaign.copy(leadCount = (campaign.leadCount - 1).coerceAtLeast(0))
        campaignDao.updateCampaign(updated)
    }

    fun getLeadIdsForCampaign(campaignId: String): Flow<List<String>> {
        return campaignDao.getLeadIdsForCampaign(campaignId)
    }
}
