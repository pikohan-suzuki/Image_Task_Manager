package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
data class Tags constructor(
    @PrimaryKey(autoGenerate = true)
    var tagId:Long=0,

    var name:String="",
    var color:Int=0
)