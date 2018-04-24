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
 * 工作流测试八
 * 包含网关测试
 * 包含网关可以看做是排他网关和并行网关的结合体。
 * 和排他网关一样，你可以在外出顺序流上定义条件，包含网关会解析它们。
 * 但是主要的区别是包含网关可以选择多于一条顺序流，这和并行网关一样。
 * */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InclusiveGatewayTest extends PluggableActivitiTestCase {
    @Resource
    private RuntimeService runtimeService;          //运行时服务
    @Resource
    private TaskService taskService;                //任务服务
    @Resource
    private RepositoryService repositoryService;    //流程定义和部署服务

    /**
     * 1.部署流程定义
     * */
    @Test
    public void deployProcess() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        Deployment deployment = deploymentBuilder
                .name("inclusiveGateWay")
                //添加资源文件方式一
                .addClasspathResource("processes/inclusiveGateway.bpmn")
                .addClasspathResource("processes/inclusiveGateway.png")
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
        map.put("level",1);
        map.put("money",10);
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("inclusiveGateWay",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:42501
    }

    /**
     * 2.查询待办的个人任务
     * 三个条件中满足两个，所以只能查出两个任务
     * */
    @Test
    public void queryTaskByAsignee() {
        List<Task> tasks = taskService.createTaskQuery()
                //.taskCandidateOrAssigned("张三")
                .processDefinitionKey("inclusiveGateWay")
                .list();
        //Task[id=145009, name=UserTask]
        //Task[id=145011, name=UserTask]
        tasks.forEach(
                item-> System.out.println(item)
        );
    }
}
