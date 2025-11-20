package com.ccino.demo.compose.sdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ccino.demo.dialog.BaseDialogFragment


@Composable
fun DialogCase(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        var showDialog by remember { mutableStateOf(false) }
        var nativeDialog by remember { mutableStateOf(false) }

        Button(onClick = { showDialog = true }) {
            Text("compose dialog")
        }

        Button(onClick = { nativeDialog = true }) {
            Text("native dialog")
        }

        if (showDialog) {
            CustomDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {},
                title = "From Composable",
                message = "This is a custom dialog from a Composable."
            )
        }
        if (nativeDialog) {
            val activity = LocalActivity.current as? AppCompatActivity
            activity?.supportFragmentManager?.let { fm ->
                if (fm.findFragmentByTag("NativeDialog") == null) {
                    NativeDialog().show(fm, "NativeDialog")
                }
            }
            nativeDialog = false
        }
    }
}

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: String = "Title",
    message: String = "Message"
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dismiss")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onConfirmation()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


class NativeDialog : BaseDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CustomDialog(
                    onDismissRequest = { dismiss() },
                    onConfirmation = {},
                    title = "Native",
                    message = "This is a custom dialog from a native."
                )
            }
        }
    }

    override fun bindView(view: View) {
    }

    override fun getLayoutRes(): Int {
        return 0
    }

}