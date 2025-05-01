package br.com.rodrigo.gestortarefas.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddressUtil {

    public static String getLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "IP address not found";
        }
    }
}
