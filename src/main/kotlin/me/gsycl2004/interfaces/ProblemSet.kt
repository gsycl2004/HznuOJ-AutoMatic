package me.gsycl2004.interfaces

import me.gsycl2004.interfaces.data.Problem

interface ProblemSet {
    val id:Int
    val name:String
    val start:String
    val end:String
    val status:String
    val type:String
    val problems:List<Problem>


}