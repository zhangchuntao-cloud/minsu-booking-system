package com.code.web_homework.controller;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.common.Result;
import com.code.web_homework.config.SessionUtil;
import com.code.web_homework.dto.HouseQuery;
import com.code.web_homework.entity.House;
import com.code.web_homework.entity.User;
import com.code.web_homework.service.HouseCategoryService;
import com.code.web_homework.service.HouseService;
import com.code.web_homework.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 后台房源管理控制器。
 * <p>
 * 管理员可以管理全部房源并执行审核，房东只能管理自己名下的房源。
 */
@Controller
@RequestMapping("/admin/house")
public class AdminHouseController {

    private final HouseService houseService;
    private final HouseCategoryService categoryService;
    private final UserService userService;

    public AdminHouseController(HouseService houseService, HouseCategoryService categoryService,
                                UserService userService) {
        this.houseService = houseService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /** 房源列表 */
    @GetMapping("/list")
    public String list(HouseQuery query, HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        // 房东只能看到自己的房源，管理员可查看全部
        if (loginUser.isHost()) {
            query.setHostId(loginUser.getId());
        }
        query.setPageSize(10);
        PageResult<House> page = houseService.page(query);

        model.addAttribute("page", page);
        model.addAttribute("query", query);
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("menu", "house");
        model.addAttribute("loginUser", loginUser);
        return "admin/house-list";
    }

    /** 新增 / 编辑房源页面 */
    @GetMapping({"/edit", "/edit/{id}"})
    public String edit(@PathVariable(value = "id", required = false) Long id,
                       HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        House house;
        if (id == null) {
            house = new House();
            house.setMaxGuest(2);
            house.setBedroom(1);
            house.setLivingRoom(1);
            house.setBathroom(1);
            house.setBedCount(1);
            house.setArea(60);
        } else {
            house = houseService.getById(id);
            // 房东只能编辑自己的房源
            if (loginUser.isHost() && !house.getHostId().equals(loginUser.getId())) {
                throw new BusinessException("无权编辑他人发布的房源");
            }
        }
        model.addAttribute("house", house);
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("hosts", userService.listHosts());
        // 把图集拼成按行分隔的文本，供编辑页的文本域直接回显
        model.addAttribute("imageUrlsText", joinImages(house.getImages()));
        model.addAttribute("menu", "house");
        model.addAttribute("loginUser", loginUser);
        return "admin/house-edit";
    }

    /**
     * 保存房源（新增或修改）。
     * <p>
     * 页面中的图集以换行分隔的图片地址文本域提交，这里拆分后交给服务层处理。
     */
    @PostMapping("/save")
    public String save(House house,
                       @RequestParam(value = "imageUrls", required = false) String imageUrls,
                       @RequestParam(value = "hostIdInput", required = false) Long hostIdInput,
                       HttpServletRequest request, Model model) {
        User loginUser = SessionUtil.getLoginUser(request);
        List<String> images = splitImages(imageUrls);
        // 未显式指定封面时，取图集的第一张作为封面
        if ((house.getCoverImg() == null || house.getCoverImg().trim().isEmpty()) && !images.isEmpty()) {
            house.setCoverImg(images.get(0));
        }

        try {
            if (loginUser.isHost()) {
                // 房东新增房源时，归属固定为当前登录用户
                house.setHostId(loginUser.getId());
            } else if (hostIdInput != null) {
                house.setHostId(hostIdInput);
            }
            if (house.getHostId() == null) {
                throw new BusinessException("请选择房源所属房东");
            }

            if (house.getId() == null) {
                houseService.save(house, images, loginUser.getId());
            } else {
                houseService.update(house, images, loginUser.getId(), loginUser.isAdmin());
            }
            return "redirect:/admin/house/list";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("house", house);
            model.addAttribute("categories", categoryService.listAll());
            model.addAttribute("hosts", userService.listHosts());
            model.addAttribute("imageUrlsText", imageUrls == null ? "" : imageUrls);
            model.addAttribute("menu", "house");
            model.addAttribute("loginUser", loginUser);
            return "admin/house-edit";
        }
    }

    /**
     * 审核房源（AJAX）。
     *
     * @param status 1 通过上架 / 3 驳回
     */
    @PostMapping("/audit")
    @ResponseBody
    public Result<Void> audit(@RequestParam("id") Long id,
                              @RequestParam("status") Integer status,
                              @RequestParam(value = "auditRemark", required = false) String auditRemark) {
        houseService.audit(id, status, auditRemark);
        return Result.success(status == Constants.HOUSE_ONLINE ? "房源已审核通过并上架" : "房源已驳回", null);
    }

    /** 上架 / 下架（AJAX） */
    @PostMapping("/status")
    @ResponseBody
    public Result<Void> changeStatus(@RequestParam("id") Long id,
                                     @RequestParam("status") Integer status,
                                     HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        houseService.changeStatus(id, status, loginUser.getId());
        return Result.success(status == Constants.HOUSE_ONLINE ? "房源已上架" : "房源已下架", null);
    }

    /** 删除房源（AJAX） */
    @PostMapping("/delete")
    @ResponseBody
    public Result<Void> delete(@RequestParam("id") Long id, HttpServletRequest request) {
        User loginUser = SessionUtil.getLoginUser(request);
        House house = houseService.getRawById(id);
        if (house == null) {
            return Result.fail("房源不存在");
        }
        if (loginUser.isHost() && !house.getHostId().equals(loginUser.getId())) {
            return Result.fail("无权删除他人发布的房源");
        }
        houseService.delete(id);
        return Result.success("房源已删除", null);
    }

    /** 后台房源详情预览 */
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        model.addAttribute("house", houseService.getById(id));
        model.addAttribute("menu", "house");
        model.addAttribute("loginUser", SessionUtil.getLoginUser(request));
        return "admin/house-view";
    }

    /** 把文本域中的图片地址按行拆分成集合 */
    private List<String> splitImages(String text) {
        List<String> list = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return list;
        }
        for (String line : Arrays.asList(text.split("\\r?\\n"))) {
            String url = line.trim();
            if (!url.isEmpty()) {
                list.add(url);
            }
        }
        return list;
    }

    /** 把图集拼接成按行分隔的文本，供编辑页回显 */
    private String joinImages(List<com.code.web_homework.entity.HouseImage> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (com.code.web_homework.entity.HouseImage image : images) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(image.getImageUrl());
        }
        return sb.toString();
    }

    /** 供页面判断使用的状态常量 */
    @ModelAttribute("houseStatusOnline")
    public Integer houseStatusOnline() {
        return Constants.HOUSE_ONLINE;
    }
}
