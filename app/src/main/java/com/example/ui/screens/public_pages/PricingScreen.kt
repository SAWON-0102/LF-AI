package com.example.ui.screens.public_pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UpgradePlanModal
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400

@Composable
fun PricingScreen(
    onSelectPlan: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isYearly by remember { mutableStateOf(true) }
    var showCheckoutModal by remember { mutableStateOf(false) }
    var selectedPlanForModal by remember { mutableStateOf("Growth") }
    var selectedPriceForModal by remember { mutableStateOf("$79/mo") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Simple, Transparent Pricing",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Scale your pipeline with predictable plans. Upgrade or downgrade anytime.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Billing Toggle
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (!isYearly) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isYearly,
                    onCheckedChange = { isYearly = it },
                    modifier = Modifier.testTag("pricing_toggle_yearly")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Yearly",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isYearly) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = Emerald400.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Save 20%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald400,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pricing Cards
        val starterPrice = if (isYearly) "$23/mo" else "$29/mo"
        val growthPrice = if (isYearly) "$63/mo" else "$79/mo"
        val agencyPrice = if (isYearly) "$159/mo" else "$199/mo"

        // STARTER PLAN
        PricingCard(
            title = "Starter",
            price = starterPrice,
            billingPeriod = if (isYearly) "billed annually" else "billed monthly",
            description = "Essential lead discovery for freelancers and early-stage founders.",
            features = listOf(
                "100 leads/month",
                "Basic AI scoring",
                "Lead dashboard & management",
                "CSV export",
                "Basic analytics"
            ),
            isPopular = false,
            buttonText = "Start Starter Plan",
            onButtonClick = {
                selectedPlanForModal = "Starter"
                selectedPriceForModal = starterPrice
                showCheckoutModal = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GROWTH PLAN (MOST POPULAR)
        PricingCard(
            title = "Growth",
            price = growthPrice,
            billingPeriod = if (isYearly) "billed annually" else "billed monthly",
            description = "Powerful AI lead generation & outreach automation for growing sales teams.",
            features = listOf(
                "1,000 leads/month",
                "Advanced AI scoring & explanations",
                "AI Outreach Generator (Email & LinkedIn)",
                "Campaign management & tracking",
                "Advanced funnel analytics",
                "CRMs & Zapier integrations"
            ),
            isPopular = true,
            buttonText = "Start Growth Plan",
            onButtonClick = {
                selectedPlanForModal = "Growth"
                selectedPriceForModal = growthPrice
                showCheckoutModal = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // AGENCY PLAN
        PricingCard(
            title = "Agency",
            price = agencyPrice,
            billingPeriod = if (isYearly) "billed annually" else "billed monthly",
            description = "High-volume lead engine with white-label options for agencies & enterprises.",
            features = listOf(
                "5,000 leads/month",
                "Multiple active campaigns",
                "Advanced automation triggers",
                "Team member seats",
                "Priority 24/7 support",
                "White-label export option"
            ),
            isPopular = false,
            buttonText = "Start Agency Plan",
            onButtonClick = {
                selectedPlanForModal = "Agency"
                selectedPriceForModal = agencyPrice
                showCheckoutModal = true
            }
        )
    }

    UpgradePlanModal(
        show = showCheckoutModal,
        targetPlan = selectedPlanForModal,
        priceMonthly = selectedPriceForModal,
        onConfirmUpgrade = {
            onSelectPlan(selectedPlanForModal)
            showCheckoutModal = false
        },
        onDismiss = { showCheckoutModal = false }
    )
}

@Composable
private fun PricingCard(
    title: String,
    price: String,
    billingPeriod: String,
    description: String,
    features: List<String>,
    isPopular: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isPopular) 2.dp else 1.dp,
            if (isPopular) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isPopular) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Amber400, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MOST POPULAR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = price, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = billingPeriod, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(16.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = if (isPopular) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Text(text = buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
