package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.MD5Util;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.dto.RegisterForm;
import com.code.web_homework.dto.UserQuery;
import com.code.web_homework.entity.Permission;
import com.code.web_homework.entity.Role;
import com.code.web_homework.entity.User;
import com.code.web_homework.mapper.PermissionMapper;
import com.code.web_homework.mapper.RoleMapper;
import com.code.web_homework.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户业务逻辑层。
 * <p>
 * 负责登录校验、注册、资料维护等与用户相关的业务规则，
 * 数据库操作全部委托给 {@link UserMapper}。
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    /** 构造器注入：依赖关系明确，且便于单元测试时替换实现 */
    public UserService(UserMapper userMapper, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    /**
     * 登录校验。
     *
     * @param username 登录账号
     * @param password 明文密码
     * @return 校验通过的用户对象
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("请输入登录账号");
        }
        if (password == null || password.isEmpty()) {
            throw new BusinessException("请输入登录密码");
        }
        User user = userMapper.selectByUsername(username.trim());
        if (user == null) {
            throw new BusinessException("账号不存在，请检查后重试");
        }
        if (!MD5Util.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误，请重新输入");
        }
        if (user.getStatus() == null || user.getStatus() != Constants.USER_ENABLED) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }
        return user;
    }

    /**
     * 用户注册。
     */
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        if (userMapper.selectByUsername(form.getUsername()) != null) {
            throw new BusinessException("该账号已被注册，请更换后重试");
        }
        // 注册身份只允许是普通会员或房东，防止前端伪造管理员角色
        String roleCode = Constants.ROLE_HOST.equals(form.getRoleCode())
                ? Constants.ROLE_HOST : Constants.ROLE_USER;

        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(MD5Util.encrypt(form.getPassword()));
        user.setRealName(form.getRealName());
        user.setGender(form.getGender());
        user.setPhone(form.getPhone());
        user.setEmail(form.getEmail());
        user.setRoleCode(roleCode);
        user.setStatus(Constants.USER_ENABLED);
        // 新用户赠送 100 元新人礼包，用于体验余额支付
        user.setBalance(new BigDecimal("100.00"));
        user.setCreatedBy(0L);

        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    /** 根据主键查询用户 */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /** 根据账号查询用户 */
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    /**
     * 更新个人资料。
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(User user) {
        User exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 资料修改不允许变更账号、密码与角色，避免越权
        User update = new User();
        update.setId(user.getId());
        update.setRealName(user.getRealName());
        update.setGender(user.getGender());
        update.setBirthday(user.getBirthday());
        update.setPhone(user.getPhone());
        update.setEmail(user.getEmail());
        update.setIdCard(user.getIdCard());
        update.setModifyBy(user.getId());
        userMapper.update(update);
    }

    /**
     * 修改登录密码。
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!MD5Util.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于 6 位");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        userMapper.updatePassword(userId, MD5Util.encrypt(newPassword));
    }

    /**
     * 由管理员调整用户角色。
     * <p>
     * 管理员账号的角色不允许被改动，避免误操作导致系统失去管理员。
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(User user) {
        User exist = userMapper.selectById(user.getId());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        if (Constants.ROLE_ADMIN.equals(exist.getRoleCode())
                && !Constants.ROLE_ADMIN.equals(user.getRoleCode())) {
            throw new BusinessException("管理员账号的角色不允许修改");
        }
        User update = new User();
        update.setId(user.getId());
        update.setRoleCode(user.getRoleCode());
        update.setModifyBy(user.getModifyBy());
        userMapper.update(update);
    }

    /** 分页查询用户列表 */
    public PageResult<User> page(UserQuery query) {
        long total = userMapper.countByQuery(query);
        List<User> list = userMapper.selectByQuery(query);
        return new PageResult<>(list, total, query.getPageNum(), query.getPageSize());
    }

    /** 启用或禁用账号 */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (Constants.ROLE_ADMIN.equals(user.getRoleCode())) {
            throw new BusinessException("管理员账号不允许被禁用");
        }
        userMapper.updateStatus(id, status);
    }

    /** 重置密码为 123456 */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        userMapper.updatePassword(id, MD5Util.encrypt("123456"));
    }

    /** 删除用户 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (Constants.ROLE_ADMIN.equals(user.getRoleCode())) {
            throw new BusinessException("管理员账号不允许被删除");
        }
        userMapper.deleteById(id);
    }

    /** 查询全部房东 */
    public List<User> listHosts() {
        return userMapper.selectHosts();
    }

    /** 查询全部角色 */
    public List<Role> listRoles() {
        return roleMapper.selectAll();
    }

    /** 查询某角色拥有的权限，用于页面按权限显示菜单 */
    public List<Permission> listPermissions(String roleCode) {
        return permissionMapper.selectByRoleCode(roleCode);
    }

    /** 用户总数 */
    public int countAll() {
        return userMapper.countAll();
    }

    /** 按角色统计人数 */
    public int countByRole(String roleCode) {
        return userMapper.countByRoleCode(roleCode);
    }
}
