package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
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
import com.example.data.local.LeadEntity
import com.example.data.vm.LeadViewModel
import com.example.data.vm.SortOption
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LeadScoreBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge

@Composable
fun LeadsListScreen(
    leadVm: LeadViewModel,
    onNavigateLeadDetail: (String) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leads by leadVm.filteredLeads.collectAsState()
    val searchQuery by leadVm.searchQuery.collectAsState()
    val selectedStatus by leadVm.selectedStatus.collectAsState()
    val selectedPriority by leadVm.selectedPriority.collectAsState()
    val selectedLeadIds by leadVm.selectedLeadIds.collectAsState()
    val currentSort by leadVm.sortOption.collectAsState()

    var showAddModal by remember { mutableStateOf(false) }
    var showBulkStatusDialog by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val statusOptions = listOf("All", "New", "Contacted", "Qualified", "Meeting", "Won", "Lost")
    val priorityOptions = listOf("All", "High", "Medium", "Low")

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Lead Repository", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${leads.size} prospects matching criteria", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row {
                    IconButton(onClick = { showSortMenu = true }, modifier = Modifier.testTag("sort_leads_button")) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("AI Score (High to Low)") },
                            onClick = { leadVm.setSortOption(SortOption.SCORE_DESC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("AI Score (Low to High)") },
                            onClick = { leadVm.setSortOption(SortOption.SCORE_ASC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Name (A to Z)") },
                            onClick = { leadVm.setSortOption(SortOption.NAME_ASC); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Recently Created") },
                            onClick = { leadVm.setSortOption(SortOption.DATE_DESC); showSortMenu = false }
                        )
                    }

                    IconButton(
                        onClick = {
                            val csv = leadVm.exportToCsvString()
                            onShowToast("CSV exported! (${leads.size} leads formatted)")
                        },
                        modifier = Modifier.testTag("export_csv_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export CSV")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { leadVm.setSearchQuery(it) },
                placeholder = { Text("Search by name, company, job title, email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { leadVm.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("lead_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips - Status
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusOptions) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { leadVm.setStatusFilter(status) },
                        label = { Text(status) },
                        modifier = Modifier.testTag("status_filter_$status")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bulk Actions Bar (Shown when items are selected)
            AnimatedVisibility(visible = selectedLeadIds.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${selectedLeadIds.size} selected",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { showBulkStatusDialog = true }) {
                                Text("Update Status")
                            }
                            IconButton(onClick = { showBulkDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // Leads List
            if (leads.isEmpty()) {
                EmptyStateView(
                    title = "No Prospects Found",
                    description = "No leads match your current search or filter rules. Try resetting your filter or use the AI Lead Finder.",
                    buttonText = "+ Add New Lead",
                    onButtonClick = { showAddModal = true },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(leads, key = { it.id }) { lead ->
                        LeadRowCard(
                            lead = lead,
                            isSelected = selectedLeadIds.contains(lead.id),
                            onSelectToggle = { leadVm.toggleLeadSelection(lead.id) },
                            onClick = { onNavigateLeadDetail(lead.id) }
                        )
                    }
                }
            }
        }

        // FAB to Add Lead
        FloatingActionButton(
            onClick = { showAddModal = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_add_lead"),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Lead")
        }
    }

    // Add Lead Modal
    if (showAddModal) {
        AddLeadDialog(
            onDismiss = { showAddModal = false },
            onSave = { name, email, company, title, ind, loc, web, score, stat, prio, notes ->
                leadVm.addLead(name, email, company, title, ind, loc, web, score, stat, prio, notes)
                onShowToast("New lead $name added successfully!")
                showAddModal = false
            }
        )
    }

    // Bulk Status Dialog
    if (showBulkStatusDialog) {
        var newStatusChoice by remember { mutableStateOf("Qualified") }
        AlertDialog(
            onDismissRequest = { showBulkStatusDialog = false },
            title = { Text("Bulk Update Status (${selectedLeadIds.size} leads)") },
            text = {
                Column {
                    listOf("New", "Contacted", "Qualified", "Meeting", "Won", "Lost").forEach { st ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { newStatusChoice = st }
                                .padding(vertical = 6.dp)
                        ) {
                            Checkbox(checked = newStatusChoice == st, onCheckedChange = { newStatusChoice = st })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(st, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    leadVm.bulkUpdateStatus(newStatusChoice)
                    onShowToast("Updated ${selectedLeadIds.size} leads to status: $newStatusChoice")
                    showBulkStatusDialog = false
                }) {
                    Text("Apply Bulk Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkStatusDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Bulk Delete Dialog
    ConfirmationDialog(
        show = showBulkDeleteDialog,
        title = "Delete ${selectedLeadIds.size} Selected Leads?",
        message = "Are you sure you want to delete these leads? This action cannot be undone.",
        confirmText = "Delete Leads",
        isDestructive = true,
        onConfirm = {
            leadVm.bulkDeleteSelected()
            onShowToast("Selected leads deleted.")
            showBulkDeleteDialog = false
        },
        onDismiss = { showBulkDeleteDialog = false }
    )
}

@Composable
private fun LeadRowCard(
    lead: LeadEntity,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectToggle() },
                modifier = Modifier.testTag("lead_checkbox_${lead.id}")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(lead.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    PriorityBadge(priority = lead.priority)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("${lead.jobTitle} at ${lead.company}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${lead.industry} • ${lead.location}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }

            Column(horizontalAlignment = Alignment.End) {
                LeadScoreBadge(score = lead.score)
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = lead.status)
            }
        }
    }
}

@Composable
private fun AddLeadDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, Int, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("SaaS") }
    var location by remember { mutableStateOf("San Francisco, CA") }
    var website by remember { mutableStateOf("") }
    var scoreStr by remember { mutableStateOf("80") }
    var status by remember { mutableStateOf("New") }
    var priority by remember { mutableStateOf("High") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("+ Add New Lead", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("add_lead_name"))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address *") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("add_lead_email"))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("add_lead_company"))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Job Title") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("add_lead_title"))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = industry, onValueChange = { industry = it }, label = { Text("Industry") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = scoreStr, onValueChange = { scoreStr = it }, label = { Text("Score (0-100)") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && company.isNotBlank()) {
                        val scoreInt = scoreStr.toIntOrNull() ?: 75
                        onSave(name, email, company, title, industry, location, website, scoreInt, status, priority, notes)
                    }
                },
                modifier = Modifier.testTag("add_lead_save_button")
            ) {
                Text("Save Prospect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
