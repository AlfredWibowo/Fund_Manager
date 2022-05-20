package com.example.uas2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionCls (
    var IdTransaksi : String,
    var Tanggal : String,
    var Kategori : String,
    var Deskripsi : String,
    var Amount : String,
    var Tipe : String
) : Parcelable