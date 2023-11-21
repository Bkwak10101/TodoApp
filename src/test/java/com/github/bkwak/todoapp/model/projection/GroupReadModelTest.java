package com.github.bkwak.todoapp.model.projection;

import com.github.bkwak.todoapp.model.Task;
import com.github.bkwak.todoapp.model.TaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class GroupReadModelTest {
    @Test
    @DisplayName("should create null deadline for group when no task deadline")
    void constructor_noDeadlines_createsNullDeadline() {
        //given
        var source = new TaskGroup();
        source.setDescription("lorem");
        source.setTasks(Set.of(new Task("ipsum", null)));

        //when
        var result = new GroupReadModel(source);

        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline", null);
    }

}