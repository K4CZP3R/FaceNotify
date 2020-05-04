package xyz.k4czp3r.facenotify.user_interface

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.billingclient.api.*
import com.google.android.material.chip.Chip
import xyz.k4czp3r.facenotify.R

class FragmentAbout : Fragment(), PurchasesUpdatedListener {
    private val TAG = FragmentAbout::class.qualifiedName
    private lateinit var billingClient: BillingClient
    private val skuList = listOf("support_the_creator_cocacola", "support_the_creator_eat", "support_the_creator_change")

    private lateinit var chipCola : Chip
    private lateinit var chipEat: Chip
    private lateinit var chipChange: Chip


    companion object{
        fun newInstance(): FragmentAbout{
            return FragmentAbout()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setupBillingClient(context: Context){
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(p0: BillingResult?) {
                if(p0?.responseCode == BillingClient.BillingResponseCode.OK){
                    Log.i(TAG, "Setup billing ready")
                    loadAllSKUs()
                    //Setup ok
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "Failed")
            }
        })
    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    val billingFlowParams = BillingFlowParams
                        .newBuilder()
                        .setSkuDetails(skuDetails)
                    if(skuDetails.sku == "support_the_creator_cocacola"){
                        chipCola.text = "${skuDetails.price} ${skuDetails.priceCurrencyCode}";
                        chipCola.setOnClickListener {
                            billingClient.launchBillingFlow(activity, billingFlowParams.build())
                        }
                    }
                    else if(skuDetails.sku == "support_the_creator_eat"){
                        chipEat.text = "${skuDetails.price} ${skuDetails.priceCurrencyCode}";
                        chipEat.setOnClickListener{
                            billingClient.launchBillingFlow(activity, billingFlowParams.build())
                        }

                    }
                    else if(skuDetails.sku == "support_the_creator_change"){
                        chipChange.text = "${skuDetails.price} ${skuDetails.priceCurrencyCode}";

                        chipChange.setOnClickListener {
                                billingClient.launchBillingFlow(activity, billingFlowParams.build())
                            }
                    }
                    else{
                        Log.w(TAG, "Not implemented, sku=${skuDetails.sku}")
                    }
                }
            }
        }

    } else {
        println("Billing Client not ready")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chipChange = view.findViewById(R.id.chip_change)
        chipCola = view.findViewById(R.id.chip_cocacola)
        chipEat = view.findViewById(R.id.chip_eat)


        setupBillingClient(context!!)

    }
}