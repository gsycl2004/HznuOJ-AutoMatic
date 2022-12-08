package me.gsycl2004.interfaces

import me.gsycl2004.LangEnum
import me.gsycl2004.interfaces.data.Problem
import me.gsycl2004.interfaces.data.Solution

interface LoginClient : Client {
    fun submit(id: Int, code: String, langType: LangEnum)

    fun submit(cid:Int,id:Int,code:String,langType: LangEnum)

    fun getMySolution(id: Int): List<Solution>

    fun getMySolution(id:Int,pid: Int): List<Solution>


    fun getProblemSet(page:Int):List<ProblemSet>




    fun getMySolutionByProblem(problem: Problem) = getMySolution(problem.id)

    fun submitByProblem(problem: Problem, code: String, langType: LangEnum) = submit(problem.id, code, langType)

}