package cn.anytec.security.service.impl;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

/**
 * @Description: TODO
 * @author: zhao
 * @date 2018/9/14 16:06
 */
public class testDemo {

    public static void main(String[] args) {
        InetAddress localHost;
        String ipAddress = "";
        try {
            localHost = Inet4Address.getLocalHost();
            ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(ipAddress);
    }
}