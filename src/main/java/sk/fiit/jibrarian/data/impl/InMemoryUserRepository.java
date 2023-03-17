package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void saveUser(User user) throws AlreadyExistingUserException {
        if (users.containsKey(user.getEmail())) {
            // TODO Log it
            throw new AlreadyExistingUserException(String.format("User with id %s already exists", user.getId()));
        }
        users.put(user.getEmail(), user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }
}
