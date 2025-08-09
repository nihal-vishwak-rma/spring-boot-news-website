package com.news.SpringBootNews_wb.services;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.news.SpringBootNews_wb.model.Otp;
import com.news.SpringBootNews_wb.model.User;
import com.news.SpringBootNews_wb.model.UserRole;
import com.news.SpringBootNews_wb.repository.OtpRepository;
import com.news.SpringBootNews_wb.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
           .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

          return  new CustomUserDetails(user);
    }


    public boolean registerUser(User user){


        if (userRepository.findByEmail(user.getEmail()).isPresent()) {

            return false;
            
        }

       Optional<Otp> otp = otpRepository.findByEmail(user.getEmail());

       if (otp.isEmpty() || !otp.get().isVerified()) {
        
          return false;
       }


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        otpRepository.delete(otp.get());

        return true;
    }

    public boolean deleteUser(Long id){

        try {
            
            userRepository.deleteById(id);
            
            return true;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }

       
    }


    public boolean switchRole(Long id){

        try {
            
            Optional<User> user = userRepository.findById(id);

            if (user != null && user.isPresent()) {

                User user2 =  user.get();

                if (user2.getRole().equals(UserRole.USER)) {

                    user2.setRole(UserRole.ADMIN);
                    userRepository.save(user2);

                    return true;                    
                }
                else{

                    user2.setRole(UserRole.USER);
                    userRepository.save(user2);

                    return true;                

                }
                
            }

        } catch (Exception e) {
           e.printStackTrace();
           return false ;
        }

        return false;
        
    }

   


}
