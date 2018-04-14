package com.jyx.workflow.service.impl;

import com.jyx.workflow.service.ActivityConsumerService;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
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
    private RepositoryService repositoryService;    //流程定义和部署服务
    @Resource
    private IdentityService identityService;        //用户和组的管理
    @Resource
    private ManagementService managementService;    //日常维护服务
    @Resource
    private HistoryService historyService;          //历史记录服务

    /**
     * 部署流程定义
     * */
    @Override
    public String deployProcess(String processFilePathBpmn,String processFilePathPng,String deploymentName) {
        System.out.println("deployProcess=================>");
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        Deployment deployment = deploymentBuilder
                .name(deploymentName)
                //添加资源文件方式一
                .addClasspathResource(processFilePathBpmn)
                .addClasspathResource(processFilePathPng)
                //添加资源文件方式二
                //以zip包的形式添加资源文件（必须为zip格式）
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(processFilePath)))
                .deploy();
        System.out.println("流程id："+ deployment.getId()+"   流程名称："+deployment.getName());
        return "部署成功";
    }

    @Override
    public Map<String, Object> queryProcess(String name) {
        //System.out.println("name=========================>"+name);
        Map<String,Object> map = new HashMap<>();
        //流程定义查询
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //添加查询条件
        //
        List<ProcessDefinition> processDefinitionQueryList = processDefinitionQuery.processDefinitionName(name).list();
        for (ProcessDefinition processDefinition : processDefinitionQueryList) {
            System.out.println(processDefinition.getId());
            System.out.println(processDefinition.getName());
            System.out.println("------------------------------");
        }

        map.put("list",processDefinitionQueryList);

        return map;
    }

    @Override
    public Map<String, Object> queryLastVersionProcess(String name) {
        Map<String,Object> map = new HashMap<>();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionName(name).latestVersion().singleResult();
        System.out.println(processDefinition.getName()+"   "+processDefinition.getVersion());
        return map;
    }

    @Override
    public Map<String, Object> delDeployment(String deploymentId) {
        Map<String,Object> map = new HashMap<>();
        //不带级联删除，只能删除没有启动的流程，如果启动就会抛出异常
        repositoryService.deleteDeployment(deploymentId);
        //级联删除，会删除流程部署，同时也会删除已经启动的流程
        //repositoryService.deleteDeployment(deploymentId,true);
        map.put("msg","删除成功");
        return map;
    }

    @Override
    public Map<String, Object> queryProcessImage(String deploymentId) throws IOException {
        Map<String,Object> map = new HashMap<>();
        List<String> list = repositoryService.getDeploymentResourceNames(deploymentId);
        InputStream inputStream = null;
        File img = null;
        for (String name: list) {
            if(name.endsWith(".png")) {
                inputStream = repositoryService.getResourceAsStream(deploymentId,name);
                img = new File("F:/"+name);
                OutputStream os = new FileOutputStream(img);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                inputStream.close();
                break;
            }
        }

        return map;
    }

    @Override
    public String startProcessInstance(String key) {

        Map<String,Object> map = new HashMap<String,Object>();
        //流程启动
        //map用于设置流程变量
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey(key,map);
        //流程id
        String processId = pi1.getId();
        System.out.println("流程id:"+processId);
        return "流程启动成功";
    }

    @Override
    public Map<String,Object> processVariables(String executionId,String taskId,Map<String,Object> map) {

        /**
         * 设置流程变量
         * executionId指的是执行对象ID
         * taskId指的是任务ID
         * */
        //runtimeService.setVariables(executionId,map);
        taskService.setVariables(taskId,map);

        //taskService.setVariablesLocal(taskId,map);//Local代表与任务taskId绑定,不加Local相当于流程实例变量，加上Local相当于task变量
        //启动任务时设置流程变量
        //runtimeService.startProcessInstanceByKey(key,map);
        //完成任务时设置流程变量
        //taskService.complete(taskId,map);

        //Map<String,Object> variables = runtimeService.getVariables(executionId);
        Map<String,Object> variables = taskService.getVariables(taskId);

        for(Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        //runtimeService.getVariablesLocal(executionId);
        //taskService.getVariablesLocal(taskId);
        return map;
    }
}
