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
 * 工作流测试二
 * 连线测试
 * */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WiredProcessTest extends PluggableActivitiTestCase {

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
    //@org.activiti.engine.test.Deployment(resources = {"processes/wired.bpmn","processes/wired.png"})
    public void deployProcess() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        Deployment deployment = deploymentBuilder
                .name("wired")
                //添加资源文件方式一
                .addClasspathResource("processes/wired.bpmn")
                .addClasspathResource("processes/wired.png")
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
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("wired",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:27501
    }

    /**
     * 2.查询待办的个人任务
     * */
    @Test
    public void queryTaskByAsignee() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned("张三")
                .processDefinitionKey("wired")
                .list();
        tasks.forEach(
                item-> System.out.println(item)
        );
    }
    /**
     * 4.完成组长审批操作，importent分别设置为true及false
     * */
    @Test
    public void complateFirstStep() {
        //任务ID可以通过代办人名称和流程定义名称查询到
        //见方法3.queryTaskByAsignee
        String taskId = "35004";
        Map<String,Object> map = new HashMap<>();
        //map.put("importent",true);
        map.put("importent",false);
        taskService.complete(taskId,map);
        System.out.println("任务完成");
    }
    /**
     * 5.查询待办事务
     * 如果importent为true则需要经理（李四）审批，如果
     * importent为false
     * */
    @Test
    public void queryTaskByAsignee2() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned("李四")
                .processDefinitionKey("wired")
                .list();
        tasks.forEach(
                item-> System.out.println(item)
        );
    }
}
