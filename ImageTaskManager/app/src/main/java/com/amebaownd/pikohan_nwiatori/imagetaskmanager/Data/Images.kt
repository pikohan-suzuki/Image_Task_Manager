package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Images constructor(
    @PrimaryKey(autoGenerate = true)
    val imageId:Long,
    val src:Int
)
