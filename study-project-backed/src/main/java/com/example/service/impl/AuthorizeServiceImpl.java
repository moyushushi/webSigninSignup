package com.example.service.impl;

import com.example.entity.auth.Account;
import com.example.entity.user.AccountUser;
import com.example.mapper.UserMapper;
import com.example.security.CustomUserDetails;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate template;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountUser account = mapper.findAccountUserByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new CustomUserDetails(account, account.getPassword()); //
    }
    /*
              1.生成验证码
              2.放redis里 (3分钟过期,60s可发送一次)
              3.发送到指定邮箱
              4.失败，redis中删除
              5.用户注册是从redis中取出，验证
             */
    @Override
    public String sendValidateEmail(String email,String sessionId,boolean hasAccount) {
        String key = "email:verify:" +email+":"+hasAccount;
        log.debug("【发送验证邮件】key={}, email={}, hasAccount={}", key, email, hasAccount); // 替换System.out为日志
        if (template.hasKey(key)){
            Long expire= Optional.of(template.getExpire(key,TimeUnit.SECONDS)).orElse(0L);
            log.info("【发送验证邮件】Redis存在该key，剩余过期时间={}秒", expire);
            if (expire>120) {
                log.warn("【发送验证邮件】请求频繁，email={}", email);
                return "请求频繁，请稍后再试";
            }
        }
        Account account = mapper.findAccountByNameOrEmail(email);
        log.debug("【发送验证邮件】数据库查询结果：account={}", account);
        if (account == null && hasAccount) {
            log.warn("【发送验证邮件】无此邮箱账户，email={}", email);
            return "没有此邮件地址的账户";
        }
        if (account != null&& !hasAccount) {
            log.warn("【发送验证邮件】邮箱已注册，email={}", email);
            return "此邮箱已被其他用户注册";
        }
        Random random = new Random();
        int code= random.nextInt(899999)+100000;
        log.info("【发送验证邮件】生成验证码：{}，email={}", code, email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("邮箱验证");
        message.setText("验证码是："+code);
        try {
            log.debug("【发送验证邮件】开始发送邮件，from={}, to={}", from, email);
            mailSender.send(message);
            template.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            log.info("【发送验证邮件】邮件发送成功，Redis已存储验证码，key={}", key);
            return null;
        }catch (MailException e){
            log.error("【发送验证邮件】邮件发送失败，email={}", email, e); // 打印异常栈
            return "邮件发送失败，请检查邮箱地址是否正确或联系管理员";
        }
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email:verify:" + email+":false";
        log.debug("【注册验证】key={}, username={}, email={}", key, username, email);
        if(template.hasKey(key)){
            String s= template.opsForValue().get(key);
            if (s==null) {
                log.warn("【注册验证】验证码失效，email={}", email);
                return "验证码失效";
            }
            if (code != null && code.equals(s)){
                // 修复：调用Mapper时传单个参数username（匹配@Param("text")）
                Account account=mapper.findAccountByNameOrEmail(username);
                password = encoder.encode(password);
                template.delete(key);
                if (account != null) {
                    log.warn("【注册验证】用户名已注册，username={}", username);
                    return "此用户名已被注册!";
                }
                // 修复：调用createAccount时参数顺序匹配@Param注解（email,username,password）
                int rows = mapper.createAccount(email, username, password);
                log.info("【注册验证】创建账号结果：受影响行数={}", rows);
                if(rows>0){
                    return null;
                }else {
                    log.error("【注册验证】创建账号失败，email={}, username={}", email, username);
                    return "内部错误，联系管理员";
                }
            }else {
                log.warn("【注册验证】验证码错误，email={}, 输入验证码={}, 正确验证码={}", email, code, s);
                return "验证码错误";
            }
        }else {
            log.warn("【注册验证】未完成邮箱验证，email={}", email);
            return "请先完成邮箱验证！";
        }
    }

    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:verify:" + email+":true";
        log.debug("【重置密码验证】key={}, email={}", key, email);
        if(template.hasKey(key)) {
            String s = template.opsForValue().get(key);
            if (s == null) {
                log.warn("【重置密码验证】验证码失效，email={}", email);
                return "验证码失效";
            }
            if (code != null && code.equals(s)) {
                template.delete(key);
                log.info("【重置密码验证】验证码正确，email={}", email);
                return null;
            } else {
                log.warn("【重置密码验证】验证码错误，email={}, 输入验证码={}, 正确验证码={}", email, code, s);
                return "验证码错误";
            }
        }else {
            log.warn("【重置密码验证】未完成邮箱验证，email={}", email);
            return "请先完成邮箱验证！";
        }
    }

    @Override
    public boolean resetPassword(String email ,String password) {
        log.debug("【重置密码】email={}", email);
        password = encoder.encode(password);
        // 修复：调用setPasswordByEmail时参数顺序匹配@Param注解（password,email）
        int rows = mapper.setPasswordByEmail(password, email);
        log.info("【重置密码】修改密码结果：受影响行数={}", rows);
        return rows>0;
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            // 1. 通过 UserDetailsService 加载用户（该方法本身已实现）
            UserDetails userDetails = loadUserByUsername(username);
            // 2. 使用 encoder 校验密码
            return encoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            // 用户不存在时返回 false
            return false;
        }
    }
}
