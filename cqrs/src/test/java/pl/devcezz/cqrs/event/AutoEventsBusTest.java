package pl.devcezz.cqrs.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.devcezz.cqrs.exception.event.NoHandlerForEventException;
import pl.devcezz.cqrs.exception.event.NotImplementedEventHandlerInterfaceException;
import pl.devcezz.cqrs.exception.event.NotImplementedEventInterfaceException;
import pl.devcezz.tests.TestFiles;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutoEventsBusTest {

    @TempDir
    Path tempDir;

    @Test
    void should_event_handler_handle_event() {
        Path path = TestFiles.createTestFileInDir("auto_events_bus_test_%s" + System.currentTimeMillis(), tempDir);
        AutoEventsBus autoEventsBus = new AutoEventsBus(
                Set.of(
                        new FirstMailEventHandler(path),
                        new SecondMailEventHandler(path)
                )
        );

        autoEventsBus.publish(new MailEvent("Message"));

        assertThat(TestFiles.readFileLines(path)).containsExactly("Message", "Message");
    }

    @Test
    void should_fail_when_no_event_handler_for_event() {
        AutoEventsBus autoEventsBus = new AutoEventsBus(
                Set.of(new FirstMailEventHandler())
        );

        assertThatThrownBy(() -> autoEventsBus.publish(new ChatEvent()))
                .isInstanceOf(NoHandlerForEventException.class);
    }

    @Test
    void should_fail_when_set_event_handler_without_generic() {
        assertThatThrownBy(() -> new AutoEventsBus(Set.of(new EventHandlerWithoutGeneric())))
                .isInstanceOf(NotImplementedEventHandlerInterfaceException.class);
    }

    @Test
    void should_fail_when_set_event_handler_without_generic_event_implementation() {
        assertThatThrownBy(() -> new AutoEventsBus(Set.of(new EventHandlerForEventInterface())))
                .isInstanceOf(NotImplementedEventInterfaceException.class);
    }
}

class MailEvent implements Event {

    final String message;

    MailEvent(final String message) {
        this.message = message;
    }
}
class ChatEvent implements Event {}

class FirstMailEventHandler implements EventHandler<MailEvent>, Serializable {

    private Path path;

    FirstMailEventHandler() {
    }

    FirstMailEventHandler(final Path path) {
        this.path = path;
    }

    @Override
    public void handle(final MailEvent event) {
        Optional.ofNullable(path)
                .ifPresent(path -> TestFiles.writeMessageToFile(path, event.message));
    }
}
class SecondMailEventHandler implements EventHandler<MailEvent> {

    private final Path path;

    SecondMailEventHandler(final Path path) {
        this.path = path;
    }

    @Override
    public void handle(final MailEvent event) {
        Optional.ofNullable(path)
                .ifPresent(path -> TestFiles.writeMessageToFile(path, event.message));
    }
}
class EventHandlerWithoutGeneric implements EventHandler {
    @Override
    public void handle(final Event event) {

    }
}
class EventHandlerForEventInterface implements EventHandler<Event> {
    @Override
    public void handle(final Event event) {

    }
}