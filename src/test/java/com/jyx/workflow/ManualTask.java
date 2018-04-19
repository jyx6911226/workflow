package com.jyx.workflow;

import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流测试六
 * 手动任务，手动任务仅仅是一个节点，证明此步骤执行过，任务不需要单独完成或操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ManualTask extends PluggableActivitiTestCase {
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
     * 1.部署流程定义
     * */
    @Test
    public void deployProcess() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        Deployment deployment = deploymentBuilder
                .name("manualTask")
                //添加资源文件方式一
                .addClasspathResource("processes/manualTask.bpmn")
                .addClasspathResource("processes/manualTask.png")
                //添加资源文件方式二
                //以zip包的形式添加资源文件（必须为zip格式）
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(processFilePath)))
                .deploy();
        System.out.println("流程id："+ deployment.getId()+"   流程名称："+deployment.getName());
    }

    /**
     * 2.启动
     * */
    @Test
    public void startProcess() {
        Map<String,Object> map = new HashMap<String,Object>();
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("manualTask",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:92501
    }

    /**
     * 3.查询任务
     * 手动任务查询不到
     * */
    @Test
    public void ensureExecution() {
        List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery().processDefinitionKey("receiveTask").list();
        pis.forEach(item -> System.out.println(item));
    }

}

