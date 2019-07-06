package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import java.io.Serializable

class MemosAndMemoTags{
    @Embedded
    lateinit var memos:Memos

    @Relation(parentColumn = "memoId",entityColumn = "memoId")
    lateinit var memoTags:List<MemoTags>

}