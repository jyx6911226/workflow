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
 * 工作流测试七
 * 个人任务分配
 * 方式一：直接指定代理人（在流程图中直接指定，不再演示）
 * 方式二：通过流程变量设置代理人(在流程图的任务中设置代理人assignee)
 * 方式三：通过api通过代码设置代理人(见TaskListenerImpl)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonalTaskTest extends PluggableActivitiTestCase {

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
                .name("personalTask2")
                //方式二
//              .addClasspathResource("processes/personalTask.bpmn")
//              .addClasspathResource("processes/personalTask.png")
                //方式三
                .addClasspathResource("processes/personalTask2.bpmn")
                .addClasspathResource("processes/personalTask2.png")
                .deploy();
        System.out.println("流程id："+ deployment.getId()+"   流程名称："+deployment.getName());
    }

    /**
     * 2.启动
     * */
    @Test
    public void startProcess() {
        Map<String,Object> map = new HashMap<String,Object>();
        //方式二：通过流程变量设置代理人
        //map.put("assigneeName","张三");
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("personalTask2",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:120001
    }

    /**
     * 3.查询待办的个人任务
     * */
    @Test
    public void queryTaskByAsignee() {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned("张三")
                .processDefinitionKey("personalTask2")
                .list();
        tasks.forEach(
                item-> System.out.println(item)
        );
        //Task[id=120005, name=UserTask]
    }

    /**
     * 4.将个人任务从一个人（张三）分配给另一个人（李四）
     *
     * */
    @Test
    public void giveTask2Others() {
        taskService.setAssignee("135004","李四");
    }
}
