package org.librazy.demo.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.librazy.demo.dubbo.domain.BlogEntryEntity;
import org.librazy.demo.dubbo.domain.SrpAccountEntity;
import org.librazy.demo.dubbo.domain.UserEntity;
import org.librazy.demo.dubbo.domain.repo.BlogRepository;
import org.librazy.demo.dubbo.domain.repo.UserRepository;
import org.librazy.demo.dubbo.model.SrpSignupForm;
import org.librazy.demo.dubbo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(interfaceClass = UserService.class)
@Component
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BlogRepository blogRepository) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity loadUserByUsername(String id) {
        log.debug("Loading user {}", id);
        Optional<UserEntity> user = userRepository.findById(Long.valueOf(id));
        if (!user.isPresent()) {
            log.info("user {} not found when loading", id);
            throw new UsernameNotFoundException("");
        }
        return user.get();
    }

    @Override
    @Transactional
    public UserEntity registerUser(SrpSignupForm signupForm) {
        log.info("Registering new srp user {}", signupForm.getEmail());
        UserEntity user = new UserEntity(signupForm.getEmail(), signupForm.getNick());
        new SrpAccountEntity(user, signupForm.getSalt(), signupForm.getVerifier());
        user = userRepository.saveAndFlush(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public SrpAccountEntity getSrpAccount(String email) {
        return userRepository.getAccount(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public UserEntity update(UserEntity old, User userForm) {
        if (userForm.getEmail() != null) {
            old.setEmail(userForm.getEmail());
        }
        if (userForm.getAvatar() != null) {
            old.setAvatar(userForm.getAvatar());
        }
        if (userForm.getBio() != null) {
            old.setBio(userForm.getBio());
        }
        if (userForm.getNick() != null) {
            old.setNick(userForm.getNick());
        }
        return userRepository.saveAndFlush(old);
    }

    @Override
    @Transactional
    public boolean addStarredEntries(UserEntity user, BlogEntryEntity blog) {
        boolean addStarredEntries = user.addStarredEntries(blog);
        userRepository.saveAndFlush(user);
        blogRepository.saveAndFlush(blog);
        return addStarredEntries;
    }

    @Override
    public boolean removeStarredEntries(UserEntity user, BlogEntryEntity blog) {
        boolean removeStarredEntries = user.removeStarredEntry(blog);
        userRepository.saveAndFlush(user);
        blogRepository.saveAndFlush(blog);
        return removeStarredEntries;
    }

    @Override
    @Transactional
    public boolean addFollowing(UserEntity following, UserEntity followed) {
        boolean addFollowing = following.addFollowing(followed);
        userRepository.save(followed);
        userRepository.save(following);
        userRepository.flush();
        return addFollowing;
    }

    @Override
    @Transactional
    public boolean removeFollowing(UserEntity following, UserEntity followed) {
        boolean removeFollowing = following.removeFollowing(followed);
        userRepository.save(followed);
        userRepository.save(following);
        userRepository.flush();
        return removeFollowing;
    }

}
