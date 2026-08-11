package com.example.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LeadEntity
import com.example.data.local.LeadForgeDatabase
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class SortOption { SCORE_DESC, SCORE_ASC, NAME_ASC, DATE_DESC }

class LeadViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LeadForgeDatabase.getDatabase(application)
    val leadRepository = LeadRepository(db.leadDao(), db.activityDao(), db.outreachTemplateDao(), db.workspaceDao())

    val allLeads: StateFlow<List<LeadEntity>> = leadRepository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow("All") // "All", "New", "Contacted", "Qualified", "Meeting", "Won", "Lost"
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val _selectedPriority = MutableStateFlow("All") // "All", "High", "Medium", "Low"
    val selectedPriority: StateFlow<String> = _selectedPriority.asStateFlow()

    private val _selectedIndustry = MutableStateFlow("All")
    val selectedIndustry: StateFlow<String> = _selectedIndustry.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.SCORE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _selectedLeadIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedLeadIds: StateFlow<Set<String>> = _selectedLeadIds.asStateFlow()

    val filteredLeads: StateFlow<List<LeadEntity>> = combine(
        allLeads,
        searchQuery,
        selectedStatus,
        selectedPriority,
        combine(selectedIndustry, sortOption) { ind, sort -> ind to sort }
    ) { leads, query, status, priority, indAndSort ->
        val (industry, sort) = indAndSort
        var list = leads.filter { lead ->
            val matchesQuery = query.isBlank() ||
                    lead.name.contains(query, ignoreCase = true) ||
                    lead.company.contains(query, ignoreCase = true) ||
                    lead.jobTitle.contains(query, ignoreCase = true) ||
                    lead.email.contains(query, ignoreCase = true)

            val matchesStatus = status == "All" || lead.status.equals(status, ignoreCase = true)
            val matchesPriority = priority == "All" || lead.priority.equals(priority, ignoreCase = true)
            val matchesIndustry = industry == "All" || lead.industry.equals(industry, ignoreCase = true)

            matchesQuery && matchesStatus && matchesPriority && matchesIndustry
        }

        list = when (sort) {
            SortOption.SCORE_DESC -> list.sortedByDescending { it.score }
            SortOption.SCORE_ASC -> list.sortedBy { it.score }
            SortOption.NAME_ASC -> list.sortedBy { it.name }
            SortOption.DATE_DESC -> list.sortedByDescending { it.createdAt }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getLeadById(id: String): kotlinx.coroutines.flow.Flow<LeadEntity?> {
        return leadRepository.getLeadById(id)
    }

    fun getActivitiesForLead(id: String): kotlinx.coroutines.flow.Flow<List<com.example.data.local.ActivityEntity>> {
        return leadRepository.getActivitiesForLead(id)
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setStatusFilter(s: String) { _selectedStatus.value = s }
    fun setPriorityFilter(p: String) { _selectedPriority.value = p }
    fun setIndustryFilter(i: String) { _selectedIndustry.value = i }
    fun setSortOption(s: SortOption) { _sortOption.value = s }

    fun toggleLeadSelection(id: String) {
        val current = _selectedLeadIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _selectedLeadIds.value = current
    }

    fun selectAllLeads(select: Boolean) {
        if (select) {
            _selectedLeadIds.value = filteredLeads.value.map { it.id }.toSet()
        } else {
            _selectedLeadIds.value = emptySet()
        }
    }

    fun addLead(
        name: String,
        email: String,
        company: String,
        jobTitle: String,
        industry: String,
        location: String,
        website: String,
        score: Int,
        status: String,
        priority: String,
        notes: String
    ) {
        viewModelScope.launch {
            val newLead = LeadEntity(
                id = "lead-${UUID.randomUUID().toString().take(8)}",
                workspaceId = "ws-demo-1",
                name = name,
                email = email,
                company = company,
                jobTitle = jobTitle,
                industry = industry.ifBlank { "SaaS" },
                location = location.ifBlank { "United States" },
                website = website.ifBlank { "https://$company.com".lowercase().replace(" ", "") },
                score = score.coerceIn(0, 100),
                status = status.ifBlank { "New" },
                priority = priority.ifBlank { "Medium" },
                source = "Manual",
                notes = notes
            )
            leadRepository.insertLead(newLead)
        }
    }

    fun updateLead(lead: LeadEntity) {
        viewModelScope.launch {
            leadRepository.updateLead(lead)
        }
    }

    fun addNote(leadId: String, noteText: String) {
        viewModelScope.launch {
            leadRepository.addLeadNote(leadId, noteText)
        }
    }

    fun deleteLead(id: String) {
        viewModelScope.launch {
            leadRepository.deleteLead(id)
            val current = _selectedLeadIds.value.toMutableSet()
            current.remove(id)
            _selectedLeadIds.value = current
        }
    }

    fun bulkDeleteSelected() {
        viewModelScope.launch {
            val ids = _selectedLeadIds.value.toList()
            if (ids.isNotEmpty()) {
                leadRepository.deleteLeads(ids)
                _selectedLeadIds.value = emptySet()
            }
        }
    }

    fun bulkUpdateStatus(newStatus: String) {
        viewModelScope.launch {
            val ids = _selectedLeadIds.value.toList()
            if (ids.isNotEmpty()) {
                leadRepository.updateLeadsStatus(ids, newStatus)
                _selectedLeadIds.value = emptySet()
            }
        }
    }

    fun exportToCsvString(): String {
        val leads = filteredLeads.value
        val sb = StringBuilder()
        sb.append("ID,Name,Email,Company,JobTitle,Industry,Location,Score,Status,Priority,Source\n")
        leads.forEach { l ->
            sb.append("\"${l.id}\",\"${l.name}\",\"${l.email}\",\"${l.company}\",\"${l.jobTitle}\",\"${l.industry}\",\"${l.location}\",${l.score},\"${l.status}\",\"${l.priority}\",\"${l.source}\"\n")
        }
        return sb.toString()
    }
}
