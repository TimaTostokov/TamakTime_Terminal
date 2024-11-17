package com.pos_terminal.tamaktime_temirnal.presentation.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.Extensions
import com.pos_terminal.tamaktime_temirnal.databinding.ActivityMainBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardloading.CardFragmentLoading
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardloading.QrOrderFragmentLoading
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.dialoglogin.LoginDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        Extensions.loadLocale(this)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

       nfcAdapter = NfcAdapter.getDefaultAdapter(this)

       if (nfcAdapter == null) {
            Extensions.showToast(this, "NFC недоступно")
//            finish()
//            return
        }

        loadSavedLanguage()

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.category_fcv) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, _, _ ->
            invalidateOptionsMenu()
        }
        val appBarConfiguration: AppBarConfiguration = AppBarConfiguration(navController.graph)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        lifecycleScope.launch {
            viewModel.hasCredentials.collect {
                if (it) {
                } else {
                    showSignInDialog()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val intentFiltersArray = arrayOf<IntentFilter>()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
            Log.d("MainActivity", "Tag discovered: ${bytesToHex(tagId)}")
            val cardUuid = bytesToHex(tagId)
            handleCardUuid(cardUuid)
        }
    }

    private fun bytesToHex(bytes: ByteArray?): String {
        val sb = StringBuilder()
        bytes?.forEach { byte ->
            sb.append(String.format("%02X", byte))
        }
        return sb.toString()
    }

    private fun handleCardUuid(cardUuid: String) {
        Log.d(
            "MainActivity",
            "Handling NFC tag with UUID: $cardUuid"
        )
        saveCardUuidToSharedPreferences(cardUuid)
        passCardUuidToFragment(cardUuid)
    }

    private fun passCardUuidToFragment(cardUuid: String) {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.card_fcv) as? NavHostFragment

        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()

        if (currentFragment is CardFragmentLoading) {
            currentFragment.handleNfcTag(cardUuid)
        }

        if (currentFragment is QrOrderFragmentLoading) {
            currentFragment.handleQrTag(cardUuid)
        }

        navHostFragment?.childFragmentManager?.fragments?.forEach {
            if (it is CardFragmentLoading) {
                it.handleNfcTag(cardUuid)
            }
        }

        navHostFragment?.childFragmentManager?.fragments?.forEach {
            if (it is QrOrderFragmentLoading) {
                it.handleQrTag(cardUuid)
            }
        }
    }

    private fun saveCardUuidToSharedPreferences(cardUuid: String) {
        viewModel.setUuid(cardUuid)
        val sharedPreferences = getSharedPreferences("CardPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("card_uuid", cardUuid)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.category_fcv) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        val currentDestId = navController.currentDestination?.id
        if (currentDestId == R.id.cardFragmentAuthed) {
            menuInflater.inflate(R.menu.menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_exit -> {
                lifecycleScope.launch { viewModel.exit() }
            }

            R.id.ic_menu -> {
                alertDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.change_language_alert_diaolog, null)
        val buttonEn = view.findViewById<Button>(R.id.dialogButtonEn)
        val buttonRu = view.findViewById<Button>(R.id.dialogButtonRu)
        val buttonTur = view.findViewById<Button>(R.id.dialogButtonTur)
        builder.setView(view)
        buttonEn.setOnClickListener {
            builder.dismiss()
            updateLocale("en")
        }
        buttonRu.setOnClickListener {
            builder.dismiss()
            updateLocale("ru")
        }
        buttonTur.setOnClickListener {
            builder.dismiss()
            updateLocale("tr")
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun loadSavedLanguage() {
        val savedLanguage = viewModel.getSavedLanguage()
        if (resources.configuration.locales[0].language != savedLanguage) {
            updateLocale(savedLanguage)
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode.lowercase(Locale.ROOT))

        val config = resources.configuration.apply {
            setLocale(locale)
        }

        val intent = this.intent
        this.apply {
            viewModel.saveSelectedLanguage(languageCode)
            recreate()
        }
    }

    private fun showSignInDialog() {
        val fragmentManager = supportFragmentManager
        val newFragment = LoginDialogFragment()
        newFragment.isCancelable = false
        newFragment.show(fragmentManager, "sign in")
    }

}