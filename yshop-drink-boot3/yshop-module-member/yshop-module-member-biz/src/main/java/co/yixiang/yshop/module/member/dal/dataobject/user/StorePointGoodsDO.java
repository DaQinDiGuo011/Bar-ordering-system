package co.yixiang.yshop.module.member.dal.dataobject.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePointGoodsDO {

    private Integer id;
    private String name;
    private String image;
    private Integer needPoint;
    private Integer stock;
    private Integer status;
    private LocalDateTime createdAt;

}
