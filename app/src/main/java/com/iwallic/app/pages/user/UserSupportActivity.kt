package com.iwallic.app.pages.user

import android.os.Bundle
import android.widget.LinearLayout
import com.iwallic.app.R
import com.iwallic.app.base.BaseActivity

class UserSupportActivity : BaseActivity() {
    private lateinit var backLL: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_support)

        initDOM()
        initClick()
    }

    private fun initDOM() {
        backLL = findViewById(R.id.user_support_back)
    }

    private fun initClick() {
        backLL.setOnClickListener {
            finish()
        }
    }
}