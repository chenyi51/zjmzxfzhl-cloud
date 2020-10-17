package com.zjmzxfzhl.modules.sys.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjmzxfzhl.common.core.Result;
import com.zjmzxfzhl.common.core.base.BaseController;
import com.zjmzxfzhl.common.core.base.UserInfo;
import com.zjmzxfzhl.common.core.exception.SysException;
import com.zjmzxfzhl.common.core.util.SecurityUtils;
import com.zjmzxfzhl.common.log.annotation.Log;
import com.zjmzxfzhl.common.security.annotation.Inner;
import com.zjmzxfzhl.modules.sys.entity.SysUser;
import com.zjmzxfzhl.modules.sys.entity.vo.SysPasswordForm;
import com.zjmzxfzhl.modules.sys.entity.vo.SysUserInfo;
import com.zjmzxfzhl.modules.sys.service.SysUserService;
import com.zjmzxfzhl.modules.sys.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 用户Controller
 *
 * @author 庄金明
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 自定义查询列表
     *
     * @param sysUser
     * @param current
     * @param size
     * @return
     */
    @PreAuthorize("@elp.single('sys:user:list')")
    @GetMapping(value = "/list")
    public Result list(SysUser sysUser, @RequestParam Integer current, @RequestParam Integer size) {
        IPage<SysUser> pageList = sysUserService.list(new Page<SysUser>(current, size), sysUser);
        return Result.ok(pageList);
    }

    /**
     * 公共选人查询
     *
     * @param sysUser
     * @param current
     * @param size
     * @return
     */
    @GetMapping(value = "/listSelectUser")
    public Result listSelectUser(SysUser sysUser, @RequestParam Integer current, @RequestParam Integer size) {
        IPage<SysUser> pageList = sysUserService.listSelectUser(new Page<SysUser>(current, size), sysUser);
        return Result.ok(pageList);
    }

    @PreAuthorize("@elp.single('sys:user:list')")
    @GetMapping(value = "/queryById")
    public Result queryById(@RequestParam String id) {
        SysUser sysUser = sysUserService.getById(id);
        return Result.ok(sysUser);
    }

    /**
     * @param sysUser
     * @return
     * @功能：新增
     */
    @Log(value = "新增用户")
    @PreAuthorize("@elp.single('sys:user:save')")
    @PostMapping(value = "/save")
    public Result save(@Valid @RequestBody SysUser sysUser) {
        sysUserService.saveSysUser(sysUser);
        return Result.ok();
    }

    /**
     * @param sysUser
     * @return
     * @功能：修改
     */
    @Log(value = "修改用户")
    @PreAuthorize("@elp.single('sys:user:update')")
    @PutMapping(value = "/update")
    public Result update(@Valid @RequestBody SysUser sysUser) {
        sysUserService.updateSysUser(sysUser);
        return Result.ok();
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除
     */
    @Log(value = "删除用户")
    @PreAuthorize("@elp.single('sys:user:delete')")
    @DeleteMapping(value = "/delete")
    public Result delete(@RequestParam String ids) {
        sysUserService.delete(ids);
        return Result.ok();
    }

    @Log(value = "获取用户信息")
    @GetMapping(value = "/getUserInfo")
    public Result getUserInfo(@RequestParam(required = false) String roleId, HttpServletRequest request) {
        SysUserInfo sysUserInfo = sysUserService.saveGetUserInfo(SecurityUtils.getUserId(), roleId);
        return Result.ok(sysUserInfo);
    }

    @Inner
    @GetMapping(value = "/info")
    public Result<UserInfo> info(@RequestParam String userId) {
        return ((UserInfoService) sysUserService).info(userId,null);
    }

    @PostMapping(value = "/updatePassword")
    public Result updatePassword(@RequestBody SysPasswordForm sysPasswordForm) {
        boolean success = sysUserService.updatePassword(sysPasswordForm);
        if (!success) {
            return Result.error("原密码错误");
        }
        return Result.ok();
    }

    @Log(value = "导出用户信息")
    @PreAuthorize("@elp.single('sys:user:export')")
    @GetMapping(value = "/export")
    public void export(SysUser sysUser, HttpServletResponse response) throws IOException {
        try {
            IPage<SysUser> page = sysUserService.list(null, sysUser);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=SysUserExport.xlsx");
            EasyExcel.write(response.getOutputStream(), SysUser.class).sheet("SysUser").doWrite(page.getRecords());
        } catch (Exception e) {
            throw new SysException("下载文件失败：" + e.getMessage());
        }
    }
}
