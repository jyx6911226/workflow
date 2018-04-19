package com.jyx.workflow;

import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作流测试五
 * 接收任务测试
 * ReceiveTask不是UserTask所以不能用TaskService操作
 *
 * 接收任务（ReceiveTask）即等待任务，接收任务是一个简单任务，它会等待对应消息的到达。当前，官方只实现

 了这个任务的java语义。 当流程达到接收任务，流程状态会保存到数据库中。在任务创建后，意味着流程会进入等

 待状态，直到引擎接收了一个特定的消息， 这会触发流程穿过接收任务继续执行。
 * */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReceiveTaskTest extends PluggableActivitiTestCase {

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
                .name("receiveTask")
                //添加资源文件方式一
                .addClasspathResource("processes/receiveTask.bpmn")
                .addClasspathResource("processes/receiveTask.png")
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
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("receiveTask",map);

        //执行对象id
        String executionId = pi1.getId();
        System.out.println("executionId:"+executionId);
        //executionId:82501
    }

    /**
     * 3.确认任务
     *  接收任务需要经过确认才可以继续执行
     * */
    @Test
    public void ensureExecution() {
        //查询正在执行的执行对象，不能用TaskService查询！！！
//        List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery().processDefinitionKey("receiveTask").list();
//        pis.forEach(item -> System.out.println(item));
//        runtimeService.signal("82501");
//        System.out.println("确认完成");
    }
}
