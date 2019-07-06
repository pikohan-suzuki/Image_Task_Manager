package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

class MemosAndMemoImages {
    @Embedded
    lateinit var memos:Memos

    @Relation(parentColumn = "memoId",entityColumn = "memoId")
    lateinit var memoImages:List<MemoImages>
}