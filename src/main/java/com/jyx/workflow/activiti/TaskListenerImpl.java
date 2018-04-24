package com.jyx.workflow.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 任务代理人设置
 * 方式三：通过api通过代码设置代理人
 */
public class TaskListenerImpl implements TaskListener{
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("========================>notify");
        //可以指定个人和组任务
        delegateTask.setAssignee("张三");
    }
}
