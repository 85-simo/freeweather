package com.example.freeweather.presentation.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freeweather.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimpleDialogFragment : DialogFragment() {
    private val args: SimpleDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
        .setTitle(args.titleResId)
        .setMessage(args.contentResId)
        .setPositiveButton(R.string.ok, null)
        .setCancelable(false)
        .create()
}