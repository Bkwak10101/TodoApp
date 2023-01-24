package com.github.bkwak.todoapp.logic;

import com.github.bkwak.todoapp.model.Project;
import com.github.bkwak.todoapp.model.TaskGroup;
import com.github.bkwak.todoapp.model.TaskGroupRepository;
import com.github.bkwak.todoapp.model.TaskRepository;
import com.github.bkwak.todoapp.model.projection.GroupReadModel;
import com.github.bkwak.todoapp.model.projection.GroupWriteModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;
import java.util.List;

public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    public TaskGroupService(TaskGroupRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
        return createGroup(source, null);
    }

    GroupReadModel createGroup(final GroupWriteModel source, final Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Task group with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
