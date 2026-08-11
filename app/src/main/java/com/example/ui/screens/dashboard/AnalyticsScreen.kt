package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.vm.AnalyticsViewModel
import com.example.data.vm.DateRange
import com.example.ui.components.StatCard
import com.example.ui.theme.Emerald400
import com.example.ui.theme.HotScoreColor
import com.example.ui.theme.WarmScoreColor

@Composable
fun AnalyticsScreen(
    analyticsVm: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val summary by analyticsVm.analyticsSummary.collectAsState()
    val selectedRange by analyticsVm.selectedDateRange.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Pipeline Performance Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Real-time conversion metrics, funnel progression, and lead quality breakdown.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(14.dp))

        // Date Range Selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DateRangeChip("7 Days", selectedRange == DateRange.LAST_7_DAYS) { analyticsVm.setDateRange(DateRange.LAST_7_DAYS) }
            DateRangeChip("30 Days", selectedRange == DateRange.LAST_30_DAYS) { analyticsVm.setDateRange(DateRange.LAST_30_DAYS) }
            DateRangeChip("90 Days", selectedRange == DateRange.LAST_90_DAYS) { analyticsVm.setDateRange(DateRange.LAST_90_DAYS) }
            DateRangeChip("This Year", selectedRange == DateRange.THIS_YEAR) { analyticsVm.setDateRange(DateRange.THIS_YEAR) }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Funnel Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Lead Conversion Funnel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                summary.funnelStages.forEach { stage ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${stage.stage} (${stage.count})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text(String.format("%.1f%%", stage.percentage), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (stage.percentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lead Quality Distribution
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Score Tier Distribution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QualityDistributionBadge("Hot (80-100)", "${summary.hotLeadsCount}", HotScoreColor)
                    QualityDistributionBadge("Warm (60-79)", "${summary.warmLeadsCount}", WarmScoreColor)
                    QualityDistributionBadge("Cold (0-59)", "${summary.coldLeadsCount}", MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun DateRangeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.testTag("date_range_$text")
    )
}

@Composable
private fun QualityDistributionBadge(label: String, count: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
