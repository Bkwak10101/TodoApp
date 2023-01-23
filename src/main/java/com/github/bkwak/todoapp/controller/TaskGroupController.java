package com.github.bkwak.todoapp.controller;

import com.github.bkwak.todoapp.logic.TaskGroupService;
import com.github.bkwak.todoapp.model.Task;
import com.github.bkwak.todoapp.model.TaskRepository;
import com.github.bkwak.todoapp.model.projection.GroupReadModel;
import com.github.bkwak.todoapp.model.projection.GroupWriteModel;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class TaskGroupController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository repository;

    TaskGroupController(TaskGroupService service, TaskRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping()
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate) {
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @GetMapping()
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        return ResponseEntity.ok(service.readAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id) {
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

}
