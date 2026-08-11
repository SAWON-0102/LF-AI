package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeadEntity
import com.example.data.vm.LeadViewModel
import com.example.data.vm.MainViewModel
import com.example.ui.components.AiModeBanner
import com.example.ui.components.LeadScoreBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.HotScoreColor
import com.example.ui.theme.WarmScoreColor

@Composable
fun DashboardScreen(
    mainVm: MainViewModel,
    leadVm: LeadViewModel,
    onNavigateLeads: () -> Unit,
    onNavigateLeadDetail: (String) -> Unit,
    onNavigateAiFinder: () -> Unit,
    onNavigateCampaigns: () -> Unit,
    onNavigateAnalytics: () -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val leads by leadVm.allLeads.collectAsState()
    val workspace by mainVm.activeWorkspace.collectAsState()
    val scrollState = rememberScrollState()

    val totalLeads = leads.size
    val qualifiedLeads = leads.count { it.status.equals("Qualified", true) || it.status.equals("Meeting", true) || it.status.equals("Won", true) }
    val avgScore = if (totalLeads > 0) leads.map { it.score }.average().toInt() else 0
    val wonCount = leads.count { it.status.equals("Won", true) }
    val conversionRate = if (totalLeads > 0) String.format("%.1f%%", (wonCount.toFloat() / totalLeads) * 100f) else "0.0%"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Welcome Header & Quick Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome back, ${workspace?.userName ?: "Alex"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${workspace?.companyName ?: "GrowthForge Inc."} Workspace • Plan: ${workspace?.plan ?: "Growth"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.clickable { onNavigateSettings() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Amber400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = workspace?.plan ?: "Growth",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Status Banner
        AiModeBanner(
            isLiveAiConnected = true,
            onConnectClick = { onNavigateSettings() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Bar
        Text("Quick Lead Actions", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateAiFinder,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("dash_action_ai_finder"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Lead Finder", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateLeads,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("dash_action_all_leads"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manage Leads", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // KPI Stat Grid
        Text("Pipeline Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Total Leads",
                value = "$totalLeads",
                changeText = "14%",
                isPositive = true,
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f),
                testTag = "kpi_total_leads"
            )
            StatCard(
                title = "Qualified",
                value = "$qualifiedLeads",
                changeText = "22%",
                isPositive = true,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                testTag = "kpi_qualified_leads"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Avg AI Score",
                value = "$avgScore/100",
                changeText = "8 pts",
                isPositive = true,
                icon = Icons.Default.Psychology,
                modifier = Modifier.weight(1f),
                testTag = "kpi_avg_score"
            )
            StatCard(
                title = "Conversion",
                value = conversionRate,
                changeText = "3.2%",
                isPositive = true,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f),
                testTag = "kpi_conversion"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent High-Intent Prospects Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("High-Intent Prospects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onNavigateLeads) {
                Text("View All ($totalLeads)")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val topLeads = leads.sortedByDescending { it.score }.take(5)
        topLeads.forEach { lead ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onNavigateLeadDetail(lead.id) }
                    .testTag("dash_lead_row_${lead.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lead.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("${lead.jobTitle} • ${lead.company}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LeadScoreBadge(score = lead.score, showLabel = false)
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(status = lead.status)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campaign Quick Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active Campaigns", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onNavigateCampaigns) {
                        Text("Manage Campaigns")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Q3 SaaS Founders Outreach • 58.4% Open Rate • 22.1% Reply Rate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
