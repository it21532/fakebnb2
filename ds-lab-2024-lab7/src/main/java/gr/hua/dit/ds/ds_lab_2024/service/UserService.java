package gr.hua.dit.ds.ds_lab_2024.service;

import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user.getUsername();
    }

    @Transactional
    public User updateUser(User user) {
        Optional<User> existingOpt = userRepository.findByUsername(user.getUsername());
        if (existingOpt.isEmpty()) {
            throw new UsernameNotFoundException("User with username: " + user.getUsername() + " not found!");
        }
        User existingUser = existingOpt.get();
        user.setEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        userOpt.ifPresent(userRepository::delete);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User with username: " + username + " not found!");
        }
        User user = opt.get();
        String role = user.getSecurityRole();
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
        );
    }
    @Transactional
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }
}