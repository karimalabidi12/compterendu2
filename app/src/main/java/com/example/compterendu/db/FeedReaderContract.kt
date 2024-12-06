package com.example.compterendu.db
import android.provider.BaseColumns

object FeedReaderContract {
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "Product"
        const val COLUMN_NAME_PRICE = "price"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_IMAGE = "imageRes"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FeedEntry.COLUMN_NAME_PRICE} REAL," +
                "${FeedEntry.COLUMN_NAME_NAME} TEXT," +
                "${FeedEntry.COLUMN_NAME_DESCRIPTION} TEXT," +
                "${FeedEntry.COLUMN_NAME_IMAGE} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"
}