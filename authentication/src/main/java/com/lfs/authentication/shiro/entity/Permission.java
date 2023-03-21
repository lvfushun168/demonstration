package com.lfs.authentication.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "permission")
public class Permission {

    private Integer id;

    @JsonProperty(value = "parent_id")
    private Integer parentId;

    @JsonProperty(value = "permiss_Name")
    private String permissName;

    @JsonProperty(value = "permiss_nice")
    private String permissNice;

    @JsonProperty(value = "permiss_type")
    private String permissType;

    @JsonProperty(value = "permiss_uri")
    private String permissUri;

    @JsonProperty(value = "crt_user_id")
    private String crtUserId;

    @JsonProperty(value = "crt_user_id")
    private LocalDateTime crtTime;


}
