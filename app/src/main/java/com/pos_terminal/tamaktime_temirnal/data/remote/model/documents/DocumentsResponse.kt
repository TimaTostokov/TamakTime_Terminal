package com.pos_terminal.tamaktime_temirnal.data.remote.model.documents

import com.google.gson.annotations.SerializedName

data class DocumentResponse(
    val id: Long,
    @SerializedName("document_type")
    val documentType: Int,
    val date: String,
    val lines: List<LineResponse>
)

data class LineResponse(
    val product: Product,
    val quantity: String,
    val price: String,
    val total: String
)

data class Product(
    val id: Long,
    val canteen: Canteen,
    val title: String,
    val description: String,
    val category: Category,
    val thumbnail: String,
    val code: String,
    @SerializedName("buying_price")
    val buyingPrice: String,
    @SerializedName("selling_price")
    val sellingPrice: String,
    val available: Boolean,
    val count: Int,
    @SerializedName("issue_date")
    val issueDate: String,
    @SerializedName("expiration_date")
    val expirationDate: String
)

data class Canteen(
    val id: Long,
    val name: String,
    val avatar: String,
    val language: Language,
    val school: School
)

data class Category(
    val id: Long,
    val name: String,
    val icon: String,
    val color: String,
    val editable: Boolean
)

data class Language(val id: Long, val name: String)

data class School(
    val id: Long,
    val name: String,
    val address: Address,
    val phones: List<Phone>,
    val language: Language
)

data class Address(
    val id: Long,
    val district: District,
    val street: String,
    val number: String
)

data class District(val id: Long, val name: String, val city: City)

data class City(val id: Long, val name: String, val region: Region)

data class Region(val id: Long, val name: String, val country: Country)

data class Country(val id: Long, val name: String)

data class Phone(val id: Long, val number: String)
