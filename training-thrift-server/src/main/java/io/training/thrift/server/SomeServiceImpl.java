package io.training.thrift.server;

import io.training.thrift.api.SomeService;
import io.training.thrift.api.User;
import org.apache.thrift.TException;

import java.util.List;
import java.util.Random;

public class SomeServiceImpl implements SomeService.Iface {
    @Override
    public String echo(String msg) throws TException {
        return "Hello " + msg;
    }

    @Override
    public int addUser(User user) throws TException {

        user.setUserId(new Random().nextInt(100));
        return user.getUserId();
    }

    @Override
    public List<User> findUserByIds(List<Integer> idList) throws TException {
        return null;
    }
}
