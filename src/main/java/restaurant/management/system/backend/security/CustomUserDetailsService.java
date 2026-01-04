package restaurant.management.system.backend.security;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.management.system.backend.entities.User;
import restaurant.management.system.backend.repositories.UserRepository;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User foundPerson = userRepository.findByEmail(email);
        if (foundPerson == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }
        return new CustomUserDetails(foundPerson);
    }
}
