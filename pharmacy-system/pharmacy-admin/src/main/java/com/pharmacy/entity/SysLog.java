package com.pharmacy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog {
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField("user_id")
    private Integer userId;

    @TableField("username")
    private String username;

    @TableField("real_name")
    private String realName;

    @TableField("oper_module")
    private String operModule;

    @TableField("oper_type")
    private String operType;

    @TableField("oper_content")
    private String operContent;

    @TableField("oper_ip")
    private String operIp;

    @TableField("oper_time")
    private LocalDateTime operTime;

    @TableField("remark")
    private String remark;
}
