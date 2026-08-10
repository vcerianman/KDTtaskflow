package com.kdt.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;

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

    int update(User user);

    int deleteById(@Param("id") Long id);
}
