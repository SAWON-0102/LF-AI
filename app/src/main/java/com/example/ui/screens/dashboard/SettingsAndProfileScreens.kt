package com.example.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.vm.MainViewModel
import com.example.ui.components.UpgradePlanModal

@Composable
fun SettingsAndProfileScreen(
    mainVm: MainViewModel,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workspace by mainVm.activeWorkspace.collectAsState()
    val session by mainVm.userSession.collectAsState()

    var companyName by remember(workspace) { mutableStateOf(workspace?.companyName ?: "GrowthForge Inc.") }
    var industry by remember(workspace) { mutableStateOf(workspace?.industry ?: "SaaS") }
    var targetLocation by remember(workspace) { mutableStateOf(workspace?.targetLocation ?: "North America") }
    var monthlyTarget by remember(workspace) { mutableStateOf((workspace?.monthlyTarget ?: 50).toString()) }

    var showUpgradeModal by remember { mutableStateOf(false) }
    var targetPlanName by remember { mutableStateOf("Agency") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Workspace & Account Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Manage company profile, API secrets, and subscription plan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Tier Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Subscription Tier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Current Active Plan: ${session.plan}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Button(
                        onClick = {
                            targetPlanName = "Growth"
                            showUpgradeModal = true
                        },
                        modifier = Modifier.testTag("btn_upgrade_growth")
                    ) {
                        Text("Upgrade to Growth")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            targetPlanName = "Agency"
                            showUpgradeModal = true
                        },
                        modifier = Modifier.testTag("btn_upgrade_agency")
                    ) {
                        Text("Upgrade to Agency")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Company Workspace Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Company ICP Parameters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth().testTag("settings_company_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = { Text("Primary Industry Focus") },
                    modifier = Modifier.fillMaxWidth().testTag("settings_industry_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetLocation,
                    onValueChange = { targetLocation = it },
                    label = { Text("Target Geographic Markets") },
                    modifier = Modifier.fillMaxWidth().testTag("settings_location_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = monthlyTarget,
                    onValueChange = { monthlyTarget = it },
                    label = { Text("Monthly Qualified Lead Goal") },
                    modifier = Modifier.fillMaxWidth().testTag("settings_target_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val targetInt = monthlyTarget.toIntOrNull() ?: 50
                        mainViewModelComplete(mainVm, companyName, industry, targetLocation, targetInt)
                        onShowToast("Workspace parameters saved successfully!")
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_save_workspace_settings")
                ) {
                    Text("Save Workspace Settings")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Key & Secrets Panel Guidance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Provider API Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "API keys (such as GEMINI_API_KEY) are managed securely via the AI Studio Secrets Panel. Injected keys automatically activate live AI features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    UpgradePlanModal(
        show = showUpgradeModal,
        targetPlan = targetPlanName,
        priceMonthly = if (targetPlanName == "Agency") "$199/mo" else "$79/mo",
        onConfirmUpgrade = {
            mainVm.upgradePlan(targetPlanName)
            onShowToast("Upgraded plan to $targetPlanName!")
            showUpgradeModal = false
        },
        onDismiss = { showUpgradeModal = false }
    )
}

private fun mainViewModelComplete(mainVm: MainViewModel, company: String, ind: String, loc: String, target: Int) {
    val current = mainVm.activeWorkspace.value ?: return
    mainVm.completeOnboarding(
        fullName = current.userName,
        companyName = company,
        industry = ind,
        companySize = current.companySize,
        targetMarket = current.targetMarket,
        targetLocation = loc,
        jobTitles = current.targetJobTitles,
        monthlyTarget = target
    )
}
