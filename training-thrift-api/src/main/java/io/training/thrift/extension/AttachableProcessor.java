package io.training.thrift.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

import java.util.Map;

@Slf4j
public class AttachableProcessor implements TProcessor {
    private TProcessor realProcessor;
    public AttachableProcessor(TProcessor realProcessor) {
        this.realProcessor = realProcessor;
    }

    @Override
    public boolean process(TProtocol in, TProtocol out) throws TException {
        if (in instanceof AttachableBinaryProtocol) {
            AttachableBinaryProtocol serverProtocol = (AttachableBinaryProtocol) in;
            serverProtocol.markTFramedTransport(in);
            TMessage tMessage = serverProtocol.readMessageBegin();
            serverProtocol.readFieldZero();
            Map<String, String> headInfo = serverProtocol.getAttachment();
            log.info("读取到的隐式参数:{}", headInfo);
//            String traceId = headInfo.get(TRACE_ID.getValue());
//            String parentSpanId = headInfo.get(PARENT_SPAN_ID.getValue());
//            String isSampled = headInfo.get(IS_SAMPLED.getValue());
//            Boolean sampled = isSampled == null || Boolean.parseBoolean(isSampled);
//
//            if (traceId != null && parentSpanId != null) {
//                TraceUtils.startLocalTracer("rpc.thrift receive", traceId, parentSpanId, sampled);
//                String methodName = tMessage.name;
//                TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_METHOD, methodName);
//                TTransport transport = in.getTransport();
//                String hostAddress = ((TSocket) transport).getSocket().getRemoteSocketAddress().toString();
//                TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_SERVER, hostAddress);
//            }
            serverProtocol.resetTFramedTransport(in);
        }
        boolean result = realProcessor.process(in, out);
        return result;
    }

}
