package com.dadm.reto_8.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dadm.reto_8.data.model.Company

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "company_db"
        private const val DATABASE_VERSION = 1

        const val TABLE_COMPANIES = "companies"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_URL = "url"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_SERVICES = "services"
        const val COLUMN_CLASSIFICATION = "classification"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_COMPANY_TABLE = """
            CREATE TABLE $TABLE_COMPANIES (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_URL TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_SERVICES TEXT,
                $COLUMN_CLASSIFICATION TEXT
            )
        """
        db.execSQL(CREATE_COMPANY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMPANIES")
        onCreate(db)
    }

    fun insertCompany(company: Company): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, company.name)
        values.put(COLUMN_URL, company.url)
        values.put(COLUMN_PHONE, company.phone)
        values.put(COLUMN_EMAIL, company.email)
        values.put(COLUMN_SERVICES, company.services)
        values.put(COLUMN_CLASSIFICATION, company.classification)

        return db.insert(TABLE_COMPANIES, null, values)
    }

    fun getAllCompanies(): List<Company> {
        val companies = mutableListOf<Company>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COMPANIES", null)

        if (cursor.moveToFirst()) {
            do {
                val company = Company(
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    url = cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                    phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)),
                    email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    services = cursor.getString(cursor.getColumnIndex(COLUMN_SERVICES)),
                    classification = cursor.getString(cursor.getColumnIndex(COLUMN_CLASSIFICATION))
                )
                companies.add(company)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return companies
    }

    fun clearDatabase() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_COMPANIES")
        db.close()
    }

    fun updateCompany(company: Company) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", company.name)
            put("classification", company.classification)
            put("email", company.email)
            put("phone", company.phone)
            put("services", company.services)
            put("url", company.url)
        }
        db.update("companies", contentValues, "name = ?", arrayOf(company.name))
    }

    fun deleteCompany(company: Company) {
        val db = this.writableDatabase
        db.delete("companies", "name = ?", arrayOf(company.name))
    }
}