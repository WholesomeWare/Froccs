package com.csakitheone.froccs.helper

import android.util.Log
import com.csakitheone.froccs.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting

class AI {
    companion object {

        private const val apiKey = BuildConfig.geminiApiKey
        private val model = GenerativeModel(
            modelName = "gemini-1.5-flash-8b",
            apiKey = apiKey,
            safetySettings = listOf(
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
            ),
        )


        suspend fun generateRatioName(vine: Float, soda: Float): String {
            // 126 tokens
            val prompt =
                "Találj ki egy kreatív nevet egy fröccsnek! Példák: 1-2 Hosszúlépés, 2-2 Háp-háp, 1-4 Sportfröccs, 3-2 Házmester, 1-9 Sóherfröccs vagy Távolugrás, 5-5 Maflás, 6-4 Polgármester. Az első szám a bor, a második a szóda. Az arány, amit el kell nevezned: $vine-$soda. A válaszod csak az ital neve legyen!"
            try {
                return model.generateContent(prompt).text
                    ?.replace(Regex("""\*"""), "")
                    ?.trim() ?: ""
            } catch (e: Exception) {
                Log.d("AI", e.message ?: "")
                return ""
            }
        }

    }
}