package project.gamepage.data.model.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToMany
    @JoinTable(name = "user_friend_requests", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id") )
    private Set<User> userFriendRequest;
    @ManyToMany
    @JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id") )
    private Set<User> userFriend;
    private int gamesPlayed;
    private int gamesWon;
    private int movesDone;



    public User() {
        super();
        this.roles = new HashSet<>();
    }
    public User(String username, String email, String password, String firstName, String lastName, Collection<Role> roles, int gamesPlayed, int gamesWon, int movesDone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.movesDone = movesDone;
    }

    public void sendFriendRequest(User user) {
        if (CollectionUtils.isEmpty(this.userFriendRequest)) {
            this.userFriendRequest = new HashSet<>();
        }
        this.userFriendRequest.add(user);
    }

    public void addFriend(User user) {
        if (CollectionUtils.isEmpty(this.userFriend)) {
            this.userFriend = new HashSet<>();
        }
        this.userFriend.add(user);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Set<User> getUserFriendRequest() {
        return userFriendRequest;
    }

    public Set<User> getUserFriend() {
        return userFriend;
    }

    public void setUserFriend(Set<User> userFriend) {
        this.userFriend = userFriend;
    }

    public void setUserFriendRequest(Set<User> userFriendRequest) {
        this.userFriendRequest = userFriendRequest;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getMovesDone() {
        return movesDone;
    }

    public void setMovesDone(int movesDone) {
        this.movesDone = movesDone;
    }
}
