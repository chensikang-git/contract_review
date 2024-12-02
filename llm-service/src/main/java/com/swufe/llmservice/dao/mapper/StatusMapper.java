package com.swufe.llmservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swufe.llmservice.dao.enetty.DocxFileDO;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;

@Mapper
public interface StatusMapper extends BaseMapper<DocxFileDO>  {

}
