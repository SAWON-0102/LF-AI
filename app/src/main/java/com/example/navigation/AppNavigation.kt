package com.example.navigation

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.vm.AiViewModel
import com.example.data.vm.AnalyticsViewModel
import com.example.data.vm.CampaignViewModel
import com.example.data.vm.LeadViewModel
import com.example.data.vm.MainViewModel
import com.example.ui.screens.dashboard.AiLeadFinderScreen
import com.example.ui.screens.dashboard.AnalyticsScreen
import com.example.ui.screens.dashboard.CampaignsScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.IntegrationsScreen
import com.example.ui.screens.dashboard.LeadDetailScreen
import com.example.ui.screens.dashboard.LeadScoringScreen
import com.example.ui.screens.dashboard.LeadsListScreen
import com.example.ui.screens.dashboard.SettingsAndProfileScreen
import com.example.ui.screens.public_pages.AboutScreen
import com.example.ui.screens.public_pages.ContactScreen
import com.example.ui.screens.public_pages.FeaturesScreen
import com.example.ui.screens.public_pages.ForgotPasswordScreen
import com.example.ui.screens.public_pages.HomeScreen
import com.example.ui.screens.public_pages.HowItWorksScreen
import com.example.ui.screens.public_pages.LoginScreen
import com.example.ui.screens.public_pages.PricingScreen
import com.example.ui.screens.public_pages.PrivacyPolicyScreen
import com.example.ui.screens.public_pages.SignUpScreen
import com.example.ui.screens.public_pages.TermsScreen
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import kotlinx.coroutines.launch

object Routes {
    // Public
    const val HOME = "home"
    const val FEATURES = "features"
    const val HOW_IT_WORKS = "how_it_works"
    const val PRICING = "pricing"
    const val ABOUT = "about"
    const val CONTACT = "contact"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PRIVACY = "privacy"
    const val TERMS = "terms"

    // Dashboard
    const val DASHBOARD = "dashboard"
    const val LEADS = "leads"
    const val LEAD_DETAIL = "lead_detail/{leadId}"
    const val AI_FINDER = "ai_finder"
    const val LEAD_SCORING = "lead_scoring"
    const val CAMPAIGNS = "campaigns"
    const val ANALYTICS = "analytics"
    const val INTEGRATIONS = "integrations"
    const val SETTINGS = "settings"

    fun leadDetailRoute(id: String) = "lead_detail/$id"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadForgeMainApp(
    mainVm: MainViewModel = viewModel(),
    leadVm: LeadViewModel = viewModel(),
    campaignVm: CampaignViewModel = viewModel(),
    aiVm: AiViewModel = viewModel(),
    analyticsVm: AnalyticsViewModel = viewModel()
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    val userMessage by mainVm.userMessage.collectAsState()
    val session by mainVm.userSession.collectAsState()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            mainVm.clearToast()
        }
    }

    val isDashboardRoute = currentRoute in listOf(
        Routes.DASHBOARD, Routes.LEADS, Routes.AI_FINDER,
        Routes.CAMPAIGNS, Routes.ANALYTICS, Routes.SETTINGS,
        Routes.LEAD_SCORING, Routes.INTEGRATIONS
    ) || currentRoute.startsWith("lead_detail")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LeadForge AI", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Public Pages", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 20.dp))
                NavigationDrawerItem(
                    label = { Text("Home Landing") },
                    selected = currentRoute == Routes.HOME,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.HOME) }
                )
                NavigationDrawerItem(
                    label = { Text("Features Overview") },
                    selected = currentRoute == Routes.FEATURES,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.FEATURES) }
                )
                NavigationDrawerItem(
                    label = { Text("How It Works") },
                    selected = currentRoute == Routes.HOW_IT_WORKS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.HOW_IT_WORKS) }
                )
                NavigationDrawerItem(
                    label = { Text("Pricing Plans") },
                    selected = currentRoute == Routes.PRICING,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.PRICING) }
                )
                NavigationDrawerItem(
                    label = { Text("About Company") },
                    selected = currentRoute == Routes.ABOUT,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.ABOUT) }
                )
                NavigationDrawerItem(
                    label = { Text("Contact Sales") },
                    selected = currentRoute == Routes.CONTACT,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.CONTACT) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("App Workspace", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 20.dp))
                NavigationDrawerItem(
                    label = { Text("Dashboard") },
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.DASHBOARD) }
                )
                NavigationDrawerItem(
                    label = { Text("Leads Repository") },
                    selected = currentRoute == Routes.LEADS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.LEADS) }
                )
                NavigationDrawerItem(
                    label = { Text("AI Lead Finder") },
                    selected = currentRoute == Routes.AI_FINDER,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.AI_FINDER) }
                )
                NavigationDrawerItem(
                    label = { Text("Outreach Campaigns") },
                    selected = currentRoute == Routes.CAMPAIGNS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.CAMPAIGNS) }
                )
                NavigationDrawerItem(
                    label = { Text("Funnel Analytics") },
                    selected = currentRoute == Routes.ANALYTICS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.ANALYTICS) }
                )
                NavigationDrawerItem(
                    label = { Text("AI Scoring Rules") },
                    selected = currentRoute == Routes.LEAD_SCORING,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.LEAD_SCORING) }
                )
                NavigationDrawerItem(
                    label = { Text("Integrations & Webhooks") },
                    selected = currentRoute == Routes.INTEGRATIONS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.INTEGRATIONS) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings & Profile") },
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Routes.SETTINGS) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { navController.navigate(Routes.HOME) }
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LeadForge AI", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("app_bar_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Switch between Public Site & App Workspace
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable {
                                    if (isDashboardRoute) {
                                        navController.navigate(Routes.HOME)
                                    } else {
                                        navController.navigate(Routes.DASHBOARD)
                                    }
                                }
                                .testTag("toggle_workspace_mode")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDashboardRoute) "Website" else "App SaaS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (isDashboardRoute) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == Routes.DASHBOARD,
                            onClick = { navController.navigate(Routes.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard") },
                            modifier = Modifier.testTag("nav_tab_dashboard")
                        )
                        NavigationBarItem(
                            selected = currentRoute == Routes.LEADS || currentRoute.startsWith("lead_detail"),
                            onClick = { navController.navigate(Routes.LEADS) },
                            icon = { Icon(Icons.Default.Group, contentDescription = "Leads") },
                            label = { Text("Leads") },
                            modifier = Modifier.testTag("nav_tab_leads")
                        )
                        NavigationBarItem(
                            selected = currentRoute == Routes.AI_FINDER,
                            onClick = { navController.navigate(Routes.AI_FINDER) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Finder") },
                            label = { Text("AI Finder") },
                            modifier = Modifier.testTag("nav_tab_ai_finder")
                        )
                        NavigationBarItem(
                            selected = currentRoute == Routes.CAMPAIGNS,
                            onClick = { navController.navigate(Routes.CAMPAIGNS) },
                            icon = { Icon(Icons.Default.Campaign, contentDescription = "Campaigns") },
                            label = { Text("Campaigns") },
                            modifier = Modifier.testTag("nav_tab_campaigns")
                        )
                        NavigationBarItem(
                            selected = currentRoute == Routes.ANALYTICS,
                            onClick = { navController.navigate(Routes.ANALYTICS) },
                            icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                            label = { Text("Analytics") },
                            modifier = Modifier.testTag("nav_tab_analytics")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                // Public Pages
                composable(Routes.HOME) {
                    HomeScreen(
                        onStartGeneratingLeads = { navController.navigate(Routes.DASHBOARD) },
                        onSeeHowItWorks = { navController.navigate(Routes.HOW_IT_WORKS) },
                        onViewPricing = { navController.navigate(Routes.PRICING) }
                    )
                }
                composable(Routes.FEATURES) { FeaturesScreen() }
                composable(Routes.HOW_IT_WORKS) {
                    HowItWorksScreen(onStartGeneratingLeads = { navController.navigate(Routes.DASHBOARD) })
                }
                composable(Routes.PRICING) {
                    PricingScreen(onSelectPlan = { plan ->
                        mainVm.upgradePlan(plan)
                        navController.navigate(Routes.DASHBOARD)
                    })
                }
                composable(Routes.ABOUT) { AboutScreen() }
                composable(Routes.CONTACT) {
                    ContactScreen(onSubmitMessage = { msg -> mainVm.showToast(msg) })
                }
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = { email, name ->
                            mainVm.login(email, name)
                            navController.navigate(Routes.DASHBOARD)
                        },
                        onNavigateSignUp = { navController.navigate(Routes.SIGN_UP) },
                        onNavigateForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
                    )
                }
                composable(Routes.SIGN_UP) {
                    SignUpScreen(
                        onSignUpSuccess = { email, name ->
                            mainVm.login(email, name)
                            navController.navigate(Routes.DASHBOARD)
                        },
                        onNavigateLogin = { navController.navigate(Routes.LOGIN) }
                    )
                }
                composable(Routes.FORGOT_PASSWORD) {
                    ForgotPasswordScreen(
                        onSendReset = { email -> mainVm.showToast("Reset link sent to $email") },
                        onNavigateLogin = { navController.navigate(Routes.LOGIN) }
                    )
                }
                composable(Routes.PRIVACY) { PrivacyPolicyScreen() }
                composable(Routes.TERMS) { TermsScreen() }

                // Workspace Pages
                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        mainVm = mainVm,
                        leadVm = leadVm,
                        onNavigateLeads = { navController.navigate(Routes.LEADS) },
                        onNavigateLeadDetail = { id -> navController.navigate(Routes.leadDetailRoute(id)) },
                        onNavigateAiFinder = { navController.navigate(Routes.AI_FINDER) },
                        onNavigateCampaigns = { navController.navigate(Routes.CAMPAIGNS) },
                        onNavigateAnalytics = { navController.navigate(Routes.ANALYTICS) },
                        onNavigateSettings = { navController.navigate(Routes.SETTINGS) }
                    )
                }
                composable(Routes.LEADS) {
                    LeadsListScreen(
                        leadVm = leadVm,
                        onNavigateLeadDetail = { id -> navController.navigate(Routes.leadDetailRoute(id)) },
                        onShowToast = { msg -> mainVm.showToast(msg) }
                    )
                }
                composable(
                    route = Routes.LEAD_DETAIL,
                    arguments = listOf(navArgument("leadId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val leadId = backStackEntry.arguments?.getString("leadId") ?: ""
                    LeadDetailScreen(
                        leadId = leadId,
                        leadVm = leadVm,
                        aiVm = aiVm,
                        onBack = { navController.popBackStack() },
                        onShowToast = { msg -> mainVm.showToast(msg) }
                    )
                }
                composable(Routes.AI_FINDER) {
                    AiLeadFinderScreen(
                        aiVm = aiVm,
                        onNavigateSettings = { navController.navigate(Routes.SETTINGS) },
                        onShowToast = { msg -> mainVm.showToast(msg) }
                    )
                }
                composable(Routes.CAMPAIGNS) {
                    CampaignsScreen(
                        campaignVm = campaignVm,
                        onShowToast = { msg -> mainVm.showToast(msg) }
                    )
                }
                composable(Routes.ANALYTICS) {
                    AnalyticsScreen(analyticsVm = analyticsVm)
                }
                composable(Routes.LEAD_SCORING) {
                    LeadScoringScreen(onShowToast = { msg -> mainVm.showToast(msg) })
                }
                composable(Routes.INTEGRATIONS) {
                    IntegrationsScreen(onShowToast = { msg -> mainVm.showToast(msg) })
                }
                composable(Routes.SETTINGS) {
                    SettingsAndProfileScreen(
                        mainVm = mainVm,
                        onShowToast = { msg -> mainVm.showToast(msg) }
                    )
                }
            }
        }
    }
}
