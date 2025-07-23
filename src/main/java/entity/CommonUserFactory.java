package entity;

public class CommonUserFactory implements UserFactory{

    @Override
    public User createUser(String name, String password){
     return new CommonUser(name, password);
    }
}
