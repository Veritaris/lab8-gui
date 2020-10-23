package dependencies.UserAuthorization;

public class Session {
    private User user;

    public Session() {
        this.user = new User("", "");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void clear() {
        this.user = new User("", "");
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return String.format(
          "Session %s with user %s", this.hashCode(), this.user.getUsername()
        );
    }
}
