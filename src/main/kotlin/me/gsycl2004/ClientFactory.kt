package me.gsycl2004

import IClientFactory
import me.gsycl2004.impl.ClientImpl
import me.gsycl2004.interfaces.Client
import me.gsycl2004.interfaces.LoginClient


object ClientFactory : IClientFactory {
    override fun newClient():Client{
        return ClientImpl()
    }

    override fun newLoginClient(id:String, password:String):LoginClient{
        return newClient().login(id,password)
    }


}

