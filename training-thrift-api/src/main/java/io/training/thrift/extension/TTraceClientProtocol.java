package io.training.thrift.extension;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TTransport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TTraceClientProtocol extends TBinaryProtocol {

    private Map<String, String> HEAD_INFO;

    public TTraceClientProtocol(TTransport transport) {
        super(transport);
        HEAD_INFO = new HashMap<>();
    }

    //clientSend
    @Override
    public void writeMessageBegin(TMessage tMessage) throws TException {
        //trace start
        //TraceUtils.startLocalTracer("rpc.thrift start");

        String methodName = tMessage.name;
        //TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_METHOD, methodName);
        TTransport transport = this.getTransport();
        //String hostAddress = ((TSocket) transport).getSocket().getRemoteSocketAddress().toString();
        //TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_SERVER, hostAddress);

        super.writeMessageBegin(tMessage);
        //write trace header to field0
        writeFieldZero();
    }


    public void writeFieldZero() throws TException {
        TField TRACE_HEAD = new TField("traceHeader", TType.MAP, (short) 0);
        this.writeFieldBegin(TRACE_HEAD);
        {
            Map<String, String> traceInfo = genTraceInfo();
            this.writeMapBegin(new TMap(TType.STRING, TType.STRING, traceInfo.size()));
            for (Map.Entry<String, String> entry : traceInfo.entrySet()) {
                this.writeString(entry.getKey());
                this.writeString(entry.getValue());
            }

            this.writeMapEnd();
        }
        this.writeFieldEnd();
    }

    private Map<String, String> genTraceInfo() {
        //gen trace info
        HEAD_INFO.put("randomUUID", UUID.randomUUID().toString());
        return HEAD_INFO;
    }

    //clientReceive
    @Override
    public TMessage readMessageBegin() throws TException {
        TMessage tMessage = super.readMessageBegin();
        if (tMessage.type == TMessageType.EXCEPTION) {
            TApplicationException x = TApplicationException.read(this);
            //TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_EXCEPTION, StringUtil.trimNewlineSymbolAndRemoveExtraSpace(x.getMessage()));
            //TraceUtils.endAndSendLocalTracer();
        } else if (tMessage.type == TMessageType.REPLY) {
            //TraceUtils.endAndSendLocalTracer();
        }
        return tMessage;
    }
}
