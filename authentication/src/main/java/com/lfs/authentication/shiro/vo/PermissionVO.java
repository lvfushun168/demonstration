package com.lfs.authentication.shiro.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PermissionVO {
    private Integer id;

    private Integer parentId;

    private String permissName;

    private String permissNice;

    private String permissType;

    private String permissUri;

    private LocalDateTime crtTime;

}
