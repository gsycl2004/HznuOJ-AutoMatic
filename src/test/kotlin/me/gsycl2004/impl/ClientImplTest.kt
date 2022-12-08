package me.gsycl2004.impl

import kotlinx.coroutines.delay
import me.gsycl2004.ClientFactory
import me.gsycl2004.LangEnum
import me.gsycl2004.interfaces.Client
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception


fun main() {
    val client = ClientFactory.newLoginClient("2022210401097", "Hznu2022097")
    val problems = client.getAllProblems()
    problems.forEach { it ->
        val result = client.getMySolution(it.id)
        if (result.isNotEmpty()) {
            result.filter { it.result == "Accepted" }.forEach { solutions ->
                val hdb = ClientFactory.newLoginClient("2022210405045", "Hznu2022045")
                hdb.submit(
                    solutions.problemId,
                    solutions.code.colorCode().replace("\n"," ").colorCode().also(System.out::println),
                    if (solutions.langType.contains("++")) LangEnum.CPP11 else LangEnum.C11
                )
                Thread.sleep(5000)
            }
        }
    }

    //val contest = client.getProblemSet(0).forEach { set ->
    //    try {
    //        println(set.name)
    //        set.problems.forEach { problem ->
    //            client.getMySolution(set.id, problem.id).filter { it.result == "Accepted" }.forEach {
    //                println(problem.name)
    //                val hdb = ClientFactory.newLoginClient("2022210405045", "Hznu2022045")
    //                hdb.submit(set.id,it.problemId,it.code.colorCode(),if (it.langType.contains("++")) LangEnum.CPP11 else LangEnum.C11)
    //                Thread.sleep(5000)
    //            }
    //        }
//
    //    }catch (ex:Exception){
    //        ex.printStackTrace()
    //    }
    //}
}


fun String.colorCode(): String {
    val splited = this.replace("\\/\\/.*? ".toRegex(), " ").split("(?<=[;{}>]) ".toRegex())
    val token = splited.toSet()
    val texts = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val textsB = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val m = token.filter { !it.startsWith("#") }
        .associateWith { "${textsB.random()}${texts.random()}${texts.random()}${texts.random()}" }
    val builder = StringBuilder()
    builder.apply {
        splited.filter { it.startsWith("#") }.forEach {
            appendLine(it)
        }
        m.forEach { t, u ->
            appendLine("#define $u $t")
        }
        splited.filter { !it.startsWith("#") }.forEach {
            appendLine(m[it])
        }
    }
    return builder.toString()
}