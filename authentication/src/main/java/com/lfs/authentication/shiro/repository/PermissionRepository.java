package com.lfs.authentication.shiro.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lfs.authentication.shiro.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionRepository extends BaseMapper<Permission> {

    @Select("<script> "+"select id,parent_id,permiss_name from   " +
            "(  " +
            "     SELECT   " +
            "      id  " +
            "      ,parent_id  " +
            "      ,permiss_name  " +
            "      ,@le:= IF (parent_id = 0 ,0,IF( LOCATE( CONCAT('|',parent_id,':'),@pathlevel)   > 0  ,SUBSTRING_INDEX( SUBSTRING_INDEX(@pathlevel,CONCAT('|',parent_id,':'),-1),'|',1) +1,@le+1) ) levels  " +
            "        " +
            "      ,@pathlevel:= CONCAT(@pathlevel,'|',id,':', @le ,'|') pathlevel  " +
            "        " +
            "      ,@pathnodes:= IF( parent_id =0,',0',CONCAT_WS(',',IF( LOCATE( CONCAT('|',parent_id,':'),@pathall) > 0  , SUBSTRING_INDEX( SUBSTRING_INDEX(@pathall,CONCAT('|',parent_id,':'),-1),'|',1),@pathnodes ) ,parent_id  ) )paths  " +
            "        " +
            "      ,@pathall:=CONCAT(@pathall,'|',id,':', @pathnodes ,'|') pathall   " +
            "        " +
            "    FROM  permission,   " +
            "    (SELECT @le:=0,@pathlevel:='', @pathall:='',@pathnodes:='') vv  " +
            "    ORDER BY  parent_id,id  " +
            "    ) src  " +
            " where LOCATE (CONCAT(',',#{id}),paths)   > 0  order by id "+"</script>")
    List<Permission> findAllNextPermission(@Param("id") Integer id);

}
