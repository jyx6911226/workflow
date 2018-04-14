package com.jyx.workflow.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;


/**
 * 流程相关service
 * 流程定义不能修改，只能更新
 * */
/**
 * Process是指流程，即某一个业务流程
 * execution是指执行对象，即业务流程的某一个分支，当只有一个分支时，与流程实例相同
 * task是指执行的任务，即指行对象中的某一个节点
 * */
/**
 * ProcessDefinitionQuery为流程查询器，定义有封装好的查询方法用于查询流程定义
 */
@RestController
@RequestMapping("/activityService")
public interface ActivityConsumerService {

    /**
     * 部署流程定义
     * */
    @RequestMapping(value="/deployProcess",method= RequestMethod.GET)
    String deployProcess(String processFilePathBpmn,String processFilePathPng,String deploymentName);

    /**
     * 查询流程定义
     * */
    @RequestMapping(value="/queryProcess",method= RequestMethod.GET)
    Map<String,Object> queryProcess(String name);

    /**
     * 查询最新版本的流程定义
     * */
    @RequestMapping(value="/queryLastVersionProcess",method= RequestMethod.GET)
    Map<String,Object> queryLastVersionProcess(String name);
    /**
     * 删除流程定义部署
     * */
    @RequestMapping(value="/delDeployment",method= RequestMethod.GET)
    Map<String,Object> delDeployment(String deploymentId);

    /**
     * 获取流程图
     * */
    @RequestMapping(value="/queryProcessImage",method= RequestMethod.GET)
    Map<String,Object> queryProcessImage(String deploymentId) throws IOException;

    /**
     * 启动流程实例
     * */
    @RequestMapping(value="/startProcessInstance",method= RequestMethod.GET)
    String startProcessInstance(String key);

    /**
     * 流程变量processVariables
     * 1.用于传递业务参数
     * 2.用于决定流程的走向（分支）
     * 3.指定任务办理人
     * */
    /**
     * Local代表与任务taskId绑定
     * 使用runtimeService设置的流程变量是与executionId关联的变量，在这个流程分支内都可以获得这些变量
     * 使用taskService设置的流程变量是与taskId关联的变量，即当task完成时，其他的任务是获取不到这个变量的
     * taskService.complete(String.valueOf(taskId), variables); //注意：设置的是流程级别的变量，已测试
     * */
    @RequestMapping(value="/processVariables",method= RequestMethod.GET)
    Map<String,Object> processVariables(String executionId,String taskId,Map<String,Object> map);
}
