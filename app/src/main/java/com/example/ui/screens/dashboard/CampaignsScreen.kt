package com.example.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.CampaignEntity
import com.example.data.vm.CampaignViewModel
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Rose400

@Composable
fun CampaignsScreen(
    campaignVm: CampaignViewModel,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val campaigns by campaignVm.allCampaigns.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Outreach Campaigns", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${campaigns.size} active outbound campaigns", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = { showCreateModal = true },
                modifier = Modifier.testTag("btn_create_campaign"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Campaign")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(campaigns, key = { it.id }) { campaign ->
                CampaignCard(
                    campaign = campaign,
                    onTogglePause = {
                        campaignVm.toggleCampaignPause(campaign)
                        onShowToast("Campaign status updated")
                    },
                    onDelete = {
                        campaignVm.deleteCampaign(campaign.id)
                        onShowToast("Campaign deleted")
                    }
                )
            }
        }
    }

    if (showCreateModal) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var audience by remember { mutableStateOf("B2B SaaS Founders") }

        AlertDialog(
            onDismissRequest = { showCreateModal = false },
            title = { Text("+ Create Outbound Campaign", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Campaign Name") },
                        modifier = Modifier.fillMaxWidth().testTag("campaign_name_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description & Value Prop") },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("campaign_desc_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = audience,
                        onValueChange = { audience = it },
                        label = { Text("Target Persona") },
                        modifier = Modifier.fillMaxWidth().testTag("campaign_audience_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            campaignVm.createCampaign(name, desc, audience)
                            onShowToast("Campaign $name launched!")
                            showCreateModal = false
                        }
                    },
                    modifier = Modifier.testTag("btn_save_campaign")
                ) {
                    Text("Launch Campaign")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateModal = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: CampaignEntity,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("campaign_card_${campaign.id}"),
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
                    Text(campaign.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Surface(
                    color = if (campaign.status == "Active") Emerald400.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = campaign.status,
                        color = if (campaign.status == "Active") Emerald400 else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(campaign.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem("Leads Tagged", "${campaign.leadCount}")
                MetricItem("Sent Outpatient", "${campaign.sentCount}")
                MetricItem("Open Rate", "${campaign.openRate}%")
                MetricItem("Reply Rate", "${campaign.replyRate}%")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onTogglePause, modifier = Modifier.testTag("btn_pause_${campaign.id}")) {
                    Icon(if (campaign.status == "Active") Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Toggle Pause")
                }
                IconButton(onClick = onDelete, modifier = Modifier.testTag("btn_delete_${campaign.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
    }
}
