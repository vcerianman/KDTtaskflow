package com.kdt.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;

import com.kdt.taskflow.domain.UserStatus;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    int insert(User user);

    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(@Param("email") String email);

    List<User> search(@Param("role") UserRole role,
                      @Param("keyword") String keyword);

    List<User> searchAdmin(@Param("username") String username,
                           @Param("email") String email,
                           @Param("role") UserRole role,
                           @Param("status") UserStatus status);

    int update(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    int softDeleteById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
