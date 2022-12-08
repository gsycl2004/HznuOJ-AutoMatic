import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @author wcy
 * @date 2020/3/4
 * 为了支持okhttp 绕过验签功能
 */
object SSLSocketClientUtil {
    fun getSocketFactory(manager: TrustManager): SSLSocketFactory? {
        var socketFactory: SSLSocketFactory? = null
        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(manager), SecureRandom())
            socketFactory = sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return socketFactory
    }

    val x509TrustManager: X509TrustManager
        get() = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { _, _ -> true }
}