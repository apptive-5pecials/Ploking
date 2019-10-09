/*
 * Created by Lee Oh Hyoung on 2019-10-09..
 */
package com.fivespecial.ploking.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// This is the base class for the Template Method Pattern.
abstract class BaseFragment : Fragment() {

    /**
     * @Description : Define resource Id to be used in layout inflate
     */
    abstract val resourceId: Int

    /**
     * @Description : init View Components by findViewById
     */
    abstract fun initComponent(view: View)

    /**
     * @Description : set Listener of view components
     * such as setOnClickListener, SetOnTouchListener, etc..
     */
    abstract fun setupListeners(view: View)

    /**
     * @Description : business logic and implementation of application
     */
    abstract fun setupImplementation()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(resourceId, container, false)

        initComponent(view = view)
        setupListeners(view = view)
        setupImplementation()

        return view
    }

}