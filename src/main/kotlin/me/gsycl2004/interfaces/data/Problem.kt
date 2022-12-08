package me.gsycl2004.interfaces.data

interface Problem {
    val id:Int
    val name:String
    val author:String
    val src:String
    val ac:Int
    val sub:Int
    val score:Int

    fun getAccepted(page:Int): List<Accepted>

    fun getAllAccepted(): List<Accepted>

}