package com.bangkit.acnetect.model

import java.io.Serializable

class Article : Serializable {
    lateinit var nama: String
    lateinit var penyebab: String
    lateinit var solusi: String
    lateinit var penjelasan: String
    lateinit var image: String
}