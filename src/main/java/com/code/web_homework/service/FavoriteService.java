package com.code.web_homework.service;

import com.code.web_homework.entity.Favorite;
import com.code.web_homework.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房源收藏业务逻辑层。
 */
@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * 切换收藏状态。
     * <p>
     * 已收藏则取消，未收藏则新增，前端只需调用一次即可完成「收藏 / 取消收藏」。
     *
     * @return true 表示本次操作后为已收藏状态
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggle(Long userId, Long houseId) {
        int count = favoriteMapper.countByUserAndHouse(userId, houseId);
        if (count > 0) {
            favoriteMapper.deleteByUserAndHouse(userId, houseId);
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setHouseId(houseId);
        favoriteMapper.insert(favorite);
        return true;
    }

    /** 查询某用户的收藏列表 */
    public List<Favorite> listByUser(Long userId) {
        return favoriteMapper.selectByUserId(userId);
    }

    /** 判断用户是否已收藏某房源 */
    public boolean isFavorited(Long userId, Long houseId) {
        if (userId == null) {
            return false;
        }
        return favoriteMapper.countByUserAndHouse(userId, houseId) > 0;
    }

    /** 收藏总数 */
    public int countAll() {
        return favoriteMapper.countAll();
    }
}
