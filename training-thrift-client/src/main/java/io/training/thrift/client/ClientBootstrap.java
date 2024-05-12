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
//        handleMultiProtocol();
        //standard();
        handleAttachment();
    }

    private static void standard() {
        System.out.println("客户端启动....");
        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 9898, 30000);
            // 协议要和服务端一致
            TProtocol protocol = new TBinaryProtocol(transport);
//            SomeService.Client client = new SomeService.Client(protocol);
            SomeService.Client.Factory clientFactory = new SomeService.Client.Factory();
            SomeService.Client client = clientFactory.getClient(protocol);
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
//            SomeService.Client client = new SomeService.Client(protocol);
            SomeService.Client.Factory clientFactory = new SomeService.Client.Factory();
            SomeService.Client client = clientFactory.getClient(protocol);
            transport.open();
            String result = client.echo("哈哈");
            System.out.println(result);

//            List<String> msgList = new ArrayList<>();
//            for(int i = 0; i < 100000; i++) {
//                msgList.add(UUID.randomUUID().toString());
//            }
//            for(int i = 0; i < 100000; i++) {
//                result = client.echo(msgList.get(i));
//                if(!result.equals("Hello " + msgList.get(i))) {
//                    System.out.println("不相同：" + msgList.get(i));
//                }
//            }


            for(int i = 0; i < 100000; i++) {
                String r = UUID.randomUUID().toString();
                try {
                    result = client.echo(r);
                    if(!result.equals("Hello " + r)) {
                        log.info("不相同：request:{}, result:{}", r ,result);
                    }
                } catch (Exception e) {
                    log.warn("##################:{}", e.getMessage());

                    if (e instanceof TTransportException) {
                        TTransportException cause = (TTransportException) e;
                        if (cause.getType() == TTransportException.NOT_OPEN ||
                                cause.getType() == TTransportException.END_OF_FILE ||
                                cause.getType() == TTransportException.TIMED_OUT ||
                                cause.getType() == TTransportException.UNKNOWN) {

                            transport.close();
                            try {
                                transport.open();
                            } catch (TTransportException e1) {
                                // throw new RuntimeException(e1);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
//            if (null != transport) {
//                transport.close();
//            }

            if(!transport.isOpen()) {
                try {
                    transport.open();
                } catch (TTransportException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void handleMultiProtocol() {
        System.out.println("客户端启动....");
        TTransport transport = null;
        TTransport tsocket = null;
        try {
            String addr = "localhost:9898";
            String[] ipAndPort = addr.split(":");
            tsocket = new TSocket(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
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
