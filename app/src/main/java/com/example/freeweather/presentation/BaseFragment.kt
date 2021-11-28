package com.example.freeweather.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

open class BaseFragment<T : ViewBinding> : Fragment() {
    protected var _binding: T? = null
    protected val binding get() = _binding!!

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return _binding?.root ?: throw RuntimeException("Don't call onCreateView() without first setting the binding")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}