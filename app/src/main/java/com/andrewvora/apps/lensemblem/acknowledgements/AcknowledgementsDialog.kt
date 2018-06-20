package com.andrewvora.apps.lensemblem.acknowledgements

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrewvora.apps.lensemblem.R
import kotlinx.android.synthetic.main.dialog_acknowledgements.*

/**
 * Created on 4/22/2018.
 * @author Andrew Vorakrajangthiti
 */
class AcknowledgementsDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_acknowledgements, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        dialog_button1.setOnClickListener {
            dismiss()
        }

        acknowledgment_text_view.movementMethod = LinkMovementMethod.getInstance()
        acknowledgment_text_view.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.acknowledgements_body), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(getString(R.string.acknowledgements_body))
        }
    }
}