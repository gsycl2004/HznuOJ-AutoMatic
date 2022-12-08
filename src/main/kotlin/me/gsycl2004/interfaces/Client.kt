package me.gsycl2004.interfaces

import me.gsycl2004.interfaces.data.Accepted
import me.gsycl2004.interfaces.data.Problem

interface Client {
    fun login(id:String,password:String): LoginClient

    fun getProblemsByPage(page:Int):List<Problem>

    fun getAllProblems():List<Problem>

    fun getProblemAccepted(id:Int,page:Int):List<Accepted>

    fun getAllProblemAccepted(id:Int):List<Accepted>


}