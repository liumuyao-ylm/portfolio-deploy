package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.LoginDTO;
import com.pharmacy.entity.User;
import com.pharmacy.service.UserService;
import com.pharmacy.util.AuthTokenUtil;
import com.pharmacy.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenUtil authTokenUtil;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO == null || !StringUtils.hasText(loginDTO.getUsername()) || !StringUtils.hasText(loginDTO.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userService.getOne(wrapper);

        if (user == null || !PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            return Result.error("用户已被禁用");
        }

        normalizeRole(user);
        user.setToken(authTokenUtil.createToken(user));
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/password-hint")
    public Result<String> getPasswordHint(@RequestParam String username) {
        if (!StringUtils.hasText(username)) {
            return Result.error("请先输入用户名");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username.trim());
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("未找到该用户");
        }

        String remark = user.getRemark();
        if (!StringUtils.hasText(remark)) {
            return Result.success("该账号暂未配置找回密码提示，请联系管理员处理。");
        }
        return Result.success(remark);
    }

    @PostMapping
    public Result<User> add(@RequestBody User user) {
        normalizeRole(user);
        if (!isCreatableRole(user)) {
            return Result.error(400, "新增用户角色只能是 admin 或 staff");
        }
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }
        boolean success = userService.save(user);
        if (success) {
            user.setPassword(null);
        }
        return success ? Result.success(user) : Result.error("添加失败");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean success = userService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping
    public Result<User> update(@RequestBody User user) {
        normalizeRole(user);
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }
        boolean success = userService.updateById(user);
        if (success) {
            user.setPassword(null);
        }
        return success ? Result.success(user) : Result.error("更新失败");
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("未找到数据");
    }

    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        wrapper.orderByAsc(User::getUserId);
        Page<User> resultPage = userService.page(page, wrapper);
        resultPage.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(resultPage);
    }

    private void normalizeRole(User user) {
        if (user == null || !StringUtils.hasText(user.getRole())) {
            return;
        }
        String role = user.getRole().trim();
        if ("ADMIN".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role) || "店长".equals(role) || "管理员".equals(role)) {
            user.setRole("admin");
        } else if ("DEMO".equalsIgnoreCase(role) || "demo".equalsIgnoreCase(role) || "演示".equals(role) || "游客".equals(role)) {
            user.setRole("demo");
        } else if ("STAFF".equalsIgnoreCase(role) || "staff".equalsIgnoreCase(role) || "店员".equals(role) || "营业员".equals(role)) {
            user.setRole("staff");
        } else {
            user.setRole(role);
        }
    }

    private boolean isCreatableRole(User user) {
        if (user == null || !StringUtils.hasText(user.getRole())) {
            return false;
        }
        return "admin".equals(user.getRole()) || "staff".equals(user.getRole());
    }
}
