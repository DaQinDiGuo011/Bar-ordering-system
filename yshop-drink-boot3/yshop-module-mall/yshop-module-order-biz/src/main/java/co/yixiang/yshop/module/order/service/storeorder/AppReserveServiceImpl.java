package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.module.order.controller.app.order.param.AppCancelParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppCreateReserveParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppTableDateParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppBarTableVo;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppReserveOrderVo;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.BarTableDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.ReserveOrderDO;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.BarTableMapper;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.ReserveOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Slf4j
@Service
@Validated
public class AppReserveServiceImpl implements AppReserveService{

    @Resource
    BarTableMapper barTableMapper;
    @Resource
    ReserveOrderMapper orderMapper;

    @Override
    public List<AppBarTableVo> getTableList(AppTableDateParam param) {
        List<BarTableDO> allTable = barTableMapper.selectAll();
        List<Long> reservedIds = barTableMapper.selectReservedTableId(param.getDate());
        List<AppBarTableVo> voList = new ArrayList<>();
        for(BarTableDO t : allTable){
            AppBarTableVo vo = new AppBarTableVo();
            vo.setId(t.getId());
            vo.setName(t.getTableName());
            if(reservedIds.contains(t.getId())){
                vo.setStatus(2);
            }else{
                vo.setStatus(1);
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public Map<String, Object> create(AppCreateReserveParam param) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "500");
        if(getLoginUserId() == null){
            result.put("msg", "请先登录账号");
            return result;
        }
        // 判断桌台是否当日已被占用
        List<Long> reservedIds = barTableMapper.selectReservedTableId(param.getDate());
        if(reservedIds.contains(param.getTableId())){
            result.put("msg", "该桌台当日已被预定");
        }else{
            BarTableDO table = barTableMapper.selectById(param.getTableId());
            if(table == null){
                result.put("msg", "桌台不存在");
            }else{
                ReserveOrderDO order = new ReserveOrderDO();
                order.setUserId(getLoginUserId());
                order.setTableId(param.getTableId());
                order.setTableName(table.getTableName());
                order.setReserveDate(param.getDate());
                order.setReserveTime(param.getTime());
                order.setName(param.getName());
                order.setPhone(param.getPhone());
                order.setPeopleNum(param.getPeopleNum());
                order.setRemark(param.getRemark());
                order.setStatus(1);
                orderMapper.insert(order);
                result.put("code", "200");
            }
        }
        return result;
    }

    @Override
    public List<AppReserveOrderVo> getMyOrder() {
        List<ReserveOrderDO> list = orderMapper.selectUserOrder(getLoginUserId());
        List<AppReserveOrderVo> result = list.stream().map(info -> {
            AppReserveOrderVo orderVo = new AppReserveOrderVo();
            orderVo.setId(info.getId());
            orderVo.setUserId(info.getUserId());
            orderVo.setTableId(info.getTableId());
            orderVo.setTableName(info.getTableName());
            orderVo.setReserveDate(info.getReserveDate());
            orderVo.setReserveTime(info.getReserveTime());
            orderVo.setName(info.getName());
            orderVo.setPhone(info.getPhone());
            orderVo.setPeopleNum(info.getPeopleNum());
            orderVo.setRemark(info.getRemark());
            orderVo.setStatus(info.getStatus()); //1待使用 2已取消
            orderVo.setCreateTime(info.getCreateTime());
            return orderVo;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public Map<String, Object> cancel(AppCancelParam param) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "500");
        ReserveOrderDO order = orderMapper.selectById(param.getId());
        if(order == null){
            result.put("msg", "订单不存在");
        }else if(!order.getUserId().equals(getLoginUserId())){
            result.put("msg", "无权操作");
        }else if(order.getStatus() != 1){
            result.put("msg", "只有待使用订单可以取消");
        }else{
            orderMapper.updateStatus(param.getId(),2);
            result.put("code", "200");
        }
        return result;
    }
}
