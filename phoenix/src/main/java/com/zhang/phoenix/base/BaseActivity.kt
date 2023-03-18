package com.zhang.phoenix.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)
}