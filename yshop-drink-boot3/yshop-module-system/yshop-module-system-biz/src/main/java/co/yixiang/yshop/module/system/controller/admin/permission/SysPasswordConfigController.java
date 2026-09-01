package co.yixiang.yshop.module.system.controller.admin.permission;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigPageVO;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.service.permission.SysPasswordConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/passwordConfig")
@Tag(name = "密码密钥配置")
public class SysPasswordConfigController {

    @Resource
    private SysPasswordConfigService sysPasswordConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public CommonResult<PageResult<SysPasswordConfigVO>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(required = false) String passwordType) {

        SysPasswordConfigPageVO reqVO = new SysPasswordConfigPageVO();
        reqVO.setPageNo(pageNum);
        reqVO.setPageSize(pageSize);
        reqVO.setPasswordType(passwordType);
        return CommonResult.success(sysPasswordConfigService.selectPage(reqVO));
    }

    @GetMapping("/getValidInfoList")
    @Operation(summary = "单条详情")
    public CommonResult<List<SysPasswordConfigVO>> getValidInfoList(){
        return CommonResult.success(sysPasswordConfigService.getValidInfoList());
    }

    @PostMapping("/add")
    @Operation(summary = "新增")
    public CommonResult<Boolean> save(@RequestBody SysPasswordConfigVO entity){
        sysPasswordConfigService.addInfo(entity);
        return CommonResult.success(true);
    }

    @PostMapping("/update")
    @Operation(summary = "编辑")
    public CommonResult<Boolean> update(@RequestBody SysPasswordConfigVO entity){
        sysPasswordConfigService.updInfo(entity);
        return CommonResult.success(true);
    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "删除(逻辑删除)")
//    public boolean remove(@PathVariable Long id){
//        return sysPasswordConfigService.removeById(id);
//    }
//
//    @GetMapping("/getByType/{type}")
//    @Operation(summary = "根据密码类型获取有效配置")
//    public SysPasswordConfig getByType(@PathVariable String type){
//        return sysPasswordConfigService.getByType(type);
//    }
}
