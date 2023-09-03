package com.hnn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hnn.common.R;
import com.hnn.entity.User;
import com.hnn.service.IUserService;
import com.hnn.utils.SendSms;
import com.hnn.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    /**
     * 发送手机验证码*
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info(user.toString());

        //获取手机号
        String phone = user.getPhone();

        //判断手机号是否为空
        if(Strings.isNotEmpty(phone)){
            //再去生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            SendSms.sendMessage("瑞吉外卖","SMS_275445732",phone,code);

            //需要将生成的验证码保存到Session
            session.setAttribute(phone,code);
            return R.success("手机验证码短信发送成功");
        }

        //生成4位验证码

        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录*
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //将页面提交的验证码和session中保存的验证码比对
        if(codeInSession!=null && codeInSession.equals(code)){
            //如果能够比对成功，说明登陆成功

            //判断当前手机号对应的用户是否为新用户
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<User>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            //如果是新用户，就自动完成注册
            if(user == null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user.getId());
            return R.success("登陆成功");

        }

        return R.error("登陆失败");
    }

    /**
     * 退出登录*
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){

        //将当前用户id从session中移出
        session.removeAttribute("user");

        return R.success("退出登录成功");
    }

}
