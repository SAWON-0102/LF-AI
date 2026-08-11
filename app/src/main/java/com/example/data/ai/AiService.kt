package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiOutreachResult(
    val subject: String,
    val emailBody: String,
    val linkedinMessage: String,
    val followUpMessage: String,
    val isDemoMode: Boolean
)

data class AiLeadScoreExplanation(
    val score: Int,
    val rating: String, // "Hot", "Warm", "Cold"
    val explanation: String,
    val breakdownFactors: List<Pair<String, String>>,
    val isDemoMode: Boolean
)

data class DiscoveredLeadResult(
    val name: String,
    val email: String,
    val company: String,
    val jobTitle: String,
    val industry: String,
    val location: String,
    val score: Int,
    val reason: String
)

class AiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return key.isNotBlank() && !key.equals("MY_GEMINI_API_KEY", ignoreCase = true)
    }

    suspend fun generateOutreach(
        leadName: String,
        company: String,
        jobTitle: String,
        companyDesc: String,
        productService: String,
        valueProp: String,
        cta: String,
        tone: String
    ): AiOutreachResult = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext fallbackOutreach(leadName, company, jobTitle, valueProp, cta, tone)
        }

        try {
            val prompt = """
                You are an expert B2B copywriter for LeadForge AI.
                Write personalized sales outreach for:
                Target Name: $leadName
                Company: $company
                Job Title: $jobTitle
                Company Details: $companyDesc
                Our Product/Service: $productService
                Key Value Proposition: $valueProp
                Call to Action (CTA): $cta
                Tone: $tone

                Return ONLY a strict JSON object with this exact structure:
                {
                  "subject": "Email subject line",
                  "emailBody": "Full personalized email text",
                  "linkedinMessage": "Short punchy LinkedIn connection note (under 300 chars)",
                  "followUpMessage": "Brief follow-up email text (sent 3 days later)"
                }
            """.trimIndent()

            val jsonResponse = callGeminiRestApi(prompt)
            if (jsonResponse != null) {
                val cleanedText = extractJsonFromText(jsonResponse)
                val json = JSONObject(cleanedText)
                AiOutreachResult(
                    subject = json.optString("subject", "Connecting regarding $company's growth"),
                    emailBody = json.optString("emailBody", "Hi $leadName..."),
                    linkedinMessage = json.optString("linkedinMessage", "Hi $leadName, let's connect!"),
                    followUpMessage = json.optString("followUpMessage", "Hi $leadName, checking back..."),
                    isDemoMode = false
                )
            } else {
                fallbackOutreach(leadName, company, jobTitle, valueProp, cta, tone)
            }
        } catch (e: Exception) {
            fallbackOutreach(leadName, company, jobTitle, valueProp, cta, tone)
        }
    }

    suspend fun explainLeadScore(
        leadName: String,
        company: String,
        jobTitle: String,
        industry: String,
        location: String,
        score: Int
    ): AiLeadScoreExplanation = withContext(Dispatchers.IO) {
        val rating = when {
            score >= 80 -> "Hot"
            score >= 60 -> "Warm"
            else -> "Cold"
        }

        if (!isApiKeyConfigured()) {
            return@withContext fallbackScoreExplanation(leadName, company, jobTitle, industry, score, rating)
        }

        try {
            val prompt = """
                Analyze why this B2B lead received a LeadForge AI score of $score ($rating):
                Name: $leadName
                Company: $company
                Title: $jobTitle
                Industry: $industry
                Location: $location

                Provide a 2-sentence executive summary and 3 key matching factors.
                Return ONLY JSON with this format:
                {
                  "explanation": "Executive summary paragraph...",
                  "factors": [
                    {"factor": "Title Match", "detail": "Decision-maker level title"},
                    {"factor": "Industry Alignment", "detail": "High growth B2B segment"},
                    {"factor": "Buying Intent", "detail": "Active technology evaluation signal"}
                  ]
                }
            """.trimIndent()

            val jsonText = callGeminiRestApi(prompt)
            if (jsonText != null) {
                val json = JSONObject(extractJsonFromText(jsonText))
                val exp = json.optString("explanation", "High overall match based on industry and role.")
                val factorsArray = json.optJSONArray("factors")
                val factors = mutableListOf<Pair<String, String>>()
                if (factorsArray != null) {
                    for (i in 0 until factorsArray.length()) {
                        val obj = factorsArray.getJSONObject(i)
                        factors.add(obj.optString("factor") to obj.optString("detail"))
                    }
                }
                AiLeadScoreExplanation(score, rating, exp, factors, isDemoMode = false)
            } else {
                fallbackScoreExplanation(leadName, company, jobTitle, industry, score, rating)
            }
        } catch (e: Exception) {
            fallbackScoreExplanation(leadName, company, jobTitle, industry, score, rating)
        }
    }

    suspend fun findLeadsWithAi(
        industry: String,
        location: String,
        companySize: String,
        jobTitle: String,
        revenueRange: String,
        keywords: String,
        count: Int
    ): List<DiscoveredLeadResult> = withContext(Dispatchers.IO) {
        if (!isApiKeyConfigured()) {
            return@withContext fallbackLeadSearch(industry, location, companySize, jobTitle, count)
        }

        try {
            val prompt = """
                Generate $count realistic B2B target lead profiles matching criteria:
                Industry: $industry
                Location: $location
                Company Size: $companySize
                Target Job Title: $jobTitle
                Revenue: $revenueRange
                Keywords: $keywords

                Return ONLY a JSON array of objects:
                [
                  {
                    "name": "Full Name",
                    "email": "email@company.com",
                    "company": "Company Name",
                    "jobTitle": "Job Title",
                    "industry": "Industry",
                    "location": "City, Country",
                    "score": 88,
                    "reason": "Why this prospect fits the criteria"
                  }
                ]
            """.trimIndent()

            val text = callGeminiRestApi(prompt)
            if (text != null) {
                val clean = extractJsonFromText(text)
                val array = JSONArray(clean)
                val list = mutableListOf<DiscoveredLeadResult>()
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    list.add(
                        DiscoveredLeadResult(
                            name = item.optString("name", "Target Executive"),
                            email = item.optString("email", "lead@company.com"),
                            company = item.optString("company", "Tech Enterprise"),
                            jobTitle = item.optString("jobTitle", jobTitle.ifBlank { "VP of Growth" }),
                            industry = item.optString("industry", industry.ifBlank { "SaaS" }),
                            location = item.optString("location", location.ifBlank { "San Francisco, CA" }),
                            score = item.optInt("score", 85),
                            reason = item.optString("reason", "High alignment with search persona")
                        )
                    )
                }
                list
            } else {
                fallbackLeadSearch(industry, location, companySize, jobTitle, count)
            }
        } catch (e: Exception) {
            fallbackLeadSearch(industry, location, companySize, jobTitle, count)
        }
    }

    private fun callGeminiRestApi(prompt: String): String? {
        val key = try { BuildConfig.GEMINI_API_KEY } catch (e: Throwable) { "" }
        if (key.isBlank()) return null

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$key"

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val bodyStr = response.body?.string() ?: return null
        if (!response.isSuccessful) return null

        val resJson = JSONObject(bodyStr)
        val candidates = resJson.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        return parts.getJSONObject(0).optString("text")
    }

    private fun extractJsonFromText(text: String): String {
        var clean = text.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json").trim()
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }
        return clean
    }

    // --- Deterministic Fallbacks (Demo Mode) ---

    private fun fallbackOutreach(
        leadName: String,
        company: String,
        jobTitle: String,
        valueProp: String,
        cta: String,
        tone: String
    ): AiOutreachResult {
        val isPersuasive = tone.equals("Persuasive", true)
        val isFriendly = tone.equals("Friendly", true)
        val isConcise = tone.equals("Concise", true)

        val subject = when {
            isConcise -> "Quick question for $leadName"
            isFriendly -> "Excited to connect, $leadName!"
            isPersuasive -> "Transforming $company's lead pipeline this quarter"
            else -> "Unlocking high-intent leads for $company"
        }

        val greeting = if (isFriendly) "Hey $leadName," else "Dear $leadName,"
        val body = """
            $greeting

            I noticed your role as $jobTitle at $company. Many growth teams in your sector struggle with low-intent lead lists and wasted outreach.

            ${valueProp.ifBlank { "LeadForge AI combines predictive lead scoring with automated discovery to increase qualified meeting conversion rates by 3x." }}

            ${cta.ifBlank { "Are you open to a brief 5-minute preview this Thursday?" }}

            Best regards,
            LeadForge AI Growth Team
        """.trimIndent()

        val linkedin = "Hi $leadName, loved your recent updates at $company! Would love to connect and share how B2B leaders are automating lead qualification."

        val followUp = "Hi $leadName, following up on my note below regarding $company. Happy to share a quick 2-minute video preview if that's easier?"

        return AiOutreachResult(subject, body, linkedin, followUp, isDemoMode = true)
    }

    private fun fallbackScoreExplanation(
        leadName: String,
        company: String,
        jobTitle: String,
        industry: String,
        score: Int,
        rating: String
    ): AiLeadScoreExplanation {
        val explanation = "$leadName at $company received a $score/100 ($rating) lead score. The high rating is driven by senior $jobTitle authority, high company intent in $industry, and strong ICP profile completeness."

        val factors = listOf(
            "Title Authority" to "$jobTitle represents direct decision-making power.",
            "Industry Synergy" to "$industry is currently experiencing high demand for AI automation.",
            "Profile Completeness" to "Verified contact information and corporate domain match.",
            "Intent Signals" to "Company matches target revenue and headcount filters."
        )

        return AiLeadScoreExplanation(score, rating, explanation, factors, isDemoMode = true)
    }

    private fun fallbackLeadSearch(
        industry: String,
        location: String,
        companySize: String,
        jobTitle: String,
        count: Int
    ): List<DiscoveredLeadResult> {
        val targetInd = industry.ifBlank { "SaaS" }
        val targetLoc = location.ifBlank { "San Francisco, CA" }
        val targetTitle = jobTitle.ifBlank { "VP of Sales" }

        val samples = listOf(
            DiscoveredLeadResult("Aria Montgomery", "aria@apexsoftware.io", "Apex Software", targetTitle, targetInd, targetLoc, 94, "C-Suite buyer with active lead generation expansion."),
            DiscoveredLeadResult("Julian Vance", "julian@vanguardtech.co", "Vanguard Tech", "Head of Growth", targetInd, "Austin, TX", 89, "Growing 35% YoY with 50+ sales reps."),
            DiscoveredLeadResult("Sophia Reyes", "sophia@luminarysolutions.com", "Luminary Solutions", "Chief Marketing Officer", targetInd, targetLoc, 91, "Active buyer signal in marketing technology."),
            DiscoveredLeadResult("Ethan Hawke", "e.hawke@hyperiondigital.net", "Hyperion Digital", "Director of Business Dev", targetInd, "New York, NY", 83, "Strong ICP alignment and verified contact info."),
            DiscoveredLeadResult("Nora Sterling", "nora@zenithconsulting.org", "Zenith Consulting", "Managing Partner", targetInd, "Chicago, IL", 79, "Mid-size consulting firm expanding outbound team.")
        )

        return samples.take(count.coerceIn(1, 10))
    }
}
