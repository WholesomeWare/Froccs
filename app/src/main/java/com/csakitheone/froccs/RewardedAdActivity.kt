package com.csakitheone.froccs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewarded_ad)

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-5995992409743558/5083548598", adRequest, object: RewardedAdLoadCallback() {
            override fun onAdLoaded(p0: RewardedAd) {
                p0.show(this@RewardedAdActivity) {
                    Toast.makeText(applicationContext, "Köszönöm! Egészségedre!", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
            override fun onAdFailedToLoad(p0: LoadAdError) {
                finish()
            }
        })
    }
}