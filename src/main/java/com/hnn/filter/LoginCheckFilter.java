package com.hnn.filter;

import com.alibaba.fastjson.JSON;
import com.hnn.common.BaseContext;
import com.hnn.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*检查用户是否已登录*/
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        /*1.获取本次请求的URI*/
        String requestURI=request.getRequestURI();

        //定义不需要处理的请求路径
        String[] urls=new String[]{
                /*只拦截动态请求，看不到数据就可*/
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg",
                "/user/loginout"
        };
        /*2.判断本次请求是否需要处理*/
        boolean check=check(urls,requestURI);

        /*3.若不需要处理，则直接放行*/
        if(check){
            log.info("本次请求{}不需要处理",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        /*4-1.判断登陆状态，若已登录，则直接放行*/
        if(request.getSession().getAttribute("employee")!=null){

            Long empId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            log.info("用户已登录，用户id为{}，本次请求路径为：{}",request.getSession().getAttribute("employee"),request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        /*4-2.判断登陆状态，若已登录，则直接放行*/
        if(request.getSession().getAttribute("user")!=null){

            Long userId=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            log.info("用户已登录，用户id为{}，本次请求路径为：{}",request.getSession().getAttribute("user"),request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        /*5.用户未登录则返回未登录结果，通过输出流方式向页面展现数据*/
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录");

        log.info("拦截到请求：{}",request.getRequestURI());

        return;
    }

    //路径匹配，检查此次请求是否需要放行
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match=PATH_MATCHER.match(url,requestURI);
            //匹配上了就立马返回true
            if(match){
                return true;
            }
        }
        //没匹配上
        return false;
    }
}
