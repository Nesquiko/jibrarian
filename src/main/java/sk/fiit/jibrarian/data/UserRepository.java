package sk.fiit.jibrarian.data;

import sk.fiit.jibrarian.model.User;

import java.util.Optional;

public interface UserRepository {

    /**
     * Persists user
     */
    void saveUser(User user) throws AlreadyExistingUserException;

    /**
     * Return user with matching email, it doesn't exist, returns empty optional
     */
    Optional<User> getUserByEmail(String email);

    class AlreadyExistingUserException extends Exception {
        public AlreadyExistingUserException(String message) {
            super(message);
        }
    }
}
