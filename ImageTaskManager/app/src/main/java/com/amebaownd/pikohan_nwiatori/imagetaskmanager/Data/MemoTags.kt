package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import java.io.Serializable

@Entity(primaryKeys = ["memoId","tagId"],
    foreignKeys = [ForeignKey(entity = Memos::class
        ,parentColumns = ["memoId"],
        childColumns = ["memoId"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Tags::class,
        parentColumns = ["tagId"],
        childColumns = ["tagId"],
        onDelete = ForeignKey.CASCADE)])

data class MemoTags constructor(
    var memoId:Long=0,
    var tagId:Long=0
):Serializable