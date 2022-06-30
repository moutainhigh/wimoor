package com.wimoor.amazon.auth.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wimoor.amazon.auth.pojo.entity.AmazonGroup;
import com.wimoor.common.user.UserInfo;

public interface IAmazonGroupService  extends IService<AmazonGroup> {
	List<AmazonGroup> getGroupByUser(UserInfo user);
}
