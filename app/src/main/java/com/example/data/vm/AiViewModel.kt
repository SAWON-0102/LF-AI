package com.example.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiLeadScoreExplanation
import com.example.data.ai.AiOutreachResult
import com.example.data.ai.AiService
import com.example.data.ai.DiscoveredLeadResult
import com.example.data.local.LeadEntity
import com.example.data.local.LeadForgeDatabase
import com.example.data.local.OutreachTemplateEntity
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AiViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AiService()
    private val db = LeadForgeDatabase.getDatabase(application)
    private val leadRepository = LeadRepository(db.leadDao(), db.activityDao(), db.outreachTemplateDao(), db.workspaceDao())

    val isLiveAiConnected: Boolean = aiService.isApiKeyConfigured()

    // Lead Finder State
    private val _isSearchingLeads = MutableStateFlow(false)
    val isSearchingLeads: StateFlow<Boolean> = _isSearchingLeads.asStateFlow()

    private val _discoveredLeads = MutableStateFlow<List<DiscoveredLeadResult>>(emptyList())
    val discoveredLeads: StateFlow<List<DiscoveredLeadResult>> = _discoveredLeads.asStateFlow()

    // Outreach Generator State
    private val _isGeneratingOutreach = MutableStateFlow(false)
    val isGeneratingOutreach: StateFlow<Boolean> = _isGeneratingOutreach.asStateFlow()

    private val _outreachResult = MutableStateFlow<AiOutreachResult?>(null)
    val outreachResult: StateFlow<AiOutreachResult?> = _outreachResult.asStateFlow()

    // Lead Score Explanation State
    private val _isExplainingScore = MutableStateFlow(false)
    val isExplainingScore: StateFlow<Boolean> = _isExplainingScore.asStateFlow()

    private val _scoreExplanation = MutableStateFlow<AiLeadScoreExplanation?>(null)
    val scoreExplanation: StateFlow<AiLeadScoreExplanation?> = _scoreExplanation.asStateFlow()

    fun findLeads(
        industry: String,
        location: String,
        companySize: String,
        jobTitle: String,
        revenueRange: String,
        keywords: String,
        count: Int
    ) {
        viewModelScope.launch {
            _isSearchingLeads.value = true
            val results = aiService.findLeadsWithAi(
                industry = industry,
                location = location,
                companySize = companySize,
                jobTitle = jobTitle,
                revenueRange = revenueRange,
                keywords = keywords,
                count = count
            )
            _discoveredLeads.value = results
            _isSearchingLeads.value = false
        }
    }

    fun importDiscoveredLeadToWorkspace(discovered: DiscoveredLeadResult) {
        viewModelScope.launch {
            val lead = LeadEntity(
                id = "lead-${UUID.randomUUID().toString().take(8)}",
                workspaceId = "ws-demo-1",
                name = discovered.name,
                email = discovered.email,
                company = discovered.company,
                jobTitle = discovered.jobTitle,
                industry = discovered.industry,
                location = discovered.location,
                website = "https://${discovered.company.lowercase().replace(" ", "")}.com",
                score = discovered.score,
                status = "New",
                priority = if (discovered.score >= 80) "High" else "Medium",
                source = "AI Search",
                notes = discovered.reason
            )
            leadRepository.insertLead(lead)
            // Remove from discovered list once imported
            _discoveredLeads.value = _discoveredLeads.value.filter { it.email != discovered.email }
        }
    }

    fun generateOutreach(
        leadName: String,
        company: String,
        jobTitle: String,
        companyDesc: String,
        productService: String,
        valueProp: String,
        cta: String,
        tone: String
    ) {
        viewModelScope.launch {
            _isGeneratingOutreach.value = true
            val result = aiService.generateOutreach(
                leadName, company, jobTitle, companyDesc, productService, valueProp, cta, tone
            )
            _outreachResult.value = result
            _isGeneratingOutreach.value = false
        }
    }

    fun explainLeadScore(
        leadName: String,
        company: String,
        jobTitle: String,
        industry: String,
        location: String,
        score: Int
    ) {
        viewModelScope.launch {
            _isExplainingScore.value = true
            val result = aiService.explainLeadScore(
                leadName, company, jobTitle, industry, location, score
            )
            _scoreExplanation.value = result
            _isExplainingScore.value = false
        }
    }

    fun saveCurrentOutreachAsTemplate(templateName: String) {
        val result = _outreachResult.value ?: return
        viewModelScope.launch {
            val template = OutreachTemplateEntity(
                id = "tpl-${UUID.randomUUID().toString().take(8)}",
                workspaceId = "ws-demo-1",
                name = templateName.ifBlank { "AI Draft Template" },
                subject = result.subject,
                body = result.emailBody,
                linkedinMessage = result.linkedinMessage,
                followUp = result.followUpMessage
            )
            leadRepository.saveTemplate(template)
        }
    }
}
