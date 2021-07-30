package io.training.thrift.client;

import io.training.thrift.api.SomeService;
import io.training.thrift.extension.AttachableBinaryProtocol;
import io.training.thrift.extension.TTraceClientProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientBootstrap {
    private static Logger log = LoggerFactory.getLogger(ClientBootstrap.class);
    public static void main(String[] args) {
        handleMultiProtocol();
    }

    private static void standard() {
        System.out.println("客户端启动....");
        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 9898, 30000);
            // 协议要和服务端一致
            TProtocol protocol = new TBinaryProtocol(transport);
            SomeService.Client client = new SomeService.Client(protocol);
            transport.open();
            String result = client.echo("哈哈");
            System.out.println(result);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }


    private static void handleAttachment() {
        System.out.println("客户端启动....");
        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 8761, 30000);
            // 协议要和服务端一致
            TProtocol protocol = new AttachableBinaryProtocol(transport);
            SomeService.Client client = new SomeService.Client(protocol);
            transport.open();
            String result = client.echo("哈哈");
            System.out.println(result);

            List<String> msgList = new ArrayList<>();
            for(int i = 0; i < 100000; i++) {
                msgList.add(UUID.randomUUID().toString());
            }
            for(int i = 0; i < 100000; i++) {
                result = client.echo(msgList.get(i));
                if(!result.equals("Hello " + msgList.get(i))) {
                    System.out.println("不相同：" + msgList.get(i));
                }
            }
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }

    private static void handleMultiProtocol() {
        System.out.println("客户端启动....");
        TTransport transport = null;
        try {
            String addr = "localhost:8761";
            String[] ipAndPort = addr.split(":");
            TTransport tsocket = new TSocket(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
            transport = new TFramedTransport(tsocket);
            TProtocol protocol = new TTraceClientProtocol(transport);
//            TProtocol protocol = new TBinaryProtocol(transport);
            //io.training.thrift.api.SomeService$
            TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol, SomeService.class.getName() + "$");
            SomeService.Client client = new SomeService.Client(multiplexedProtocol);
            tsocket.open();
            for(int i = 0; i < 10000; i++) {
                String hello = client.echo("kdfasdf" + i);
                System.out.println("======" + hello);
            }

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }



}
