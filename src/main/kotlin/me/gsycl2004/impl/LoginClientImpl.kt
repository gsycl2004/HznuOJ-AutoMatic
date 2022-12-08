package me.gsycl2004.impl

import me.gsycl2004.LangEnum
import me.gsycl2004.interfaces.Client
import me.gsycl2004.interfaces.LoginClient
import me.gsycl2004.interfaces.ProblemSet
import me.gsycl2004.interfaces.data.Accepted
import me.gsycl2004.interfaces.data.Problem
import me.gsycl2004.interfaces.data.Solution
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.nodes.Document
import java.util.*

class LoginClientImpl(
    private val client: ClientImpl,
    private val account: String,
    private val password: String
) : LoginClient, Client by client {
    private val csrf by lazy {
        return@lazy fetch(url("https://acm.hznu.edu.cn/OJ/loginpage.php")).getElementsByAttributeValue(
            "name",
            "csrf_token"
        ).`val`()
    }

    private fun genCsrf(id:Int): String {
        return fetch(url("https://acm.hznu.edu.cn/OJ/submitpage.php?id=${id}")).getElementsByAttributeValue("name","csrf_token").`val`()
    }

    private fun genContestCsrf(cid:Int,pid:Int): String {
        return fetch(url("https://acm.hznu.edu.cn/OJ/submitpage.php?cid=${cid}&pid=${pid}")).getElementsByAttributeValue("name","csrf_token").`val`()
    }


    init {
        val result = fetch {
            url("https://acm.hznu.edu.cn/OJ/login.php")
            header("Referer", "https://acm.hznu.edu.cn/OJ/")
            post(FormBody.Builder().apply {
                add("csrf_token", csrf)
                add("user_id", account)
                add("password", password)
                add("contest_id", "")
                add("submit", "Login")
            }.build())
        }
        if (result.toString().contains("alert")) println(result)
    }


    private fun fetch(req: Request) = client.fetch(req)

    private fun url(url: String) = client.url(url)

    private fun fetch(func: Request.Builder.() -> Unit): Document {
        return client.fetch(Request.Builder().apply(func).build())
    }

    private fun getCode(problem_id: Int, ssid: Long): String {
        return fetch(url("https://acm.hznu.edu.cn/OJ/submitpage.php?id=${problem_id}&sid=${ssid}"))
            .getElementById("editor")!!
            .text()
    }


    override fun submit(id: Int, code: String,langType:LangEnum) {
        fetch {
            url("https://acm.hznu.edu.cn/OJ/submit.php")

            header("Referer", "https://acm.hznu.edu.cn/OJ/submitpage.php?id=${id}")
            post(FormBody.Builder().apply {
                add("csrf_token", genCsrf(id))
                add("language", langType.value.toString())
                add("theme","xcode")
                add("source", Base64.getEncoder().encodeToString(code.toByteArray()) + "HZNU")
                add("id", id.toString())
            }.build())
        }
    }

    override fun submit(cid: Int, id: Int, code: String, langType: LangEnum) {
        fetch {
            url("https://acm.hznu.edu.cn/OJ/submit.php")

            header("Referer", "https://acm.hznu.edu.cn/OJ/submitpage.php?id=${id}")
            post(FormBody.Builder().apply {
                add("csrf_token", genContestCsrf(cid,id))
                add("language", langType.value.toString())
                add("theme","xcode")
                add("source", Base64.getEncoder().encodeToString(code.toByteArray()) + "HZNU")
                add("cid", cid.toString())
                add("pid",id.toString())
            }.build())
        }
    }

    override fun getMySolution(id: Int): List<Solution> {
        return fetch(url("https://acm.hznu.edu.cn/OJ/status.php?csrf_token=${csrf}&problem_id=${id}&user_id=${account}"))
            .getElementsByTag(
                "tbody"
            )[0].getElementsByTag("tr").map {
            val raw = it.getElementsByTag("td")
            object : Solution {
                override val problemId: Int
                    get() = raw[2].text().toInt()
                override val langType: String
                    get() = raw[6].text()
                override val result: String
                    get() = raw[3].text()
                override val code: String
                    get() = getCode(raw[2].text().toInt(), raw[0].text().toLong())

            }

        }
    }

    override fun getMySolution(id: Int, pid: Int): List<Solution> {
        val re = fetch(url("https://acm.hznu.edu.cn/OJ/status.php?cid=${id}&user_id=${account}"))
            .getElementsByTag(
                "tbody"
            )[1].getElementsByTag("tr").map {
            val raw = it.getElementsByTag("td")
            object : Solution {
                override val problemId: Int
                    get() = solveUrl(raw[2].getElementsByTag("a")[0].attr("href"))["pid"]!!.toInt()
                override val langType: String
                    get() = raw[6].text()
                override val result: String
                    get() = raw[3].text()
                override val code: String
                    get() = getContestCode(id,pid,raw[0].text().toLong())
            }

        }.filter {
            it.problemId == pid
        }
        return re
    }

    private fun getContestCode(cid:Int,pid:Int,ssid: Long):String{
        return fetch(url("https://acm.hznu.edu.cn/OJ/submitpage.php?cid=${cid}&pid=${pid}&sid=${ssid}"))
            .getElementById("editor")!!.text().also { println("code" + it) }
    }

    override fun getProblemSet(page: Int): List<ProblemSet>{
        return fetch(url("https://acm.hznu.edu.cn/OJ/contestset.php"))
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr").map { element ->
                val raw = element.getElementsByTag("td")
                object :ProblemSet{
                    override val id: Int
                        get() = raw[0].text().toInt()
                    override val name: String
                        get() = raw[1].text()
                    override val start: String
                        get() = raw[2].text()
                    override val end: String
                        get() = raw[3].text()
                    override val status: String
                        get() = raw[4].text()
                    override val type: String
                        get() = raw[5].text()
                    override val problems: List<Problem>
                        get(){
                            return fetch(url("https://acm.hznu.edu.cn/OJ/contest.php?cid=${raw[0].text()}"))
                                .getElementsByTag("tbody")[0]
                                .getElementsByTag("tr").map {
                                    val problem = it.getElementsByTag("td")
                                    object :Problem{
                                        override val id: Int
                                            get() = solveUrl(problem[1].getElementsByTag("a")[0].attr("href"))["pid"]!!.toInt()
                                        override val name: String
                                            get() = problem[1].text()
                                        override val author: String
                                            get() = "undefined"
                                        override val src: String
                                            get() =  "undefined"
                                        override val ac: Int
                                            get() = 0
                                        override val sub: Int
                                            get() = 0
                                        override val score: Int
                                            get() = 0

                                        override fun getAccepted(page: Int): List<Accepted> {
                                            return emptyList()
                                        }

                                        override fun getAllAccepted(): List<Accepted> {
                                            return emptyList()
                                        }


                                    }

                                }

                        }
                }
            }
    }
    private fun solveUrl(url:String):Map<String,String>{
        return url.split("?")[1].split("&").associate {
            Pair(it.split("=")[0], it.split("=")[1])
        }
    }

}