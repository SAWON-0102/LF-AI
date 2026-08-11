package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        LeadEntity::class,
        CampaignEntity::class,
        CampaignLeadCrossRef::class,
        ActivityEntity::class,
        OutreachTemplateEntity::class,
        WorkspaceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LeadForgeDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao
    abstract fun campaignDao(): CampaignDao
    abstract fun activityDao(): ActivityDao
    abstract fun outreachTemplateDao(): OutreachTemplateDao
    abstract fun workspaceDao(): WorkspaceDao

    companion object {
        @Volatile
        private var INSTANCE: LeadForgeDatabase? = null

        fun getDatabase(context: Context): LeadForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeadForgeDatabase::class.java,
                    "leadforge_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialDemoData(database)
                }
            }
        }
    }
}

suspend fun populateInitialDemoData(db: LeadForgeDatabase) {
    val workspaceId = "ws-demo-1"

    val defaultWorkspace = WorkspaceEntity(
        id = workspaceId,
        userId = "usr-leadforge-1",
        userName = "Alex Vance",
        userEmail = "alex.vance@growthforge.io",
        companyName = "GrowthForge Inc.",
        industry = "SaaS",
        companySize = "11-50 employees",
        targetMarket = "B2B Tech & Agencies",
        targetLocation = "North America & Europe",
        idealCustomer = "VPs of Sales & Marketing Directors in B2B SaaS",
        targetJobTitles = "VP Sales, Head of Growth, CMO, Founder",
        monthlyTarget = 50,
        plan = "Growth",
        isOnboarded = true
    )
    db.workspaceDao().insertWorkspace(defaultWorkspace)

    // 15+ Demo Leads with realistic details & varied statuses/scores/industries
    val demoLeads = listOf(
        LeadEntity(
            id = "lead-1",
            workspaceId = workspaceId,
            name = "Sarah Chen",
            email = "sarah.chen@techflow.io",
            company = "TechFlow Solutions",
            jobTitle = "Chief Technology Officer",
            industry = "SaaS",
            location = "San Francisco, CA",
            website = "https://techflow.io",
            score = 92,
            status = "Qualified",
            priority = "High",
            source = "AI Search",
            notes = "High intent: looking for automated lead qualification & CRM integration."
        ),
        LeadEntity(
            id = "lead-2",
            workspaceId = workspaceId,
            name = "Marcus Thorne",
            email = "m.thorne@growthscale.agency",
            company = "GrowthScale Digital",
            jobTitle = "VP of Business Development",
            industry = "Agencies",
            location = "Austin, TX",
            website = "https://growthscale.agency",
            score = 88,
            status = "Contacted",
            priority = "High",
            source = "AI Search",
            notes = "Managing 25 client accounts. Wants white-label capabilities."
        ),
        LeadEntity(
            id = "lead-3",
            workspaceId = workspaceId,
            name = "Elena Rostova",
            email = "elena@nexusconsulting.com",
            company = "Nexus Strategy Group",
            jobTitle = "Managing Partner",
            industry = "Consulting",
            location = "Chicago, IL",
            website = "https://nexusconsulting.com",
            score = 95,
            status = "Meeting",
            priority = "High",
            source = "LinkedIn",
            notes = "Demo scheduled for Thursday 2 PM EST. Interested in Agency tier."
        ),
        LeadEntity(
            id = "lead-4",
            workspaceId = workspaceId,
            name = "David Sterling",
            email = "david@apexcommerce.co",
            company = "Apex E-Commerce",
            jobTitle = "Head of Marketing",
            industry = "E-commerce",
            location = "New York, NY",
            website = "https://apexcommerce.co",
            score = 74,
            status = "New",
            priority = "Medium",
            source = "Inbound",
            notes = "Downloaded whitepaper on AI Prospecting."
        ),
        LeadEntity(
            id = "lead-5",
            workspaceId = workspaceId,
            name = "Victoria Blake",
            email = "vblake@blakerealty.com",
            company = "Blake Commercial Realty",
            jobTitle = "Principal Broker",
            industry = "Real Estate",
            location = "Miami, FL",
            website = "https://blakerealty.com",
            score = 65,
            status = "Contacted",
            priority = "Medium",
            source = "CSV Import",
            notes = "Sent introductory email sequence."
        ),
        LeadEntity(
            id = "lead-6",
            workspaceId = workspaceId,
            name = "Jonathan Hayes",
            email = "jhayes@cloudworks.net",
            company = "CloudWorks Systems",
            jobTitle = "Director of Sales",
            industry = "SaaS",
            location = "Seattle, WA",
            website = "https://cloudworks.net",
            score = 91,
            status = "Won",
            priority = "High",
            source = "AI Search",
            notes = "Signed Growth Annual plan! Onboarding in progress."
        ),
        LeadEntity(
            id = "lead-7",
            workspaceId = workspaceId,
            name = "Priya Patel",
            email = "priya@lumina-analytics.io",
            company = "Lumina Analytics",
            jobTitle = "VP Growth & Demand Gen",
            industry = "SaaS",
            location = "Boston, MA",
            website = "https://lumina-analytics.io",
            score = 82,
            status = "Qualified",
            priority = "High",
            source = "AI Search",
            notes = "Replaced legacy lead scraper with LeadForge AI trial."
        ),
        LeadEntity(
            id = "lead-8",
            workspaceId = workspaceId,
            name = "Michael O'Connor",
            email = "m.oconnor@vertexmedia.co",
            company = "Vertex Growth Media",
            jobTitle = "Founder & CEO",
            industry = "Agencies",
            location = "Denver, CO",
            website = "https://vertexmedia.co",
            score = 79,
            status = "Meeting",
            priority = "Medium",
            source = "LinkedIn",
            notes = "Evaluating LeadForge AI vs Outreach.io."
        ),
        LeadEntity(
            id = "lead-9",
            workspaceId = workspaceId,
            name = "Camila Rodriguez",
            email = "camila@finnovate.tech",
            company = "Finnovate Labs",
            jobTitle = "Chief Marketing Officer",
            industry = "Professional Services",
            location = "Atlanta, GA",
            website = "https://finnovate.tech",
            score = 58,
            status = "New",
            priority = "Low",
            source = "Inbound",
            notes = "Cold prospect with modest budget."
        ),
        LeadEntity(
            id = "lead-10",
            workspaceId = workspaceId,
            name = "Arthur Pendelton",
            email = "apendelton@heritagecap.com",
            company = "Heritage Capital Partners",
            jobTitle = "Managing Director",
            industry = "Consulting",
            location = "London, UK",
            website = "https://heritagecap.com",
            score = 42,
            status = "Lost",
            priority = "Low",
            source = "Manual",
            notes = "Currently tied to 3-year contract with Salesforce."
        ),
        LeadEntity(
            id = "lead-11",
            workspaceId = workspaceId,
            name = "Samantha Wright",
            email = "swright@pulsehealth.app",
            company = "Pulse HealthTech",
            jobTitle = "Head of Customer Acquisition",
            industry = "SaaS",
            location = "Toronto, ON",
            website = "https://pulsehealth.app",
            score = 86,
            status = "Qualified",
            priority = "High",
            source = "AI Search",
            notes = "Wants automated personalized email drafts for HIPAA-compliant B2B pitches."
        ),
        LeadEntity(
            id = "lead-12",
            workspaceId = workspaceId,
            name = "Liam Gallagher",
            email = "liam@beaconadvertising.com",
            company = "Beacon Ad Group",
            jobTitle = "Sales Operations Lead",
            industry = "Agencies",
            location = "Dublin, IE",
            website = "https://beaconadvertising.com",
            score = 71,
            status = "Contacted",
            priority = "Medium",
            source = "CSV Import",
            notes = "Opened outreach email twice."
        ),
        LeadEntity(
            id = "lead-13",
            workspaceId = workspaceId,
            name = "Hannah Zimmerman",
            email = "hannah@solarpowerx.com",
            company = "SolarPowerX Solutions",
            jobTitle = "VP Commercial Development",
            industry = "Professional Services",
            location = "Phoenix, AZ",
            website = "https://solarpowerx.com",
            score = 84,
            status = "Meeting",
            priority = "High",
            source = "AI Search",
            notes = "Seeking commercial real estate leads."
        ),
        LeadEntity(
            id = "lead-14",
            workspaceId = workspaceId,
            name = "Derek Vance",
            email = "derek@quantumlogistics.io",
            company = "Quantum Logistics",
            jobTitle = "Chief Revenue Officer",
            industry = "SaaS",
            location = "Chicago, IL",
            website = "https://quantumlogistics.io",
            score = 69,
            status = "New",
            priority = "Medium",
            source = "AI Search",
            notes = "High revenue prospect, evaluating enterprise capabilities."
        ),
        LeadEntity(
            id = "lead-15",
            workspaceId = workspaceId,
            name = "Chloe Nguyen",
            email = "chloe@artisanretail.co",
            company = "Artisan Retail Group",
            jobTitle = "Director of E-Commerce",
            industry = "E-commerce",
            location = "Los Angeles, CA",
            website = "https://artisanretail.co",
            score = 77,
            status = "Contacted",
            priority = "Medium",
            source = "Inbound",
            notes = "Received product walkthrough."
        )
    )
    db.leadDao().insertLeads(demoLeads)

    // 5 Demo Campaigns
    val demoCampaigns = listOf(
        CampaignEntity(
            id = "camp-1",
            workspaceId = workspaceId,
            name = "Q3 SaaS Founders Outreach",
            description = "Targeting B2B SaaS Founders & CTOs with AI-driven lead scoring value proposition.",
            targetAudience = "CTO, Founder, VP Sales in B2B SaaS (10-200 employees)",
            leadCount = 120,
            sentCount = 98,
            openRate = 58.4f,
            replyRate = 22.1f,
            status = "Active"
        ),
        CampaignEntity(
            id = "camp-2",
            workspaceId = workspaceId,
            name = "Growth Agencies Scaler",
            description = "White-label & multi-workspace pitch for marketing & demand gen agencies.",
            targetAudience = "Agency Owners, Business Development Directors",
            leadCount = 85,
            sentCount = 85,
            openRate = 62.0f,
            replyRate = 28.5f,
            status = "Active"
        ),
        CampaignEntity(
            id = "camp-3",
            workspaceId = workspaceId,
            name = "Enterprise Consulting Nurture",
            description = "Long-form strategic consultation outreach targeting senior partners.",
            targetAudience = "Managing Partners in Tier-1 Consulting firms",
            leadCount = 45,
            sentCount = 20,
            openRate = 45.0f,
            replyRate = 12.0f,
            status = "Paused"
        ),
        CampaignEntity(
            id = "camp-4",
            workspaceId = workspaceId,
            name = "E-Commerce Head of Growth Cold Blitz",
            description = "Fast, punchy email templates highlighting quick customer acquisition ROI.",
            targetAudience = "Heads of Growth & Marketing in E-Commerce brands",
            leadCount = 200,
            sentCount = 0,
            openRate = 0f,
            replyRate = 0f,
            status = "Draft"
        ),
        CampaignEntity(
            id = "camp-5",
            workspaceId = workspaceId,
            name = "Real Estate Brokers Expansion",
            description = "Localized commercial real estate high-intent outreach.",
            targetAudience = "Principal Brokers & Commercial Realtors",
            leadCount = 60,
            sentCount = 60,
            openRate = 51.2f,
            replyRate = 18.0f,
            status = "Completed"
        )
    )
    db.campaignDao().insertCampaigns(demoCampaigns)

    // Campaign Lead cross references
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-1", "lead-1"))
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-1", "lead-6"))
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-1", "lead-7"))
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-2", "lead-2"))
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-2", "lead-8"))
    db.campaignDao().insertCampaignLeadRef(CampaignLeadCrossRef("camp-3", "lead-3"))

    // Sample Activities
    val demoActivities = listOf(
        ActivityEntity(
            id = "act-1",
            leadId = "lead-1",
            type = "Score Evaluated",
            description = "AI Score updated to 92 (Hot Lead - Strong Industry & Title Match).",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        ActivityEntity(
            id = "act-2",
            leadId = "lead-1",
            type = "Outreach Generated",
            description = "Generated 'Persuasive' email & LinkedIn follow-up message.",
            createdAt = System.currentTimeMillis() - 86400000L * 1
        ),
        ActivityEntity(
            id = "act-3",
            leadId = "lead-3",
            type = "Status Changed",
            description = "Status updated from 'Qualified' to 'Meeting'. Demo scheduled.",
            createdAt = System.currentTimeMillis() - 3600000L * 5
        )
    )
    db.activityDao().insertActivities(demoActivities)

    // Sample Outreach Templates
    val demoTemplates = listOf(
        OutreachTemplateEntity(
            id = "tpl-1",
            workspaceId = workspaceId,
            name = "High-Intent SaaS Pitch",
            subject = "Quick question regarding {{company}}'s lead pipeline",
            body = "Hi {{name}},\n\nI noticed {{company}} is scaling rapidly in {{industry}}. Many CTOs and Growth Leads spend hours manually vetting leads. LeadForge AI uses automated scoring to instantly flag 80+ hot leads.\n\nWould you be open to a 5-minute video preview this week?",
            linkedinMessage = "Hi {{name}}, impressive work at {{company}}. Would love to connect and share how we're automating lead qualification for tech teams.",
            followUp = "Hi {{name}}, just following up on my note below. Happy to send a custom 2-min demo recording tailored to {{company}}."
        ),
        OutreachTemplateEntity(
            id = "tpl-2",
            workspaceId = workspaceId,
            name = "Agency Partnership Special",
            subject = "White-label lead gen engine for {{company}}",
            body = "Hi {{name}},\n\nAs {{jobTitle}} at {{company}}, you know how crucial client acquisition is. LeadForge AI gives agencies a complete branded workspace to discover, qualify, and deliver high-scoring leads automatically.\n\nLet's connect for a brief walkthrough.",
            linkedinMessage = "Hey {{name}} - saw your client case studies at {{company}}. We help agencies automate lead discovery.",
            followUp = "Hi {{name}}, following up regarding agency white-label options."
        )
    )
    db.outreachTemplateDao().insertTemplates(demoTemplates)
}
