package io.training.thrift.extension;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.BufferedInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AttachableBinaryProtocol extends TBinaryProtocol {
    private static final long NO_LENGTH_LIMIT = -1;
    private Map<String, String> attachment;

    public AttachableBinaryProtocol(TTransport trans) {
        super(trans);
        attachment = new HashMap<>();
    }

    public AttachableBinaryProtocol(TTransport trans, boolean strictRead, boolean strictWrite) {
        super(trans, strictRead, strictWrite);
        attachment = new HashMap<>();
    }

    public AttachableBinaryProtocol(TTransport trans,
                                    long stringLengthLimit,
                                    long containerLengthLimit,
                                    boolean strictRead,
                                    boolean strictWrite) {
        super(trans, stringLengthLimit, containerLengthLimit, strictRead, strictWrite);
        attachment = new HashMap<>();
    }
    public static class Factory extends TBinaryProtocol.Factory {
        public Factory() {
            this(false, true);
        }

        public Factory(boolean strictRead, boolean strictWrite) {
            this(strictRead, strictWrite, NO_LENGTH_LIMIT, NO_LENGTH_LIMIT);
        }

        public Factory(boolean strictRead, boolean strictWrite, long stringLengthLimit, long containerLengthLimit) {
            stringLengthLimit_ = stringLengthLimit;
            containerLengthLimit_ = containerLengthLimit;
            strictRead_ = strictRead;
            strictWrite_ = strictWrite;
        }

        public TProtocol getProtocol(TTransport trans) {
            return new AttachableBinaryProtocol(trans, stringLengthLimit_, containerLengthLimit_, strictRead_, strictWrite_);
        }
    }

    public void markTFramedTransport(TProtocol in) {
        try {
            Field tioInputStream = TIOStreamTransportFieldsCache.getInstance().getTIOInputStream();
            if (tioInputStream == null){
                return;
            }
            BufferedInputStream inputStream = (BufferedInputStream) tioInputStream.get(in.getTransport());
            inputStream.mark(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMessageBegin(TMessage message) throws TException {
        super.writeMessageBegin(message);

        if(attachment.size() > 0){
            writeFieldZero();
        }
    }

    public void writeFieldZero() throws TException{
        TField ATTACHMENT = new TField("attachment", TType.MAP, (short) 0);
        this.writeFieldBegin(ATTACHMENT);
        {
            this.writeMapBegin(new TMap(TType.STRING, TType.STRING, attachment.size()));
            for (Map.Entry<String, String> entry: attachment.entrySet()) {
                this.writeString(entry.getKey());
                this.writeString(entry.getValue());
            }
            this.writeMapEnd();
        }
        this.writeFieldEnd();
    }

    public boolean readFieldZero() throws TException {
        TField schemeField = this.readFieldBegin();
        if (schemeField.id == 0 && schemeField.type == TType.MAP) {
            TMap _map = this.readMapBegin();
            attachment = new HashMap<>(_map.size);
            for (int i = 0; i < _map.size; ++i) {
                String key = this.readString();
                String value = this.readString();
                attachment.put(key, value);
            }
            this.readMapEnd();
        }
        this.readFieldEnd();
        return attachment.size() > 0;
    }

    /*
     * 重置TFramedTransport流，不影响Thrift原有流程
     */
    public void resetTFramedTransport(TProtocol in) {
        try {
            Field tioInputStream = TIOStreamTransportFieldsCache.getInstance().getTIOInputStream();
            if (tioInputStream == null){
                return;
            }
            BufferedInputStream inputStream = (BufferedInputStream) tioInputStream.get(in.getTransport());
            inputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TIOStreamTransportFieldsCache {
        private static TIOStreamTransportFieldsCache instance;
        private final Field inputStream_;
        private final String TIOStreamTransport_inputStream_ = "inputStream_";

        private TIOStreamTransportFieldsCache() throws Exception {
            inputStream_ = TIOStreamTransport.class
                    .getDeclaredField(TIOStreamTransport_inputStream_);
            inputStream_.setAccessible(true);
        }

        public static TIOStreamTransportFieldsCache getInstance()
                throws Exception {
            if (instance == null) {
                synchronized (TIOStreamTransportFieldsCache.class) {
                    if (instance == null) {
                        instance = new TIOStreamTransportFieldsCache();
                    }
                }
            }
            return instance;
        }

        public Field getTIOInputStream() {
            return inputStream_;
        }
    }

    @Override
    public TMessage readMessageBegin() throws TException {
        System.out.println("============");
        TMessage tMessage = super.readMessageBegin();

        if (tMessage.type == TMessageType.EXCEPTION) {
            TApplicationException x = TApplicationException.read(this);
//            TraceUtils.submitAdditionalAnnotation(Constants.TRACE_THRIFT_EXCEPTION, StringUtil.trimNewlineSymbolAndRemoveExtraSpace(x.getMessage()));
//            TraceUtils.endAndSendLocalTracer();
        } else if (tMessage.type == TMessageType.REPLY) {
//            TraceUtils.endAndSendLocalTracer();
        }
        return tMessage;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }
}


