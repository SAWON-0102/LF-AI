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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Emerald400

data class IntegrationItem(
    val name: String,
    val category: String,
    val description: String,
    var isConnected: Boolean
)

@Composable
fun IntegrationsScreen(
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var integrations by remember {
        mutableStateOf(
            listOf(
                IntegrationItem("Salesforce CRM", "CRM Sync", "Sync qualified prospects directly into Salesforce contacts.", true),
                IntegrationItem("HubSpot", "CRM Sync", "Bi-directional sync with HubSpot deals and contact lists.", true),
                IntegrationItem("Zapier Automation", "Webhooks", "Trigger custom Zapier webhooks when leads hit Hot score 80+.", false),
                IntegrationItem("Google Workspace / Gmail", "Outreach", "Send personalized email sequences through your work Gmail.", true),
                IntegrationItem("LinkedIn Sales Navigator", "Discovery", "Export prospect profiles and send InMail connection notes.", false)
            )
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("External Integrations & Webhooks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Connect LeadForge AI to your CRM, email provider, and marketing stack.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        integrations.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(item.category, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (item.isConnected) {
                        OutlinedButton(
                            onClick = {
                                integrations = integrations.map { if (it.name == item.name) it.copy(isConnected = false) else it }
                                onShowToast("${item.name} disconnected")
                            },
                            modifier = Modifier.testTag("btn_toggle_${item.name}")
                        ) {
                            Text("Connected", color = Emerald400, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                integrations = integrations.map { if (it.name == item.name) it.copy(isConnected = true) else it }
                                onShowToast("${item.name} connected successfully!")
                            },
                            modifier = Modifier.testTag("btn_toggle_${item.name}")
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }
        }
    }
}
