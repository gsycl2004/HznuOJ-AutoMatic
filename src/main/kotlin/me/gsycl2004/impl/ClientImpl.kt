package me.gsycl2004.impl

import SSLSocketClientUtil
import me.gsycl2004.interfaces.Client
import me.gsycl2004.interfaces.LoginClient
import me.gsycl2004.interfaces.data.Accepted
import me.gsycl2004.interfaces.data.Problem
import me.gsycl2004.interfaces.data.User
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.StringJoiner

class ClientImpl : Client {
    private val manager = SSLSocketClientUtil.x509TrustManager
    private val client = OkHttpClient().newBuilder().apply {
        hostnameVerifier(SSLSocketClientUtil.hostnameVerifier)
        //proxy(
        //   Proxy(Proxy.Type.HTTP,InetSocketAddress("localhost",10809))
        //)
        sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager)!!, manager)
        cookieJar(object : CookieJar {
            val map = HashMap<String, List<Cookie>>()
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return map.getOrDefault(url.host, listOf())
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                map[url.host] = cookies
            }
        })
    }.build()

    internal fun url(text: String): Request {
        return Request.Builder().apply {
            url(text)
        }.build()
    }

    internal fun fetch(req: Request): Document {
        return Jsoup.parse(client.newCall(req).execute().body!!.string())
    }


    override fun login(id: String, password: String): LoginClient {
        return LoginClientImpl(this,id,password)
    }

    override fun getProblemsByPage(page: Int): List<Problem> {
        return fetch(url("https://acm.hznu.edu.cn/OJ/problemset.php?&page=$page"))
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr").map {
                val raw = it.getElementsByTag("td")
                object : Problem {
                    override val id: Int
                        get() = raw[1].text().toInt()
                    override val name: String
                        get() = raw[2].text()
                    override val author: String
                        get() = raw[3].text()
                    override val src: String
                        get() = raw[4].text()
                    override val ac: Int
                        get() = raw[5].text().split("/")[0].toInt()
                    override val sub: Int
                        get() = raw[5].text().split("/")[1].toInt()
                    override val score: Int
                        get() = raw[6].text().toDouble().toInt()

                    override fun getAccepted(page: Int): List<Accepted> = getProblemAccepted(id, page)
                    override fun getAllAccepted(): List<Accepted> = getAllProblemAccepted(id)

                }
            }
    }

    override fun getAllProblems(): List<Problem> {
        val list = ArrayList<Problem>()
        var num = 0
        while (true) {
            val old = getProblemsByPage(num);
            if (old.isEmpty()) break
            list.addAll(old)
            num++
        }
        return list
    }

    private fun String.toUser(): User {
        return fetch(url("https://acm.hznu.edu.cn/OJ/userinfo.php?user=${this}")).getElementsByTag("tbody")[0].let {
            val li = it.getElementsByTag("tr")
            object : User {
                override val id: String
                    get() = li[0].getElementsByTag("td")[1].text()
                override val nick: String
                    get() = li[1].getElementsByTag("td")[1].text()
                override val ranK: Int
                    get() = li[2].getElementsByTag("td")[1].text().toInt()
                override val douqi: Int
                    get() = li[3].getElementsByTag("td")[1].text().toInt()
                override val level: String
                    get() = li[4].getElementsByTag("td")[1].text()
                override val totalAC: Int
                    get() = li[5].getElementsByTag("td")[1].text().toInt()
                override val school: String
                    get() = li[6].getElementsByTag("td")[1].text()
                override val email: String
                    get() = li[7].getElementsByTag("td")[1].text()
            }

        }
    }


    override fun getProblemAccepted(id: Int, page: Int): List<Accepted> {
        return fetch(url("https://acm.hznu.edu.cn/OJ/problemstatus.php?&id=${id}&order=date&page=${page}"))
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr").filterIndexed { index, _ -> index != 0 }.map {
                val raw = it.getElementsByTag("td")
                object : Accepted {
                    override val user: User
                        get() = raw[1].text().toUser()
                    override val result: String
                        get() = raw[2].text()
                    override val runTime: String
                        get() = raw[3].text()
                    override val memory: String
                        get() = raw[4].text()
                    override val codeLength: String
                        get() = raw[5].text()
                    override val lang: String
                        get() = raw[6].text()
                    override val submit: String
                        get() = raw[7].text()
                }
            }
    }

    override fun getAllProblemAccepted(id: Int): List<Accepted> {
        val list = ArrayList<Accepted>()
        var num = 0
        while (true) {
            val old = getProblemAccepted(id, num)
            if (old.isEmpty()) break
            list.addAll(old)
            num++
        }
        return list.reversed()
    }
}