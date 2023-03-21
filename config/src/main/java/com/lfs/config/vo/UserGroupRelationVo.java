package com.lfs.config.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserGroupRelationVo {

    private String name;

    private List<Map> detail;

}
