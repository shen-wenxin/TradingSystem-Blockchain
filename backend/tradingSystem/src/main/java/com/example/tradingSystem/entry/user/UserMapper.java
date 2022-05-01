package com.example.tradingSystem.entry.user;

import java.util.List;

import com.example.tradingSystem.domain.User.User;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE ACCOUNT = #{account}")
    User getUserByName(@Param("account") String account);

    @Select("SELECT * FROM USER")
    List<User> getAllUser();

    @Insert("INSERT INTO USER(account, name, password, role) VALUES(#{account}, #{name}, #{password}, #{role})")
    int insert(@Param("account") String account, @Param("name") String name,@Param("password") String password, @Param("role") Integer role);

    @Delete("DELETE FROM user WHERE account =#{account}")
    int delete(String account);

    @Update("UPDATE user SET PASSWORD=#{password}, WHERE ACCOUNT = #{account}")
    int update(String account, String password);
  
}
