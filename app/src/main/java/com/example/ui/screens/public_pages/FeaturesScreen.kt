package com.example.ui.screens.public_pages

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FeaturesScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Platform Features",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Full-suite AI lead discovery, qualification, and automated outreach.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureDetailCard(
            icon = Icons.Default.Search,
            title = "1. AI Lead Discovery",
            description = "Discover ideal prospects using search criteria: Industry, Location, Company size, Job title, Revenue range, and custom keywords."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureDetailCard(
            icon = Icons.Default.Psychology,
            title = "2. AI Lead Qualification",
            description = "Evaluate leads automatically with transparent scoring algorithms. Understand why each lead receives a Hot (80-100), Warm (60-79), or Cold (0-59) rating."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureDetailCard(
            icon = Icons.Default.Email,
            title = "3. AI Outreach Generator",
            description = "Generate high-converting cold emails, LinkedIn connection messages, and follow-up sequences in multiple tones (Professional, Friendly, Persuasive, Concise)."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureDetailCard(
            icon = Icons.Default.FilterList,
            title = "4. Lead Management Engine",
            description = "Organize workspace leads by Status (New, Contacted, Qualified, Meeting, Won, Lost), Priority, Source, and Campaign tags. Import and Export CSVs easily."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureDetailCard(
            icon = Icons.Default.Analytics,
            title = "5. Campaign & Analytics Suite",
            description = "Track conversion funnels, response rates, campaign performance comparison, and team lead metrics with date range filtering."
        )
    }
}

@Composable
private fun FeatureDetailCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
