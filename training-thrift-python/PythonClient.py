import sys
sys.path.append('./someservice')

from someservice import SomeService
from someservice.ttypes import *
from someservice.constants import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


try:
    # Make socket
    transport = TSocket.TSocket('192.168.31.151', 9898)
    # Buffering is critical. Raw sockets are very slow
    transport = TTransport.TBufferedTransport(transport)
    # Wrap in protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    # Create a client to use the protocol encoder
    client = SomeService.Client(protocol)
    # Connect!
    transport.open()


    msg = client.echo("Abc1111")
    print("=============%s" %msg)
    transport.close()

except Thrift.TException as tx:
    print("thrift except error%s" %(tx.message))
