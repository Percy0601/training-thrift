package io.training.thrift.server;

import io.training.thrift.api.SomeService;
import io.training.thrift.extension.AttachableBinaryProtocol;
import io.training.thrift.extension.AttachableProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class ServerBootstrap {

    public static void main(String[] args) {
        handleAttachment();
    }

    private static void standard() {
        try {
            System.out.println("服务端开启....");
            TProcessor tprocessor = new SomeService.Processor<SomeService.Iface>(new SomeServiceImpl());
            // 简单的单线程服务模型
            TServerSocket serverTransport = new TServerSocket(9898);
            TServer.Args tArgs = new TServer.Args(serverTransport);
            tArgs.processor(tprocessor);
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            TServer server = new TSimpleServer(tArgs);
            server.serve();
        } catch (TTransportException e) {
            System.out.println("server start error:{}" +  e.getMessage());
        }
    }
    private static void handleAttachment() {
        try {
            System.out.println("服务端开启....");
            TProcessor tprocessor = new SomeService.Processor<SomeService.Iface>(new SomeServiceImpl());
            AttachableProcessor proxyProcessor = new AttachableProcessor(tprocessor);
            // 简单的单线程服务模型
            TServerSocket serverTransport = new TServerSocket(9898);
            TServer.Args tArgs = new TServer.Args(serverTransport);
            tArgs.processor(proxyProcessor);
            tArgs.protocolFactory(new AttachableBinaryProtocol.Factory());
            TServer server = new TSimpleServer(tArgs);
            server.serve();
        } catch (TTransportException e) {
            System.out.println("server start error:{}" +  e.getMessage());
        }
    }

}
