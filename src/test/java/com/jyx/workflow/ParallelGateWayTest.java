package com.jyx.workflow;

import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流测试四
 * 并行网关测试
 * */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ParallelGateWayTest extends PluggableActivitiTestCase {
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
                .name("parallelGateWay")
                //添加资源文件方式一
                .addClasspathResource("processes/parallelGateWay.bpmn")
                .addClasspathResource("processes/parallelGateWay.png")
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
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("parallelGateWay",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:55001
    }

    /**
     * 2.查询待办的个人任务
     * */
    @Test
    public void queryTaskByAsignee() {
        List<Task> tasks = taskService.createTaskQuery()
                //.taskCandidateOrAssigned("付款人")
                .processDefinitionKey("parallelGateWay")
                .list();
        tasks.forEach(
                item-> System.out.println(item)
        );
        //Task[id=55007, name=付款]
        //Task[id=55010, name=发货]
        //Task[id=57502, name=收款]
        //Task[id=60002, name=收货]
        //Task[id=65003, name=关单]
    }
    /**
     * 4.完成每个任务操作
     * */
    @Test
    public void complateFirstStep() {
        //任务ID可以通过代办人名称和流程定义名称查询到
        //见方法3.queryTaskByAsignee
        String taskId = "65003";
        Map<String,Object> map = new HashMap<>();
        //map.put("money",10);
        //map.put("money",100);
        //map.put("money",1010);
        taskService.complete(taskId,map);
        System.out.println("任务完成");
    }
    /**
     * 5.查询待办事务
     *
     * */
    @Test
    public void queryTaskByAsignee2() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned("王五")
                .processDefinitionKey("parallelGateWay")
                .list();
        tasks.forEach(
                item-> System.out.println(item)
        );
    }
}
