package io.training.thrift.client;

import io.training.thrift.api.SomeService;
import io.training.thrift.extension.AttachableBinaryProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientBootstrap {
    private static Logger log = LoggerFactory.getLogger(ClientBootstrap.class);
    public static void main(String[] args) {
        handleAttachment();
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
            transport = new TSocket("localhost", 9898, 30000);
            // 协议要和服务端一致
            TProtocol protocol = new AttachableBinaryProtocol(transport);
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
}
