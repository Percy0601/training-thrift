namespace java io.training.thrift.api
struct User {
    1: i32 userId;
    2: string username;
    3: map<string, string> desc;
}

service SomeService {
	string echo(1: string msg);
    i32 addUser(1: User user);
    list<User> findUserByIds(1: list<i32> idList);
}