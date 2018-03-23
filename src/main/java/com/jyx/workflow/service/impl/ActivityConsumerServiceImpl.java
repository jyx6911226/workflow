package com.jyx.workflow.service.impl;

import com.jyx.workflow.service.ActivityConsumerService;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("activityService")
public class ActivityConsumerServiceImpl implements ActivityConsumerService {

    @Resource
    private ProcessEngine processEngine;            //流程引擎
    @Resource
    private RuntimeService runtimeService;          //运行时服务
    @Resource
    private TaskService taskService;                //任务服务
    @Resource
    private RepositoryService repositoryService;    //依赖服务
    @Resource
    private IdentityService identityService;        //用户和组的管理
    @Resource
    private ManagementService managementService;    //日常维护服务
    @Resource
    private HistoryService historyService;          //历史记录服务

    @Override
    public boolean startActivityDemo() {
        System.out.println("method startActivityDemo begin....");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("apply","zhangsan");
        map.put("approve","lisi");
        //流程启动
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("leave",map);
        String processId = pi1.getId();
        String taskId = pi1.getTasks().get(0).getId();
        taskService.complete(taskId, map);//完成第一步申请

        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        String taskId2 = task.getId();
        map.put("pass", false);
        taskService.complete(taskId2, map);//驳回申请
        System.out.println("method startActivityDemo end....");
        return false;
    }
}
