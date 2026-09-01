package co.yixiang.yshop.module.order.dal.mysql.storeorder;

import co.yixiang.yshop.module.order.dal.dataobject.storeorder.WineStoreDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WineStoreMapper extends BaseMapper<WineStoreDO> {

    /**
     * 分页查询数据列表（仅查询记录，不查总数）
     */
    List<WineStoreDO> selectPageList(@Param("ew") LambdaQueryWrapper<WineStoreDO> wrapper,
                                     @Param("offset") int offset,
                                     @Param("size") int size);

    /**
     * 查询符合条件总条数
     */
    Long selectCount(@Param("ew") LambdaQueryWrapper<WineStoreDO> wrapper);
}
