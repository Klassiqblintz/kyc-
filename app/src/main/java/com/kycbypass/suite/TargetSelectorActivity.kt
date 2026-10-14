package com.kycbypass.suite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kycbypass.suite.databinding.ActivityTargetSelectorBinding
import com.kycbypass.suite.utils.AppMonitor

class TargetSelectorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTargetSelectorBinding
    private val targetApps = listOf(
        TargetApp("Bank of America", "com.bankofamerica.bankmobile", "banking"),
        TargetApp("Chase", "com.chase.sig.android", "banking"),
        TargetApp("Wells Fargo", "com.wellsfargo.mobile", "banking"),
        TargetApp("Cash App", "com.cashapp.cashapp", "crypto"),
        TargetApp("Binance", "com.binance.dev", "crypto"),
        TargetApp("Coinbase", "com.coinbase.android", "crypto"),
        TargetApp("PayPal", "com.paypal.android.p2pmobile", "fintech"),
        TargetApp("Venmo", "com.venmo", "fintech"),
        TargetApp("Robinhood", "com.robinhood.release", "fintech"),
        TargetApp("Tinder", "com.tinder", "dating"),
        TargetApp("Bumble", "com.bumble.app", "dating")
    )
    
    private var selectedTarget: TargetApp? = null
    private val monitor = AppMonitor(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTargetSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupControls()
    }
    
    private fun setupRecyclerView() {
        val adapter = TargetAppAdapter(targetApps) { target ->
            selectedTarget = target
            binding.tvSelectedTarget.text = "Target: ${target.name}"
            finish()
        }
        binding.recyclerTargets.apply {
            layoutManager = LinearLayoutManager(this@TargetSelectorActivity)
            adapter = adapter
        }
    }
    
    private fun setupControls() {
        binding.btnBack.setOnClickListener { finish() }
    }
    
    data class TargetApp(val name: String, val packageName: String, val category: String)
}

class TargetAppAdapter(
    private var targets: List<TargetSelectorActivity.TargetApp>,
    private val onItemClick: (TargetSelectorActivity.TargetApp) -> Unit
) : RecyclerView.Adapter<TargetAppAdapter.ViewHolder>() {
    
    class ViewHolder(val binding: com.kycbypass.suite.databinding.ItemTargetBinding) : RecyclerView.ViewHolder(binding.root)
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding = com.kycbypass.suite.databinding.ItemTargetBinding.inflate(
            android.view.LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val target = targets[position]
        holder.binding.tvTargetName.text = target.name
        holder.binding.tvTargetCategory.text = target.category
        holder.itemView.setOnClickListener { onItemClick(target) }
    }
    
    override fun getItemCount() = targets.size
    fun updateTargets(newTargets: List<TargetSelectorActivity.TargetApp>) { targets = newTargets; notifyDataSetChanged() }
}
