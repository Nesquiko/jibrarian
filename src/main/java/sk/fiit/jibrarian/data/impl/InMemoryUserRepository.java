package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.List;
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

    @Override
    public void updateUser(User user) throws UserNotFound {
        if (!users.containsKey(user.getEmail())) {
            // TODO Log it
            throw new UserNotFound(String.format("User with id %s doesn't exist", user.getId()));
        }
        users.put(user.getEmail(), user);
    }

    @Override
    public List<User> getAllLibrarians() {
        return users.values().stream().filter(user -> user.getRole() == Role.LIBRARIAN).toList();
    }

    @Override
    public List<User> getAllAdmins() {
        return users.values().stream().filter(user -> user.getRole() == Role.ADMIN).toList();
    }
}
