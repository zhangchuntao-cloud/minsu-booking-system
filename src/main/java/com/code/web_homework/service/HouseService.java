package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.common.Constants;
import com.code.web_homework.common.PageResult;
import com.code.web_homework.dto.HouseQuery;
import com.code.web_homework.entity.House;
import com.code.web_homework.entity.HouseImage;
import com.code.web_homework.mapper.HouseImageMapper;
import com.code.web_homework.mapper.HouseMapper;
import com.code.web_homework.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 房源业务逻辑层。
 */
@Service
public class HouseService {

    private final HouseMapper houseMapper;
    private final HouseImageMapper houseImageMapper;
    private final ReviewMapper reviewMapper;

    public HouseService(HouseMapper houseMapper, HouseImageMapper houseImageMapper, ReviewMapper reviewMapper) {
        this.houseMapper = houseMapper;
        this.houseImageMapper = houseImageMapper;
        this.reviewMapper = reviewMapper;
    }

    /**
     * 查询房源详情，同时加载图集与评价数量。
     */
    public House getById(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在或已被删除");
        }
        house.setImages(houseImageMapper.selectByHouseId(id));
        return house;
    }

    /** 查询房源基本信息，不做存在性校验（用于下单等场景自行判断） */
    public House getRawById(Long id) {
        return houseMapper.selectById(id);
    }

    /** 分页查询房源列表 */
    public PageResult<House> page(HouseQuery query) {
        long total = houseMapper.countByQuery(query);
        List<House> list = houseMapper.selectByQuery(query);
        return new PageResult<>(list, total, query.getPageNum(), query.getPageSize());
    }

    /** 热门房源 */
    public List<House> listHot(int limit) {
        return houseMapper.selectHotHouses(limit);
    }

    /** 最新房源 */
    public List<House> listLatest(int limit) {
        return houseMapper.selectLatestHouses(limit);
    }

    /** 某房东名下的房源 */
    public List<House> listByHost(Long hostId) {
        return houseMapper.selectByHostId(hostId);
    }

    /**
     * 新增房源。
     * <p>
     * 房东提交的房源默认进入「待审核」状态，由管理员审核通过后才对外展示，
     * 这是平台内容质量的第一道关口。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(House house, List<String> imageUrls, Long operatorId) {
        validate(house);
        house.setHouseNo(generateHouseNo());
        house.setScore(new BigDecimal("5.0"));
        house.setViewCount(0);
        house.setOrderCount(0);
        house.setStatus(Constants.HOUSE_PENDING);
        house.setCreatedBy(operatorId);
        houseMapper.insert(house);

        saveImages(house.getId(), imageUrls);
        return house.getId();
    }

    /**
     * 修改房源。管理员修改后直接生效，房东修改后重新进入待审核。
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(House house, List<String> imageUrls, Long operatorId, boolean isAdmin) {
        House exist = houseMapper.selectById(house.getId());
        if (exist == null) {
            throw new BusinessException("房源不存在");
        }
        validate(house);
        house.setModifyBy(operatorId);
        // 房东修改房源信息后需要重新审核，管理员修改则保持原状态
        if (!isAdmin) {
            house.setStatus(Constants.HOUSE_PENDING);
            house.setAuditRemark("房源信息已修改，等待重新审核");
        }
        houseMapper.update(house);

        // 图片有变化时整体覆盖：先删除旧图再批量插入
        if (imageUrls != null && !imageUrls.isEmpty()) {
            houseImageMapper.deleteByHouseId(house.getId());
            saveImages(house.getId(), imageUrls);
        }
    }

    /**
     * 管理员审核房源。
     *
     * @param status      审核结果：1 通过上架 / 3 驳回
     * @param auditRemark 审核意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer status, String auditRemark) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        if (status != Constants.HOUSE_ONLINE && status != Constants.HOUSE_REJECTED) {
            throw new BusinessException("审核结果不合法");
        }
        if (status == Constants.HOUSE_REJECTED && (auditRemark == null || auditRemark.trim().isEmpty())) {
            throw new BusinessException("驳回房源时必须填写审核意见");
        }
        houseMapper.updateStatus(id, status, auditRemark);
    }

    /** 上架或下架房源 */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status, Long operatorId) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        houseMapper.updateStatus(id, status, house.getAuditRemark());
    }

    /** 删除房源，同时级联删除图片记录 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房源不存在");
        }
        houseImageMapper.deleteByHouseId(id);
        houseMapper.deleteById(id);
    }

    /** 浏览次数加一 */
    public void increaseView(Long id) {
        houseMapper.increaseViewCount(id);
    }

    /**
     * 依据评价数据重算房源综合评分。
     * <p>
     * 没有评价时保持默认满分 5.0，避免新上架房源显示为 0 分。
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshScore(Long houseId) {
        BigDecimal avg = reviewMapper.avgScoreByHouseId(houseId);
        House house = new House();
        house.setId(houseId);
        house.setScore(avg);
        house.setModifyBy(0L);
        houseMapper.update(house);
    }

    /** 房源总数 */
    public int countAll() {
        return houseMapper.countAll();
    }

    /** 按状态统计房源数量 */
    public int countByStatus(Integer status) {
        return houseMapper.countByStatus(status);
    }

    /* ==================== 私有辅助方法 ==================== */

    /** 保存房源图集 */
    private void saveImages(Long houseId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<HouseImage> images = new ArrayList<>();
        int sort = 1;
        for (String url : imageUrls) {
            if (url == null || url.trim().isEmpty()) {
                continue;
            }
            HouseImage image = new HouseImage();
            image.setHouseId(houseId);
            image.setImageUrl(url.trim());
            image.setImageName("房源图片" + sort);
            image.setSort(sort++);
            images.add(image);
        }
        if (!images.isEmpty()) {
            houseImageMapper.insertBatch(images);
        }
    }

    /** 生成房源编号，格式 MS + 年月日 + 4 位随机数 */
    private String generateHouseNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return Constants.HOUSE_NO_PREFIX + date + random;
    }

    /** 房源字段合法性校验 */
    private void validate(House house) {
        if (house.getTitle() == null || house.getTitle().trim().isEmpty()) {
            throw new BusinessException("请填写房源标题");
        }
        if (house.getTitle().length() > 60) {
            throw new BusinessException("房源标题不能超过 60 个字");
        }
        if (house.getPrice() == null || house.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("房源价格必须大于 0");
        }
        if (house.getCategoryId() == null) {
            throw new BusinessException("请选择房源分类");
        }
        if (house.getMaxGuest() == null || house.getMaxGuest() < 1) {
            throw new BusinessException("最大入住人数至少为 1 人");
        }
        if (house.getArea() == null || house.getArea() <= 0) {
            throw new BusinessException("请填写有效的房源面积");
        }
    }
}
