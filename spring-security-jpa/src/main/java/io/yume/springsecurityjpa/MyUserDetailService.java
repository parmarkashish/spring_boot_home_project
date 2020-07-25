package io.yume.springsecurityjpa;

import io.yume.springsecurityjpa.models.MyUserDetails;
import io.yume.springsecurityjpa.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService{
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user=userRepository.findByUserName(s);
        user.orElseThrow( () -> new UsernameNotFoundException("Not Found: "+s));
        return new MyUserDetails(user.get());
    }
}
