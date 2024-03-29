package sk.fiit.jibrarian.data.impl;

import sk.fiit.jibrarian.data.UserRepository;
import sk.fiit.jibrarian.model.Role;
import sk.fiit.jibrarian.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryUserRepository implements UserRepository {

    private static final Logger LOGGER = Logger.getLogger(InMemoryUserRepository.class.getName());
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private User currentlyLoggedInUser;

    @Override
    public void saveCurrentlyLoggedInUser(User user) {
        this.currentlyLoggedInUser = user;
    }

    @Override
    public Optional<User> getCurrentlyLoggedInUser() {
        return Optional.ofNullable(currentlyLoggedInUser);
    }

    @Override
    public void saveUser(User user) throws AlreadyExistingUserException {
        if (users.containsKey(user.getEmail())) {
            LOGGER.log(Level.WARNING, "User with email {0} already exists", user.getEmail());
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
            LOGGER.log(Level.WARNING, "User with email {0} doesnt exist", user.getEmail());
            throw new UserNotFound(String.format("User with id %s doesn't exist", user.getId()));
        }
        users.put(user.getEmail(), user);
    }

    @Override
    public void deleteUser(User user) throws UserNotFound {
        if (!users.containsKey(user.getEmail())) {
            LOGGER.log(Level.WARNING, "User with email {0} doesnt exist", user.getEmail());
            throw new UserNotFound(String.format("User with id %s doesn't exist", user.getId()));
        }
        users.remove(user.getEmail());
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
