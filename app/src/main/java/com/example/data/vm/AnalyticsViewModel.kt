package com.example.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampaignEntity
import com.example.data.local.LeadEntity
import com.example.data.local.LeadForgeDatabase
import com.example.data.repository.CampaignRepository
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class DateRange { LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, THIS_YEAR }

data class FunnelStage(
    val stage: String,
    val count: Int,
    val percentage: Float
)

data class AnalyticsSummary(
    val totalLeads: Int,
    val qualifiedLeads: Int,
    val avgScore: Int,
    val conversionRate: Float,
    val hotLeadsCount: Int,
    val warmLeadsCount: Int,
    val coldLeadsCount: Int,
    val funnelStages: List<FunnelStage>
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LeadForgeDatabase.getDatabase(application)
    private val leadRepository = LeadRepository(db.leadDao(), db.activityDao(), db.outreachTemplateDao(), db.workspaceDao())
    private val campaignRepository = CampaignRepository(db.campaignDao())

    val allLeads: StateFlow<List<LeadEntity>> = leadRepository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCampaigns: StateFlow<List<CampaignEntity>> = campaignRepository.allCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDateRange = MutableStateFlow(DateRange.LAST_30_DAYS)
    val selectedDateRange: StateFlow<DateRange> = _selectedDateRange.asStateFlow()

    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        allLeads,
        selectedDateRange
    ) { leads, _ ->
        val total = leads.size
        val qualified = leads.count { it.status.equals("Qualified", true) || it.status.equals("Meeting", true) || it.status.equals("Won", true) }
        val won = leads.count { it.status.equals("Won", true) }
        val avgScore = if (total > 0) leads.map { it.score }.average().toInt() else 0
        val conversionRate = if (total > 0) (won.toFloat() / total) * 100f else 0f

        val hot = leads.count { it.score >= 80 }
        val warm = leads.count { it.score in 60..79 }
        val cold = leads.count { it.score < 60 }

        val newCount = leads.count { it.status.equals("New", true) }
        val contactedCount = leads.count { it.status.equals("Contacted", true) }
        val qualCount = leads.count { it.status.equals("Qualified", true) }
        val meetingCount = leads.count { it.status.equals("Meeting", true) }
        val wonCount = won

        val funnel = listOf(
            FunnelStage("New", newCount + contactedCount + qualCount + meetingCount + wonCount, 100f),
            FunnelStage("Contacted", contactedCount + qualCount + meetingCount + wonCount, if (total > 0) ((contactedCount + qualCount + meetingCount + wonCount).toFloat() / total) * 100f else 0f),
            FunnelStage("Qualified", qualCount + meetingCount + wonCount, if (total > 0) ((qualCount + meetingCount + wonCount).toFloat() / total) * 100f else 0f),
            FunnelStage("Meeting", meetingCount + wonCount, if (total > 0) ((meetingCount + wonCount).toFloat() / total) * 100f else 0f),
            FunnelStage("Won", wonCount, if (total > 0) (wonCount.toFloat() / total) * 100f else 0f)
        )

        AnalyticsSummary(
            totalLeads = total,
            qualifiedLeads = qualified,
            avgScore = avgScore,
            conversionRate = conversionRate,
            hotLeadsCount = hot,
            warmLeadsCount = warm,
            coldLeadsCount = cold,
            funnelStages = funnel
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary(0, 0, 0, 0f, 0, 0, 0, emptyList()))

    fun setDateRange(range: DateRange) {
        _selectedDateRange.value = range
    }
}
