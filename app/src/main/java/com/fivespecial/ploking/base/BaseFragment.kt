/*
 * Created by Lee Oh Hyoung on 2019-10-09..
 */
package com.fivespecial.ploking.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract val resourceId: Int

    abstract fun initComponent(view: View)

    abstract fun setupImplementation()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(resourceId, container, false)

        initComponent(view = view)
        setupImplementation()

        return view
    }

}