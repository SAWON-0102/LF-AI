package com.example.data.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LeadForgeDatabase
import com.example.data.local.WorkspaceEntity
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class UserSession(
    val isAuthenticated: Boolean = true, // Logged in by default for seamless demo
    val email: String = "alex.vance@growthforge.io",
    val name: String = "Alex Vance",
    val company: String = "GrowthForge Inc.",
    val plan: String = "Growth" // "Starter", "Growth", "Agency"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LeadForgeDatabase.getDatabase(application)
    val leadRepository = LeadRepository(db.leadDao(), db.activityDao(), db.outreachTemplateDao(), db.workspaceDao())

    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    val activeWorkspace: StateFlow<WorkspaceEntity?> = leadRepository.activeWorkspace
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showToast(message: String) {
        _userMessage.value = message
    }

    fun clearToast() {
        _userMessage.value = null
    }

    fun login(email: String, name: String = "Growth Leader") {
        _userSession.value = UserSession(
            isAuthenticated = true,
            email = email,
            name = name,
            company = "LeadForge Partner",
            plan = "Growth"
        )
        showToast("Logged in as $email")
    }

    fun logout() {
        _userSession.value = UserSession(isAuthenticated = false)
        showToast("Logged out successfully")
    }

    fun completeOnboarding(
        fullName: String,
        companyName: String,
        industry: String,
        companySize: String,
        targetMarket: String,
        targetLocation: String,
        jobTitles: String,
        monthlyTarget: Int
    ) {
        viewModelScope.launch {
            val current = activeWorkspace.value
            val updated = (current ?: WorkspaceEntity(
                id = "ws-demo-1",
                userId = "usr-1",
                userName = fullName,
                userEmail = _userSession.value.email,
                companyName = companyName,
                industry = industry,
                companySize = companySize,
                targetMarket = targetMarket,
                targetLocation = targetLocation,
                idealCustomer = "Target leads in $industry",
                targetJobTitles = jobTitles,
                monthlyTarget = monthlyTarget,
                plan = _userSession.value.plan,
                isOnboarded = true
            )).copy(
                userName = fullName,
                companyName = companyName,
                industry = industry,
                companySize = companySize,
                targetMarket = targetMarket,
                targetLocation = targetLocation,
                targetJobTitles = jobTitles,
                monthlyTarget = monthlyTarget,
                isOnboarded = true
            )

            leadRepository.updateWorkspace(updated)
            _userSession.value = _userSession.value.copy(name = fullName, company = companyName)
            showToast("Your LeadForge AI workspace is ready!")
        }
    }

    fun upgradePlan(newPlan: String) {
        viewModelScope.launch {
            val ws = activeWorkspace.value
            if (ws != null) {
                leadRepository.updateWorkspace(ws.copy(plan = newPlan))
            }
            _userSession.value = _userSession.value.copy(plan = newPlan)
            showToast("Successfully upgraded workspace to $newPlan Plan!")
        }
    }
}
