package xyz.k4czp3r.facenotify.user_interface

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.k4czp3r.facenotify.KspBroadcastService
import xyz.k4czp3r.facenotify.KspFaceDetection
import xyz.k4czp3r.facenotify.R
import xyz.k4czp3r.facenotify.helpers.PrefsKeys
import xyz.k4czp3r.facenotify.helpers.SharedPrefs
import xyz.k4czp3r.facenotify.models.NotificationTypes


class FragmentSummary : Fragment(){
    private val sharedPrefs = SharedPrefs()
    private lateinit var switchService: Switch
    private val updateDataHandler = Handler()
    private var updateDataInTheBackground = true

    companion object{
        fun newInstance(): FragmentSummary{
            return FragmentSummary()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchService = view.findViewById(R.id.f_summary_switchService)
        switchService.setOnCheckedChangeListener { compoundButton, b ->
            switchServiceChecked(compoundButton.context, b)

        }
        updateData()
        updateDataRunnable.run()
    }

    override fun onStop() {
        super.onStop()
        updateDataInTheBackground = false
    }

    override fun onResume() {
        super.onResume()
        updateDataInTheBackground = true
        updateDataRunnable.run()
    }

    private fun switchServiceChecked(context: Context, newState: Boolean)
    {
        if(!sharedPrefs.getBoolean(PrefsKeys.PERMISSIONS_GRANTED)){
            if(newState) showAlert(context, activity!!.getString(R.string.missing_permissions_title), getString(R.string.missing_permissions_content))
            return
        }
        if(KspBroadcastService.state() == newState){
            Toast.makeText(context, activity!!.getString(R.string.service_status_not_changed), Toast.LENGTH_SHORT).show()
        }
        else{
            if(newState) {
                KspBroadcastService.start(context)
                KspFaceDetection().prepareSecureSettingsFor(NotificationTypes[sharedPrefs.getInt(PrefsKeys.SELECTED_NOTIFICATION_MODE)])
            }
            else {
                KspBroadcastService.stop(context)
                KspFaceDetection().restoreDefaultNotificationSettings()
            }
            Toast.makeText(context, activity!!.getString(R.string.service_status_changed), Toast.LENGTH_SHORT).show()
            changeServiceStateFront(newState)
        }

    }

    private fun showAlert(context: Context, title: String, content: String){
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(content)
            .setNeutralButton(activity!!.getString(R.string.OK)) { _: DialogInterface, _: Int -> }
            .show()
    }
    private fun changeServiceStateFront(newState: Boolean)
    {
        switchService.isChecked = newState
        if(newState) switchService.text = activity?.getString(R.string.service_switch_on)
        else switchService.text = activity?.getString(R.string.service_switch_off)
    }
    private fun updateData(){
        changeServiceStateFront(KspBroadcastService.state())
    }

    private var updateDataRunnable: Runnable = object : Runnable {
        override fun run() {
            updateData()
            if(updateDataInTheBackground) updateDataHandler.postDelayed(this, 1000)
        }

    }
}