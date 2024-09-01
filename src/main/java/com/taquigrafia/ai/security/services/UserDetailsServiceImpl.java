package com.taquigrafia.ai.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taquigrafia.ai.exceptions.UserNotActiveException;
import com.taquigrafia.ai.models.User;
import com.taquigrafia.ai.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

    if(!user.getIsActive()) {
      throw new UserNotActiveException("Usuário não ativo: " + username);
    }

    return UserDetailsImpl.build(user);
  }

}
