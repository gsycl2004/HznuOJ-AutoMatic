import me.gsycl2004.interfaces.Client
import me.gsycl2004.interfaces.LoginClient

interface IClientFactory {
    fun newClient(): Client
    fun newLoginClient(id: String, password: String): LoginClient
}
