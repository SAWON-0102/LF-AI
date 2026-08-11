package com.example.ui.screens.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ActivityEntity
import com.example.data.local.LeadEntity
import com.example.data.vm.AiViewModel
import com.example.data.vm.LeadViewModel
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.LeadScoreBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge

@Composable
fun LeadDetailScreen(
    leadId: String,
    leadVm: LeadViewModel,
    aiVm: AiViewModel,
    onBack: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leadState = leadVm.getLeadById(leadId).collectAsState(initial = null)
    val lead = leadState.value

    val activitiesState = leadVm.getActivitiesForLead(leadId).collectAsState(initial = emptyList())
    val activities = activitiesState.value

    val isExplaining by aiVm.isExplainingScore.collectAsState()
    val scoreExplanation by aiVm.scoreExplanation.collectAsState()

    val isGeneratingOutreach by aiVm.isGeneratingOutreach.collectAsState()
    val outreachResult by aiVm.outreachResult.collectAsState()

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }

    var selectedTone by remember { mutableStateOf("Persuasive") }
    var customValueProp by remember { mutableStateOf("LeadForge AI delivers 3x qualified leads through automated scoring.") }
    var customCta by remember { mutableStateOf("Are you open to a 5-minute preview this Thursday?") }

    val scrollState = rememberScrollState()

    if (lead == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentLead: LeadEntity = lead

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("lead_detail_back_button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Prospect Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("lead_detail_delete_button")) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lead Hero Card
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
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentLead.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${currentLead.jobTitle} • ${currentLead.company}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }

                    LeadScoreBadge(score = currentLead.score)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Dropdown Button
                    Box {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("lead_status_picker")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(status = currentLead.status)
                                Spacer(modifier = Modifier.width(6.dp))
                                TextButton(onClick = { showStatusDropdown = true }) {
                                    Text("Change", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showStatusDropdown,
                            onDismissRequest = { showStatusDropdown = false }
                        ) {
                            listOf("New", "Contacted", "Qualified", "Meeting", "Won", "Lost").forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        leadVm.updateLead(currentLead.copy(status = st))
                                        onShowToast("Status changed to $st")
                                        showStatusDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    PriorityBadge(priority = currentLead.priority)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Attributes Grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailRow(Icons.Default.Email, currentLead.email)
                    DetailRow(Icons.Default.LocationOn, "${currentLead.location} (${currentLead.industry})")
                    DetailRow(Icons.Default.Language, currentLead.website)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- AI LEAD SCORE EXPLAINER CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Score Intelligence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            aiVm.explainLeadScore(currentLead.name, currentLead.company, currentLead.jobTitle, currentLead.industry, currentLead.location, currentLead.score)
                        },
                        enabled = !isExplaining,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_explain_score")
                    ) {
                        if (isExplaining) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Analyze Score")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (scoreExplanation != null) {
                    Text(
                        text = scoreExplanation!!.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Matching Rating Factors:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    scoreExplanation!!.breakdownFactors.forEach { factor ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Row(modifier = Modifier.padding(8.dp)) {
                                Text("• ${factor.first}: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Text(factor.second, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Click 'Analyze Score' to run Gemini AI score evaluation on this prospect's company size, title level, and buying signals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- AI OUTREACH GENERATOR CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Outreach Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tone selector
                Text("Select Copy Tone:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    listOf("Persuasive", "Professional", "Friendly", "Concise").forEach { tone ->
                        FilterChip(
                            selected = selectedTone == tone,
                            onClick = { selectedTone = tone },
                            label = { Text(tone, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("tone_chip_$tone")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        aiVm.generateOutreach(
                            leadName = currentLead.name,
                            company = currentLead.company,
                            jobTitle = currentLead.jobTitle,
                            companyDesc = "${currentLead.industry} in ${currentLead.location}",
                            productService = "LeadForge AI B2B Lead Engine",
                            valueProp = customValueProp,
                            cta = customCta,
                            tone = selectedTone
                        )
                    },
                    enabled = !isGeneratingOutreach,
                    modifier = Modifier.fillMaxWidth().testTag("btn_generate_outreach"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGeneratingOutreach) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Email & LinkedIn Drafts")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Generated Output
                if (outreachResult != null) {
                    val result = outreachResult!!

                    // Email Subject
                    OutreachBox(
                        title = "Email Subject",
                        content = result.subject,
                        onCopy = {
                            copyToClipboard(context, "Email Subject", result.subject)
                            onShowToast("Subject copied to clipboard!")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Body
                    OutreachBox(
                        title = "Cold Email Body",
                        content = result.emailBody,
                        onCopy = {
                            copyToClipboard(context, "Email Body", result.emailBody)
                            onShowToast("Email body copied!")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // LinkedIn Message
                    OutreachBox(
                        title = "LinkedIn Connection Note",
                        content = result.linkedinMessage,
                        onCopy = {
                            copyToClipboard(context, "LinkedIn Note", result.linkedinMessage)
                            onShowToast("LinkedIn note copied!")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            aiVm.saveCurrentOutreachAsTemplate("${currentLead.company} $selectedTone Pitch")
                            onShowToast("Saved outreach draft to Templates!")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_template"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save as Outreach Template")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- ACTIVITY & NOTES HISTORY CARD ---
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
                    Text("Activity & Notes History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { showAddNoteDialog = true }, modifier = Modifier.testTag("btn_add_note")) {
                        Icon(Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Note")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (currentLead.notes.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Notes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Text(currentLead.notes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                activities.forEach { act ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(act.type, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(act.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    // Add Note Modal
    if (showAddNoteDialog) {
        var noteInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Add Note for ${currentLead.name}") },
            text = {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Note content") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("note_input_field")
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (noteInput.isNotBlank()) {
                        leadVm.addNote(currentLead.id, noteInput)
                        onShowToast("Note added!")
                        showAddNoteDialog = false
                    }
                }) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation
    ConfirmationDialog(
        show = showDeleteDialog,
        title = "Delete ${currentLead.name}?",
        message = "This lead record will be permanently deleted from LeadForge database.",
        confirmText = "Delete Prospect",
        isDestructive = true,
        onConfirm = {
            leadVm.deleteLead(currentLead.id)
            onShowToast("${currentLead.name} deleted.")
            onBack()
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun OutreachBox(title: String, content: String, onCopy: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(content, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
