package com.wimoor.amazon.auth.pojo.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wimoor.amazon.auth.service.IAmzAuthApiTimelimitService;
import com.wimoor.common.GeneralUtil;
import com.wimoor.common.pojo.entity.BaseEntity;
import com.wimoor.util.SpringUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 新版本SPI-API使用
 * </p>
 *
 * @author wimoor team
 * @since 2022-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_amz_auth_api_timelimit")
@ApiModel(value="AmzAuthApiTimelimit对象", description="新版本SPI-API使用")
public class AmzAuthApiTimelimit extends BaseEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "授权ID，等同于SellerId")
    private BigInteger amazonauthid;

    @ApiModelProperty(value = "API的名字")
    private String apiname;

    @ApiModelProperty(value = "是否有nexttoken")
    private String nexttoken;

    @ApiModelProperty(value = "API调用的开始时间")
    private Date startTime;

    @ApiModelProperty(value = "API调用的结束时间")
    private Date endTime;

    @ApiModelProperty(value = "本次调用的页数")
    private Integer pages;

    @ApiModelProperty(value = "下一次的恢复时常")
    private Double restore;

    @ApiModelProperty(value = "最后更新时间")
    private Date lastuptime;

    @ApiModelProperty(value = "异常log")
    private String log;

    
	public boolean apiNotRateLimit() {
		// TODO Auto-generated method stub
		if(restore==null||GeneralUtil.distanceOfSecond(lastuptime, new Date())*restore>1) {
			return true;
		}else {
			return false;
		}
	}

}
