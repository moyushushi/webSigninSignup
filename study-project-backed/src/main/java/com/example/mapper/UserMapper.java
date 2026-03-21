package com.example.mapper;

import com.example.entity.auth.Account;
import com.example.entity.user.AccountUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 修复1：单参数方法（按原逻辑，text 匹配 username/email）
    @Select("select * from db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(@Param("text") String text);

    // 修复2：单参数方法（同上）
    @Select("select * from db_account where username = #{text} or email = #{text}")
    AccountUser findAccountUserByNameOrEmail(@Param("text") String text);

    // 修复3：多参数方法添加 @Param 注解，绑定参数名
    @Insert("insert into db_account(email,username,password) values (#{email},#{username},#{password})")
    int createAccount(
            @Param("email") String email,
            @Param("username") String username,
            @Param("password") String password
    );

    // 修复4：多参数方法添加 @Param 注解，绑定参数名
    @Update("update db_account set password=#{password} where email=#{email}")
    int setPasswordByEmail(
            @Param("password") String password,
            @Param("email") String email
    );
}
