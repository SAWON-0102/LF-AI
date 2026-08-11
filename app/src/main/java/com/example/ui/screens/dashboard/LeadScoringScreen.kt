package com.example.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.LeadScoreBadge

@Composable
fun LeadScoringScreen(
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var titleWeight by remember { mutableFloatStateOf(40f) }
    var industryWeight by remember { mutableFloatStateOf(30f) }
    var companySizeWeight by remember { mutableFloatStateOf(20f) }
    var locationWeight by remember { mutableFloatStateOf(10f) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("AI Scoring Configurator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Fine-tune weight parameters to customize lead scoring for your ideal ICP.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                WeightSlider("Job Title Seniority Weight", titleWeight, { titleWeight = it }, "title_weight_slider")
                Spacer(modifier = Modifier.height(12.dp))
                WeightSlider("Industry Synergy Weight", industryWeight, { industryWeight = it }, "industry_weight_slider")
                Spacer(modifier = Modifier.height(12.dp))
                WeightSlider("Company Headcount Weight", companySizeWeight, { companySizeWeight = it }, "company_size_slider")
                Spacer(modifier = Modifier.height(12.dp))
                WeightSlider("Geographic Location Weight", locationWeight, { locationWeight = it }, "location_weight_slider")

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onShowToast("AI Lead Scoring Weights Updated!") },
                    modifier = Modifier.fillMaxWidth().testTag("btn_save_scoring_weights")
                ) {
                    Text("Save Scoring Rules", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Simulation Sandbox Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Scoring Rules Sandbox Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val simScore = ((titleWeight * 0.9f) + (industryWeight * 0.85f) + (companySizeWeight * 0.8f) + (locationWeight * 0.7f)).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sample Prospect: CTO at B2B SaaS Enterprise", style = MaterialTheme.typography.bodySmall)
                    LeadScoreBadge(score = simScore)
                }
            }
        }
    }
}

@Composable
private fun WeightSlider(label: String, value: Float, onValueChange: (Float) -> Unit, tag: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("${value.toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            modifier = Modifier.testTag(tag)
        )
    }
}
