package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.net.Uri

@Entity(primaryKeys = ["memoId", "order"],
    foreignKeys = [ForeignKey(entity = Memos::class
        ,parentColumns = ["memoId"],
        childColumns = ["memoId"],
        onDelete = ForeignKey.CASCADE)])
data class MemoImages constructor(
    var memoId:Long=0,
    var order:Int=0,
    var uri: String=""
)